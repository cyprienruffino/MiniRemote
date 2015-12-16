package model;

import java.awt.AWTException;
import java.io.IOException;

/**
 * Created by cyprien on 24/10/15.
 */
public class ShellModule {
    //Singleton
    private static ShellModule instance;

    private ShellModule() throws AWTException {
    }

    /**
     * Singleton getter
     *
     * @return Instance of the module
     * @throws AWTException
     */
    public static ShellModule getInstance() throws AWTException {
        if (instance == null) instance = new ShellModule();
        return instance;
    }

    /**
     * Execute a command with host command processor
     *
     * @param command Command to execute
     * @throws IOException
     */
    public void execute(String command) throws IOException {
        Runtime.getRuntime().exec(command);
    }

}
