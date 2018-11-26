package org.jumpmind.pos.translate;

import java.io.Serializable;
import java.util.Properties;

import org.jumpmind.pos.core.device.IDeviceMessageDispatcher;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.server.model.Action;

public interface ITranslationManagerSubscriber extends IDeviceMessageDispatcher, Serializable {

    public void showScreen(Screen screen);

    public void doAction(Action action);
    
    public boolean isInTranslateState();

    public String getNodeId();
    
    public String getAppId();
    
    public Properties getProperties();

}
