package org.jumpmind.pos.translate;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.jumpmind.pos.core.screen.DefaultScreen;
import org.jumpmind.pos.core.screen.IUIAction;
import org.jumpmind.pos.core.screen.MenuItem;
import org.jumpmind.pos.core.screen.Workstation;
import org.jumpmind.pos.translate.ILegacyRegisterStatusService.Status;


public abstract class AbstractLegacyScreenTranslator <T extends DefaultScreen> extends AbstractScreenTranslator<T>{
    
    public final static String LOCAL_NAV_PANEL_KEY = "LocalNavigationPanel";
    public final static String GLOBAL_NAV_PANEL_KEY = "GlobalNavigationPanel";
    public final static String PROMPT_RESPONSE_PANEL_KEY = "PromptAndResponsePanel";
    public final static String WORK_PANEL_KEY = "WorkPanel";
    public final static String STATUS_PANEL_KEY = "StatusPanel";

    private ILegacyUtilityManager legacyUtilityManager;

    protected ILegacyPOSBeanService legacyPOSBeanService;
    protected ILegacyStoreProperties legacyStoreProperties;
    
    public static Map<String, String> labelTagToIconMap = new HashMap<>();

    static {
        labelTagToIconMap.put("Find", "search");
        labelTagToIconMap.put("Add", "add");
        labelTagToIconMap.put("AddRewardClub", "add");
        labelTagToIconMap.put("FindRewardClub", "search");
        labelTagToIconMap.put("AddBusiness", "add");
        labelTagToIconMap.put("Delete", "delete");
        labelTagToIconMap.put("CustID", "keyboard");
        labelTagToIconMap.put("EmployeeID", "keyboard");
        labelTagToIconMap.put("CustomerInfo", "person");
        labelTagToIconMap.put("BusinessInfo", "store");
        labelTagToIconMap.put("TaxExemptID", "keyboard");
        labelTagToIconMap.put("TillOptions", "attach_money");
        labelTagToIconMap.put("ViewCurrentSales", "pageview");
        labelTagToIconMap.put("SkipToEmail", "email");
        labelTagToIconMap.put("Bypass", "done");
        labelTagToIconMap.put("EmailRcptNoMKGT", "done");
        
    }
    
    public AbstractLegacyScreenTranslator(ILegacyScreen headlessScreen, Class<T> screenClass) {
        super(headlessScreen, screenClass);
    }

    public void setLegacyPOSBeanService(ILegacyPOSBeanService beanService) {
        this.legacyPOSBeanService = beanService;
    }

    public void setLegacyStoreProperties(ILegacyStoreProperties legacyStoreProperties) {
        this.legacyStoreProperties = legacyStoreProperties;
    }        

    @Override
    protected void buildMainContent() {
        buildBackButton();
        logAvailableLocalMenuItems();
        buildStatusItems();        
        Workstation workstation = new Workstation();
        workstation.setStoreId(legacyStoreProperties.getStoreNumber());
        workstation.setWorkstationId(legacyStoreProperties.getWorkstationNumber());
        screen.setWorkstation(workstation);
    }

    protected void buildStatusItems() {
        screen.setOperatorName(WordUtils.capitalizeFully(posSessionInfo.getOperatorName()));
        screen.setRegisterStatus(posSessionInfo.isRegisterOpen()
                .map(registerOpen -> new MenuItem((registerOpen ? DefaultScreen.TITLE_OPEN_STATUS : DefaultScreen.TITLE_CLOSED_STATUS), "", true)).orElse(null));
        screen.setStoreStatus(posSessionInfo.isStoreOpen()
                .map(storeOpen -> new MenuItem((storeOpen ? DefaultScreen.TITLE_OPEN_STATUS : DefaultScreen.TITLE_CLOSED_STATUS), "", true)).orElse(null));
        screen.setName(getScreenName());
    }
    
    protected String getScreenName() {
        ILegacyStatusBeanModel statusModel =  legacyPOSBeanService.getLegacyStatusBeanModel(legacyScreen);
        if (statusModel != null && statusModel.getScreenName() != null) {
            return statusModel.getScreenName();
        } else {
            ILegacyAssignmentSpec statusPanelSpec = legacyPOSBeanService.getLegacyAssignmentSpec(legacyScreen, STATUS_PANEL_KEY);
            String labelTag = getSpecPropertyValue(statusPanelSpec, "screenNameTag", null);
            if (labelTag != null) {                
                return legacyPOSBeanService.getLegacyUtilityManager(legacyScreen).retrieveText("StatusPanelSpec", getResourceBundleFilename(), labelTag, labelTag);
            } else {
                return null;
            }
        }
    }
    
    protected void logAvailableLocalMenuItems() {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(LOCAL_NAV_PANEL_KEY);
        if (assignmentPanelSpec != null) {
            ILegacyBeanSpec localNavSpec = this.legacyPOSBeanService.getLegacyBeanSpec(this.legacyScreen, assignmentPanelSpec.getBeanSpecName());
            Map<String, Boolean> enabledState = parseButtonStates(assignmentPanelSpec);

            Arrays.stream(localNavSpec.getButtons())
                    .filter(buttonSpec -> Optional.ofNullable(enabledState.get(buttonSpec.getActionName())).orElse(buttonSpec.getEnabled()))
                    .forEachOrdered(enabledButtonSpec -> {
                        logger.info("Available local menu with label tag of: {}", enabledButtonSpec.getLabelTag());
                    });
            
        }
    }
    
    protected ILegacyUIModel getLegacyUIModel() {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(WORK_PANEL_KEY);
        ILegacyUIModel legacyUIModel = null;
        if (assignmentPanelSpec != null) {
            logger.trace("The work panel bean spec name was {}", assignmentPanelSpec.getBeanSpecName());

            legacyUIModel = this.legacyPOSBeanService.getLegacyUIModel(legacyScreen);
            
        }

        return legacyUIModel;
    }

    protected ILegacyBus getBus() {
        return this.legacyPOSBeanService.getLegacyBus(legacyScreen);
    }

    protected void buildBackButton() {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(GLOBAL_NAV_PANEL_KEY);
        if (null != assignmentPanelSpec) {
            ILegacyBeanSpec globalNavSpec = this.legacyPOSBeanService.getLegacyBeanSpec(this.legacyScreen, assignmentPanelSpec.getBeanSpecName());
            Map<String, Boolean> enabledState = parseButtonStates(assignmentPanelSpec);

            Arrays.stream(globalNavSpec.getButtons())
                    .filter(buttonSpec -> Optional.ofNullable(enabledState.get(buttonSpec.getActionName())).orElse(buttonSpec.getEnabled()))
                    .forEachOrdered(enabledButtonSpec -> {
                        if ("Undo".equals(enabledButtonSpec.getLabelTag())) {
                            screen.setBackButton(new MenuItem("Back", enabledButtonSpec.getActionName(), true));
                        }
                    });
            
        }
    }

    protected String getPanelPropertyValue(String panelName, String propertyName) {
        String propertyValue = propertyName; // default to propertyName
        ILegacyAssignmentSpec assignmentSpec = this.getLegacyAssignmentSpec(panelName);
        if (assignmentSpec != null) {
            propertyValue = this.getLegacyUtilityManager().retrieveText(assignmentSpec.getBeanSpecName(), getResourceBundleFilename(), propertyName, propertyName);
        }
        return propertyValue;
        
    }
    
    protected String getWorkPanelPropertyValue(String propertyName) {
        String propertyValue = propertyName; // default to propertyName
        ILegacyAssignmentSpec assignmentSpec = this.getLegacyAssignmentSpec();
        if (assignmentSpec != null) {
            propertyValue = this.getLegacyUtilityManager().retrieveText(assignmentSpec.getBeanSpecName(), getResourceBundleFilename(), propertyName, propertyName);
        }
        return propertyValue;
    }
    
    protected String getSpecPropertyValue(ILegacyAssignmentSpec spec, String key, String modelValue) {
        if (isBlank(modelValue)) {
            String propValue = spec.getPropertyValue(key);
            if (propValue != null) {
                return propValue;
            } else {
                return null;
            }
        } else {
            return modelValue;
        }
    }
    
    protected void updatePosSessionInfo() {
        if (legacyScreen != null) {
            ILegacyRegisterStatusService registerStatusService = this.legacyPOSBeanService.getLegacyRegisterStatusService(legacyScreen);
            if (registerStatusService.isStatusDeterminable()) {
                posSessionInfo.setRegisterOpen(Optional.of(registerStatusService.getRegisterStatus() == Status.OPEN));

                Status storeStatus = registerStatusService.getStoreStatus();
                if (storeStatus != Status.UNKNOWN) {
                    posSessionInfo.setStoreOpen(Optional.of((storeStatus == Status.OPEN)));
                }
            }          
            
            ILegacyBus bus =  legacyPOSBeanService.getLegacyBus(legacyScreen);
            ILegacyCargo cargo =  bus.getLegacyCargo();
            if (cargo != null) {
                posSessionInfo.setOperatorName(cargo.getOperatorFirstLastName());
            }
            
        }
    }
    
    
    public ILegacyUtilityManager getLegacyUtilityManager() {
        if (legacyUtilityManager == null) {
            legacyUtilityManager = this.legacyPOSBeanService.getLegacyUtilityManager(legacyScreen);
        }
        
        return legacyUtilityManager;
    }

    protected String getResourceBundleFilename() {
        return this.legacyScreen.getResourceBundleFilename();
    }

    protected List<ILegacyButtonSpec> getPanelButtons(String panelKey, Optional<Boolean> enabledButtonsOnlyOpt) {
        ILegacyAssignmentSpec assignmentPanelSpec = getLegacyAssignmentSpec(panelKey);
        List<ILegacyButtonSpec> buttons = new ArrayList<>();

        if (assignmentPanelSpec != null) {
            ILegacyBeanSpec localNavSpec = this.legacyPOSBeanService.getLegacyBeanSpec(legacyScreen, assignmentPanelSpec.getBeanSpecName());
            Map<String, Boolean> buttonStateMap = parseButtonStates(assignmentPanelSpec);

            Arrays.stream(localNavSpec.getButtons()).filter(buttonSpec -> {
                if (enabledButtonsOnlyOpt.orElse(false)) {
                    // If only enabled buttons are wanted, use the button spec
                    // enabled/disabled status to
                    // only keep the enabled buttons
                    Boolean buttonState = buttonStateMap.get(buttonSpec.getActionName());
                    return buttonState != null ? buttonState : buttonSpec.getEnabled();
                } else { // Else don't filter any of the buttons
                    return true;
                }
            }).forEachOrdered(buttonSpec -> {
                buttons.add(buttonSpec);
            });
            
        }
        return buttons;
    }

    protected ILegacyAssignmentSpec getLegacyAssignmentSpec() {
        return getLegacyAssignmentSpec(WORK_PANEL_KEY);
        
    }

    public ILegacyPromptAndResponseModel getPromptAndResponseModel() {
        return this.legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen);
    }
    
    protected String retrieveCommonText(String propName, Optional<String> defaultValue) {
        String commonText = defaultValue.isPresent() ? 
                this.legacyPOSBeanService.getLegacyUIUtilities().retrieveCommonText(propName, defaultValue.get()) : 
                this.legacyPOSBeanService.getLegacyUIUtilities().retrieveCommonText(propName);
        return commonText;
    }
    
    protected String toFormattedString(Properties resourceBundle, String propertyName, Optional<String[]> args) {
        String text = (String) resourceBundle.get(propertyName);
        if (args.isPresent() && isNotBlank(text)) {
            text = this.toFormattedString(text, args.get());
        }
        return text;
    }

    protected String toFormattedString(String stringToFormat, String[] args) {
        String formattedText = this.legacyPOSBeanService.getLegacyLocaleUtilities().formatComplexMessage(stringToFormat, args);
        return formattedText;
    }
    
    protected String toFormattedString(Properties resourceBundle, String propertyName, String[] args) {
        return toFormattedString(resourceBundle, propertyName, Optional.ofNullable(args));
    }
    
    protected ILegacyAssignmentSpec getLegacyAssignmentSpec(String panelKey) {
        ILegacyAssignmentSpec spec = this.legacyPOSBeanService.getLegacyAssignmentSpec(this.legacyScreen, panelKey);
        return spec;
    }

    @Override
    protected void chooseScreenName() {
        String screenName = null;
        ILegacyStatusBeanModel statusModel = this.legacyPOSBeanService.getLegacyStatusBeanModel(legacyScreen);
        if (statusModel != null && statusModel.getScreenName() != null) {
            screenName = statusModel.getScreenName();
        } else {
            String labelTag = getSpecPropertyValue(getLegacyAssignmentSpec(STATUS_PANEL_KEY), "screenNameTag", null);
            if (labelTag != null) {
                screenName = this.getLegacyUtilityManager().retrieveText("StatusPanelSpec", getResourceBundleFilename(), labelTag, labelTag);
            }
        }
        
        if (getScreen().getName() == null) {
            getScreen().setName(screenName);
        }
    }

    protected Map<String, Boolean> parseButtonStates(ILegacyAssignmentSpec spec) {
        Map<String, Boolean> states = new HashMap<>();
        String propValue = spec.getPropertyValue("buttonStates");
        if (propValue != null) {
            String stateString = propValue;
            String[] tokens = stateString.split(",");
            for (String token : tokens) {
                token = token.trim();
                String key = token.substring(0, token.indexOf("["));
                String value = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
                states.put(key, new Boolean(value));
            }
        }
        return states;
    }

    protected <A extends IUIAction> List<A> generateUIActionsForLocalNavButtons(Class<A> actionClass, boolean filterDisabled, String... excludedLabelTags) {
        Set<String> toExclude = new HashSet<>();
        if (excludedLabelTags != null) {
            for (String string : excludedLabelTags) {
                toExclude.add(string);
            }
        }

        final List<A> generatedActions = new ArrayList<A>();
        ILegacyAssignmentSpec panelSpec = legacyPOSBeanService.getLegacyAssignmentSpec(legacyScreen, LOCAL_NAV_PANEL_KEY);
        if (panelSpec != null) { 
            ILegacyBeanSpec localNavSpec = legacyPOSBeanService.getLegacyBeanSpec(legacyScreen, panelSpec.getBeanSpecName());
            ILegacyPOSBaseBeanModel model = legacyPOSBeanService.getLegacyPOSBaseBeanModel(legacyScreen);
            ILegacyNavigationButtonBeanModel buttonModel = model.getLegacyLocalButtonBeanModel();
            Map<String, Boolean> enabledState = parseButtonStates(panelSpec);
            for (ILegacyButtonSpec buttonSpec : localNavSpec.getButtons()) {
                if (buttonSpec != null && !toExclude.contains(buttonSpec.getLabelTag())) {
                    Boolean enabled = enabledState.get(buttonSpec.getActionName());
                    if (enabled == null) {
                        enabled = buttonSpec.getEnabled();
                    }

                    String labelTag = buttonSpec.getLabelTag();
                    String label = labelTag;
                    String action = buttonSpec.getActionName();
                    try {
                        label = legacyPOSBeanService.getLegacyUtilityManager(legacyScreen).retrieveText(panelSpec.getBeanSpecName(), getResourceBundleFilename(), labelTag, labelTag);
                    } catch (Exception e) {
                        logger.info("Failed to look up label for button: {}", labelTag);
                    }

                    if (buttonModel != null) {
                        ILegacyButtonSpec[] buttonSpecs = buttonModel.getModifyButtons();
                        if (buttonSpecs != null) {
                            for (ILegacyButtonSpec modifiedSpec : buttonSpecs) {
                                if (modifiedSpec.getActionName().equals(action)) {
                                    enabled = modifiedSpec.getEnabled();
                                }
                            }
                        }
                    }
                    if (enabled || !filterDisabled) {
                        logger.info("adding action with label tag: {}", labelTag);

                        A actionItem;
                        try {
                            actionItem = actionClass.newInstance();
                            actionItem.setTitle(label);
                            actionItem.setIcon(labelTagToIconMap.get(labelTag));
                            actionItem.setEnabled(enabled);
                            actionItem.setAction(buttonSpec.getActionName());
                            generatedActions.add(actionItem);
                        } catch (InstantiationException | IllegalAccessException e) {
                            logger.error(String.format("Failed to create action of type %s for action '%s'", actionClass.getName(),
                                    buttonSpec.getActionName()), e);
                        }
                    }

                }
            }
        }

        return generatedActions;

    }
    
    protected Optional<String> getPromptText(ILegacyUIModel uiModel, ILegacyAssignmentSpec promptResponsePanel, String resourceBundleFilename) {
        Optional<String> optPromptText = Optional.empty();
        try {
            ILegacyPromptAndResponseModel promptAndResponseModel = this.legacyPOSBeanService.getLegacyPromptAndResponseModel(legacyScreen);
            
            String promptTextTag = promptResponsePanel.getPropertyValue("promptTextTag");
            Properties resourceBundle = this.legacyPOSBeanService.getLegacyResourceBundleUtil().getText(resourceBundleFilename, Locale.getDefault());
            String promptTextKey = String.format("%s.%s", "PromptAndResponsePanelSpec", promptTextTag);
            String formattedPromptText = null;
            formattedPromptText = toFormattedString(resourceBundle, 
                promptTextKey, 
                promptAndResponseModel != null ? Optional.ofNullable(promptAndResponseModel.getArguments()) : Optional.empty()
            );
            optPromptText = Optional.ofNullable(formattedPromptText);
        } catch (Exception ex) {
            logger.error("Failed to get promptText for {}", uiModel.getModel().getClass());
        }
        return optPromptText;
    }
   
}
