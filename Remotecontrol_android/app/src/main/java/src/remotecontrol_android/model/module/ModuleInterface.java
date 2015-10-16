package src.remotecontrol_android.model.module;

import src.remotecontrol_android.model.message.MessageInterface;

/**
 * Created by cyprien on 08/10/15.
 */
public interface ModuleInterface {

    void send(ModuleInterface message);
    void receive(MessageInterface message);
}
