package org.LIGEQuintaFeiraGrupoC;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.util.Map;

public class ReadFile {
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

    public static boolean isValidFile(File file) {
        return true;
    }

    public static List getData(File file) {
        //if file is csv, getDataCSV. if json, getDataJson)
        return null;
    }

    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a csv file
     * @return returns a type List<Map> where every Map is a row and contains the values of every column
     * @throws IOException
     */
    private static List getDataCSV(File file) throws IOException {

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
    private static List getDataJSON(File file) throws IOException {

        Path path = Path.of(file.getPath());
        String jsonList = Files.readString(path);

        ObjectMapper mapper = new ObjectMapper();
        List<Map> list = Arrays.asList(mapper.readValue(jsonList, Map[].class));

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
