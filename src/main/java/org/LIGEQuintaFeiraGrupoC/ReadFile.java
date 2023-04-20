package org.LIGEQuintaFeiraGrupoC;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.FileReader;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class ReadFile {

    private static final String JSON_SUFX = ".json";
    private static final String CSV_SUFX = ".csv";
    private static final String CSV_DEL = ",";

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
        List<Map<String, String>> list = new ArrayList<>();
        String filePath = file.getPath();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String[] headers = br.readLine().split(CSV_DEL);
            while ((line = br.readLine()) != null) {
                String[] values = line.split(CSV_DEL);
                Map<String, String> record = new HashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    record.put(headers[i], values[i]);
                }
                list.add(record);
            }
        }
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
        String jsonString = Files.readString(path);

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Map<String,Object>>> typeRef = new TypeReference<>() {};
        List<Map<String, Object>> list = objectMapper.readValue(jsonString, typeRef);

        return list;
    }
}
