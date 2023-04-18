package org.LIGEQuintaFeiraGrupoC;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import java.util.Map;

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

    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a csv file
     * @return returns a type List<Map> where every Map is a row and contains the values of every column
     * @throws IOException
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
        List<Map> list = Arrays.asList(mapper.readValue(jsonList, Map[].class));

        return list;
    }

}
