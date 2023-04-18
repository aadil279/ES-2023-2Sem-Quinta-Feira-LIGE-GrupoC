package org.LIGEQuintaFeiraGrupoC;

import java.io.File;
import java.util.List;

public class ReadFile {
    public File getFile(String file) {
        // If file is local, readLocalFile(file), if is online, readOnlineFile().
        return null;
    }

    private File readLocalFile(String file) {
        return null;
    }

    private File readOnlineFile(String file) {
        return null;
    }

    public boolean isValidFile(File file) {
        return true;
    }

    public List getData(File file) {
        //if file is csv, getDataCSV. if json, getDataJson)
        return null;
    }

    private List getDataCSV(File file) {
        return null;
    }

    private List getDataJSON(File file) {
        //Use dependency (maybe jackson)
        return null;
    }

}
