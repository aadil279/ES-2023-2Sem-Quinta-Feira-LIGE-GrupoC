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
import jfxtras.icalendarfx.VCalendar;
import jfxtras.icalendarfx.components.VEvent;

import java.util.Map;

public class ReadFile {
    private static final String JSON_SUFX = ".json";
    private static final String CSV_SUFX = ".csv";
    private static final String ICAL_SUFX = ".ics";
    private static final char CSV_DEL = ';';
    public static final String URI_HEAD = "webcal://";
    public static final String HTTPS_HEAD = "https://";
    private static final String ourHeaders[] = {"Curso", "UC", "Turno", "Turma", "Inscritos", "DiaSemana", "HoraInic", "HoraFim", "Data", "Sala", "Lotacao"};
    
    /**
     * Gets a file from disk or web
     * @param file Local path or file URL
     * @return File corresponding to the path or URL
     */
    public static File getFile(String file) {
        if(file.startsWith("http://") || file.startsWith(HTTPS_HEAD) || file.startsWith(URI_HEAD))
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
        String url = file;
        String[] fields;
        String filename = "test";
        if(url.startsWith(URI_HEAD)) {
            url = url.replace(URI_HEAD,HTTPS_HEAD);
            fields = url.split("[&=]");
            for (String s : fields)
                if(s.contains("@"))     filename = s + ".ics";
        }
        else{
            fields = url.split("/");
            filename = fields[fields.length-1];
        }
        try {
            URL website = new URL(url);
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
        return file.exists() && !file.getPath().isEmpty();
    }

    /**
     * given a key set devolves a map with our header to use a common language due to differences between files
     * @param originHeaders a key set from the original file
     * @return a HashMap where keys are our common keys and value are the original file's keys
     */
    public static Map<String,String> getHeaders(Set<String> originHeaders) {
        Map<String,String> nameMap = new HashMap<>();
        int max = Math.min(originHeaders.size(), ourHeaders.length);

        int i = 0;
        for(String key : originHeaders) {
            nameMap.put(ourHeaders[i], key);
            i++;

            if(i == max) break;
        }

        return nameMap;
    }

    public static List<Map<String,?>> getData(File file) throws IOException {
        if(file.getName().endsWith(CSV_SUFX))
            return getDataCSV(file);
        if(file.getName().endsWith(JSON_SUFX))
            return getDataJSON(file);
        if(file.getName().endsWith(ICAL_SUFX))
            return getDataICS(file);

        return null;
    }

    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a csv file
     * @return returns a type List<Map> where every Map is a row and contains the values of every column
     * @throws IOException in case the csv is not properly formatted.
     */
    private static List<Map<String,?>> getDataCSV(File file) throws IOException {
        CsvSchema schema = CsvSchema.builder().setColumnSeparator(CSV_DEL).setUseHeader(true).build();
        CsvMapper mapper = new CsvMapper();

        List<Map<String,?>> list;
        MappingIterator<Map<String,?>> mappingIterator = mapper.reader().forType(Map.class).with(schema).readValues(file);
        list = mappingIterator.readAll();

        return list;
    }

    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a json list
     * @return returns a type List<Map> where every Map contains the attributes of the json element
     * @throws IOException
     */
    private static List<Map<String,?>> getDataJSON(File file) throws IOException {

        Path path = Path.of(file.getPath());
        String jsonString = Files.readString(path);

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<List<Map<String,?>>> typeRef = new TypeReference<>() {};
        List<Map<String,?>> list = objectMapper.readValue(jsonString, typeRef);

        return list;
    }

    /**
     * Function that receives a CSV file and converts it to JSON. If the file isn't a CSV or if it isn't correctly structured, it will return null.
     * @param f CSV file we want to convert
     * @return JSON file created from CSV file. Null if f isn't CSV or has errors.
     */
    public static File convertToJSON(File f) {
        if (!f.getName().endsWith(CSV_SUFX))
            return null;
        //CSV structure
        CsvSchema schema = CsvSchema.builder().setColumnSeparator(CSV_DEL).setUseHeader(true).build();
        CsvMapper mapper = new CsvMapper();
        mapper.enable(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
        try {
            // Convert CSV to List of Maps
            List<Map<String,?>> list;
            MappingIterator<Map<String,?>> mappingIterator = mapper.reader().forType(Map.class).with(schema).readValues(f);
            list = mappingIterator.readAll();

            // Write list to new jsonFile
            File jsonFile = new File(f.getName().replace(CSV_SUFX, JSON_SUFX));
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
        if(!f.getName().endsWith(JSON_SUFX))
            return null;
        try {
            // Getting values from file and creating csv structure
            JsonNode jsonTree = new ObjectMapper().readTree(f);
            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setColumnSeparator(CSV_DEL).setUseHeader(true);
            // Get header for csv
            JsonNode firstObject = jsonTree.elements().next();
            firstObject.fieldNames().forEachRemaining(fieldName -> {csvSchemaBuilder.addColumn(fieldName);});

            CsvSchema csvSchema = csvSchemaBuilder.build();

            File newFile = new File(f.getName().replace(JSON_SUFX, CSV_SUFX));
            newFile.createNewFile();

            //Convert jsonTree to csv file
            new CsvMapper().writerFor(JsonNode.class).with(csvSchema).writeValue(newFile,jsonTree);
            return newFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a Calendar in .ics
     * @return returns a type List<Map> where every Map contains an event in the Calendar
     * @throws IOException
     */
    private static List getDataICS(File file) {
        VCalendar calendar = null;
        try {
            calendar = VCalendar.parseICalendarFile(Path.of(file.getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<VEvent> events = calendar.getVEvents();

        return events;
    }
}