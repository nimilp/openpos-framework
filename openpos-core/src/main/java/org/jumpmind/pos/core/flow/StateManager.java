/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.StateConfig;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.screen.AbstractScreen;
import org.jumpmind.pos.core.service.IScreenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component()
@org.springframework.context.annotation.Scope("prototype")
public class StateManager implements IStateManager {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final Logger loggerGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");

    @Autowired
    private IScreenService screenService;
    
    @Autowired
    private ActionHandlerImpl actionHandler;

    @Autowired
    private Injector injector;

    private String appId;
    private String nodeId;
    private Scope scope = new Scope();
    private FlowConfig flowConfig;
    private IState currentState;

    private ObjectMapper jsonMapper = new ObjectMapper();

    @PostConstruct
    public void postConstruct() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(org.jumpmind.pos.core.model.annotations.Screen.class));

        for (BeanDefinition bd : scanner.findCandidateComponents("org.jumpmind.pos")) {
            logger.info("" + bd);
        }
    }

    public void init(String appId, String nodeId) {
        this.appId = appId;
        this.nodeId = nodeId;
        transitionTo(null, flowConfig.getInitialState());
    }

    protected void transitionTo(Action action, StateConfig stateConfig) {
        IState newState = buildState(stateConfig);
        transitionTo(action, newState);
    }

    protected void transitionTo(Action action, IState newState) {
        logStateTransition(currentState, newState);
        Map<String, ScopeValue> extraScope = new HashMap<>();
        extraScope.put("stateManager", new ScopeValue(this));
        injector.performInjections(newState, scope, extraScope);
        currentState = newState;
        currentState.arrive(action);
    }

    protected IState buildState(StateConfig stateConfig) {
        IState state;
        try {
            state = stateConfig.getStateClass().newInstance();
        } catch (Exception ex) {
            throw new FlowException("Failed to instantiate state " + stateConfig.getStateName() + " class " + stateConfig.getStateClass(),
                    ex);
        }
        return state;
    }

    @Override
    public IState getCurrentState() {
        return currentState;
    }

    @Override
    public AbstractScreen getLastScreen() {
        return screenService.getLastScreen(appId, nodeId);
    }

    @Override
    public void refreshScreen() {
        showScreen(getLastScreen());
    }

    // Could come from a UI or a running state..
    @Override
    public void doAction(String actionName) {
        doAction(actionName, null);
    }

    @Override
    public void doAction(String actionName, Map<String, String> params) {
        // TODO this needs to be put on the action event queue and processed
        // on main run loop thread.
        Action action = new Action(actionName, null, params);
        doAction(action);
    }

    @Override
    public void doAction(Action action) {
        StateConfig stateConfig = flowConfig.getStateConfig(currentState);
        String newStateName = stateConfig.getActionToStateMapping().get(action.getName());
        if (newStateName != null) {
            StateConfig newStateConfig = flowConfig.getStateConfig(newStateName);
            if (newStateConfig != null) {
                transitionTo(action, newStateConfig);
            } else {
                throw new FlowException("No State found for name " + newStateName);
            }
        } else {
            IState savedCurrentState = currentState;
            Form form = screenService.deserializeScreenPayload(appId, nodeId, action);

            boolean handled = actionHandler.handleAction(currentState, action, form);
            if (handled) {
                if (savedCurrentState == currentState) {
                    // state did not change, reassert the current state.
                    // transitionTo(currentState);
                }
            } else {
                logger.warn("Unexpected action {}", action);
            }
        }
    }

    @Override
    public void endConversation() {
        scope.clearConversationScope();
        transitionTo(null, flowConfig.getInitialState());
    }

    @Override
    public void endSession() {
        scope.clearSessionScope();
    }

    @Override
    public ScopeValue getScopeValue(String name) {
        return scope.resolve(name);
    }

    @Override
    public void setNodeScope(String name, Object value) {
        scope.setNodeScope(name, value);
    }

    @Override
    public void setSessionScope(String name, Object value) {
        scope.setSessionScope(name, value);
    }

    public void setConversationScope(String name, Object value) {
        scope.setConversationScope(name, value);
    }

    public FlowConfig getFlowConfig() {
        return flowConfig;
    }

    public void setFlowConfig(FlowConfig flowConfig) {
        this.flowConfig = flowConfig;
    }

    @Override
    public void showScreen(AbstractScreen screen) {
        if (this.currentState != null && this.currentState instanceof IScreenInterceptor) {
            screen = ((IScreenInterceptor)this.currentState).intercept(screen);            
        }
        screenService.showScreen(appId, nodeId, screen);
    }

    public String toJSONPretty(Object o) {
        try {
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            logger.warn("Failed to format object to json", ex);
            return String.valueOf(o);
        }
    }

    @Override
    public String getNodeId() {
        return nodeId;
    }
    
    @Override
    public String getAppId() {
        return appId;
    }
    
    protected void logStateTransition(IState oldState, IState newState) {
        if (currentState == newState) {
            return;
        }
        if (loggerGraphical.isInfoEnabled()) {            
            String oldStateName = oldState != null ? oldState.getClass().getSimpleName() : "<no state>";
            String newStateName = newState.getClass().getSimpleName();
            int box1Width = Math.max(oldStateName.length(), 20);
            int box2Width = Math.max(newStateName.length(), 20);
            
            StringBuilder buff = new StringBuilder(256);
            
            int LINE_COUNT = 5;
            for (int i = 0; i < LINE_COUNT; i++) {
                switch (i) {
                    case 0:
                        buff.append(drawTop(box1Width, box2Width));
                        break;
                    case 1:
                    case 3:
                        buff.append(drawFillerLine(box1Width, box2Width));
                        break;
                    case 2:
                        buff.append(drawTitleLine(box1Width, box2Width, oldStateName, newStateName));
                        break;                    
                    case 4:
                        buff.append(drawBottom(box1Width, box2Width));
                        break;                    
                        
                }
            }
            
            logger.info("Transition from " + currentState + " to " + newState + "\n" + buff.toString());
        } else {
            logger.info("Transition from " + currentState + " to " + newState);
        }
    }

    final int SPACES_BETWEWEN = 10;

    protected String drawTop(int box1Width, int box2Width) {
        StringBuilder buff = new StringBuilder();
        
        buff.append("┌").append(StringUtils.repeat('─', box1Width-2)).append("┐");
        buff.append(StringUtils.repeat(' ', SPACES_BETWEWEN));
        buff.append("┌").append(StringUtils.repeat('─', box2Width-2)).append("┐");
        buff.append("\r\n");
        return buff.toString();
    }
    
    protected String drawFillerLine(int box1Width, int box2Width) {
        StringBuilder buff = new StringBuilder();
        
        buff.append("│").append(StringUtils.repeat(' ', box1Width-2)).append("│");
        buff.append(StringUtils.repeat(' ', SPACES_BETWEWEN));
        buff.append("│").append(StringUtils.repeat(' ', box2Width-2)).append("│");
        buff.append("\r\n");
        return buff.toString();
    }
    
    protected String drawTitleLine(int box1Width, int box2Width, String oldStateName, String newStateName) {
        StringBuilder buff = new StringBuilder();
        buff.append("│").append(StringUtils.center(oldStateName, box1Width-2)).append("│");
        buff.append(" ───────> ");
        buff.append("│").append(StringUtils.center(newStateName, box2Width-2)).append("│");
        buff.append("\r\n");
        return buff.toString();
    }
    
    protected String drawBottom(int box1Width, int box2Width) {
        StringBuilder buff = new StringBuilder();
        
        buff.append("└").append(StringUtils.repeat('─', box1Width-2)).append("┘");
        buff.append(StringUtils.repeat(' ', SPACES_BETWEWEN));
        buff.append("└").append(StringUtils.repeat('─', box2Width-2)).append("┘");
        buff.append("\r\n");
        return buff.toString();
    }    



    // TODO
    //@Override
//    public ITranslationManager getTranslationManager() {
//        return translationManager;
//    }

}
