package model;

import java.io.File;

/**
 * Created by cyprien on 24/10/15.
 */
public class FileExplorerModule {
    //Singleton
    private static FileExplorerModule instance;

    private FileExplorerModule() {

    }

    /**
     * Singleton getter
     *
     * @return Instance of the module
     */
    public static FileExplorerModule getInstance() {
        if (instance == null) instance = new FileExplorerModule();
        return instance;
    }

    private File root;
    private File[] list;

    /**
     * Change current directory to a path
     * @param path Path to move
     */

    public void changeDirectory( String path ) {
        root = new File( path );
        list = root.listFiles();
    }

    /**
     * Returns all the files in the current directory
     * @return Array of files and directories in the current directory
     */
    public File[] getCurrentDirectoryFiles(){
        return list;
    }
}
