package org.jumpmind.pos.core.service;

import java.util.Arrays;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerFactory;
import org.jumpmind.pos.core.screen.DialogProperties;
import org.jumpmind.pos.core.screen.DialogScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class SessionSubscribedListener implements ApplicationListener<SessionSubscribeEvent> {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    IStateManagerFactory stateManagerFactory;

    @Autowired
    IMessageService messageService;
    
    @Autowired
    SessionConnectListener sessionAuthTracker;
    
    @Value("${openpos.incompatible.version.message:The compatibility version of the client does not match the server}")
    String incompatibleVersionMessage; 

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        Message<?> msg = event.getMessage();
        String sessionId = (String) msg.getHeaders().get("simpSessionId");
        String topicName = (String) msg.getHeaders().get("simpDestination");
        String nodeId = topicName.substring(topicName.indexOf("/node/") + "/node/".length());
        String appId = topicName.substring(topicName.indexOf("/app/") + "/app/".length(), topicName.indexOf("/node/"));
        logger.info("session subscribing: {}", sessionId);
        try {
            logger.info("subscribing to {}", topicName);
            IStateManager stateManager = stateManagerFactory.retrieve(appId, nodeId);
            if (stateManager == null) {
                stateManager = stateManagerFactory.create(appId, nodeId);
            }

            stateManager.setSessionAuthenticated(sessionId, sessionAuthTracker.isSessionAuthenticated(sessionId));
            stateManager.setSessionCompatible(sessionId, sessionAuthTracker.isSessionCompatible(sessionId));

            if (!stateManager.isSessionAuthenticated(sessionId)) {
                DialogScreen errorDialog = new DialogScreen();
                errorDialog.asDialog(new DialogProperties(false));
                errorDialog.setIcon("error");
                errorDialog.setTitle("Failed Authentication");
                errorDialog.setMessage(Arrays.asList("The client and server authentication tokens did not match"));
                messageService.sendMessage(appId, nodeId, errorDialog);
            } else if (!stateManager.isSessionCompatible(sessionId)) {
                DialogScreen errorDialog = new DialogScreen();
                errorDialog.asDialog(new DialogProperties(false));
                errorDialog.setIcon("error");
                errorDialog.setTitle("Incompatible Versions");
                errorDialog.setMessage(Arrays.asList(incompatibleVersionMessage));
                messageService.sendMessage(appId, nodeId, errorDialog);
            } else {
                stateManager.refreshScreen();
            }

        } catch (Exception ex) {
            logger.error("Failed to subscribe to " + topicName, ex);
        }
    }


}