package model;

import java.nio.file.FileSystem;

/**
 * Created by cyprien on 24/10/15.
 */
public class FileExplorerModule {
    //Singleton
    private static FileExplorerModule instance;

    private FileExplorerModule() {
    }

    public static FileExplorerModule getInstance() {
        if (instance == null) instance = new FileExplorerModule();
        return instance;
    }

    //TODO;
}
