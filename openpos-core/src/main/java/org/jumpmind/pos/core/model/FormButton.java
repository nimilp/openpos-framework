package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class FormButton implements IFormElement, Serializable {
    private static final long serialVersionUID = 1L;
    
    private FieldElementType elementType = FieldElementType.Button;
    private String label;
    private String buttonAction;
    private String id;
    private String icon;
    private boolean submitButton = false;
    private String confirmationMessage;

    public FormButton() {
    }
    
    public FormButton(String label, String buttonAction) {
        this(label, label, buttonAction);
    }
    
    public FormButton(String label, String icon, String buttonAction, boolean submit) {
        this(label, label, buttonAction);
        this.submitButton = submit;
        this.icon = icon;
    }

    public FormButton(String id, String label, String buttonAction) {
        this.id = id;
        this.label = label;
        this.buttonAction = buttonAction;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getButtonAction() {
        return buttonAction;
    }

    public void setButtonAction(String buttonAction) {
        this.buttonAction = buttonAction;
    }

    public FieldElementType getElementType() {
        return elementType;
    }

    public void setElementType(FieldElementType elementType) {
        this.elementType = elementType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(boolean submitButton) {
        this.submitButton = submitButton;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    public void setConfirmationMessage(String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }

    
}
