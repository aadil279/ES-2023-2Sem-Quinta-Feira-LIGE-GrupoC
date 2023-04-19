package org.LIGEQuintaFeiraGrupoC;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

public class ReadFile {

    private static final String JSON_SUFX = ".json";
    private static final String CSV_SUFX = ".csv";

    public File getFile(String file) {
        // If file is local, readLocalFile(file), if is online, readOnlineFile().
        File result = null;
        File preResult = null;

        if(file.endsWith(JSON_SUFX) || file.endsWith(CSV_SUFX)) {
            preResult = readLocalFile(file);
        } else {
            preResult = readOnlineFile(file);
        }

        if(isValidFile(preResult))
            result = preResult;

        return result;
    }

    private File readLocalFile(String file) {
        return new File(file);
    }

    private File readOnlineFile(String file) {
        return new File(file);
    }

    public boolean isValidFile(File file) {
        boolean result = false;
        if(!file.getPath().isEmpty())
            result = true;
        return result;
    }

    public List getData(File file) throws IOException {
        //if file is csv, getDataCSV. if json, getDataJson)
        List l = Collections.emptyList();

        if(file.getName().endsWith(".csv"))
            l.addAll(getDataCSV(file));
        else if(file.getName().endsWith(".json"))
            l.addAll(getDataJSON(file));

        return l;
    }

    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a csv file
     * @return returns a type List<Map> where every Map is a row and contains the values of every column
     * @throws IOException in case the csv is not properly formatted.
     */
    private List getDataCSV(File file) throws IOException {
        MappingIterator<Map> personIter = new CsvMapper().readerWithTypedSchemaFor(Map.class).readValues(file);
        List<Map> list = personIter.readAll();
        return list;
    }

    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a json list
     * @return returns a type List<Map> where every Map contains the attributes of the json element
     * @throws IOException
     */
    private List getDataJSON(File file) throws IOException {

        Path path = Path.of(file.getPath());
        String jsonList = Files.readString(path);

        ObjectMapper mapper = new ObjectMapper();

        return Arrays.asList(mapper.readValue(jsonList, Map[].class));
    }

}
