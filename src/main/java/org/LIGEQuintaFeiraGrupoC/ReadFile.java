package org.LIGEQuintaFeiraGrupoC;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;

public class ReadFile {
    private static final String JSON_SUFX = ".json";
    private static final String CSV_SUFX = ".csv";
    private static final String CSV_DEL = ",";
    
    /**
     * Gets a file from disk or web
     * @param file Local path or file URL
     * @return File corresponding to the path or URL
     */
    public static File getFile(String file) {
        if(file.startsWith("http://") || file.startsWith("https://"))
            return readOnlineFile(file);
        return readLocalFile(file);
    }

    private static File readLocalFile(String file) {
        File f = new File(file);
        if(f.exists())
            return f;
        return null;
    }

    private static File readOnlineFile(String file) {
        String[] fields = file.split("/");
        String filename = fields[fields.length-1];
        try {
            URL website = new URL(file);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(filename);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            rbc.close();
            fos.close();
            return new File(filename);
        }catch(IOException e) {
            return null;
        }
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
    public static List getDataCSV(File file) throws IOException {
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
    public static List getDataJSON(File file) throws IOException {
        Path path = Path.of(file.getAbsolutePath());
        String jsonString = Files.readString(path);

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Map<String,Object>>> typeRef = new TypeReference<>() {};
        List<Map<String, Object>> list = objectMapper.readValue(jsonString, typeRef);

        return list;
    }

    /**
     * Function that receives a CSV file and converts it to JSON. If the file isn't a CSV or if it isn't correctly structured, it will return null.
     * @param f CSV file we want to convert
     * @return JSON file created from CSV file. Null if f isn't CSV or has errors.
     */
    public static File convertToJSON(File f) {
        if (!f.getName().endsWith(".csv"))
            return null;
        //CSV structure
        CsvSchema schema = CsvSchema.builder().setColumnSeparator(';').setUseHeader(true).build();
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
        try {
            // Convert CSV to List of Maps
            List<Map<?,?>> list;
            MappingIterator<Map<?,?>> mappingIterator = mapper.reader().forType(Map.class).with(schema).readValues(f);
            list = mappingIterator.readAll();

            // Write list to new jsonFile
            File jsonFile = new File(f.getName().replace(".csv",".json"));
            jsonFile.createNewFile();
            new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT,true).writeValue(jsonFile,list);
            return jsonFile;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Function that receives a JSON file and converts it to CSV. If the file isn't JSON, it will return null
     * @param f JSON file we want to convert
     * @return CSV file created from JSON.
     */
    public static File convertToCSV(File f) {
        if(!f.getName().endsWith(".json"))  return null;
        try {
            // Getting values from file and creating csv structure
            JsonNode jsonTree = new ObjectMapper().readTree(f);
            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setColumnSeparator(';').setUseHeader(true);
            // Get header for csv
            JsonNode firstObject = jsonTree.elements().next();
            firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);});

            CsvSchema csvSchema = csvSchemaBuilder.build();

            File newFile = new File(f.getName().replace(".json",".csv"));
            newFile.createNewFile();

            //Convert jsonTree to csv file
            new CsvMapper().writerFor(JsonNode.class).with(csvSchema).writeValue(newFile,jsonTree);
            return newFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
