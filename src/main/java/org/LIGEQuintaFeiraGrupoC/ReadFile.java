package org.LIGEQuintaFeiraGrupoC;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import jfxtras.icalendarfx.VCalendar;
import jfxtras.icalendarfx.components.VEvent;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map;

public class ReadFile {
    public static final String URI_HEAD = "webcal";
    public static final String HTTP_HEAD = "http";

    public File getFile(String file) {
        // If file is local, readLocalFile(file), if is online, readOnlineFile().
        return readOnlineFile(file);
    }

    private File readLocalFile(String file) {
        return null;
    }

    /**
     * Reads an online file from a URI
     * @param file a String formatted as a URI
     * @return a type File without name which content is a String if the URI is valid, 'null' otherwise
     */
    private File readOnlineFile(String file) {
        if(file.substring(0, 10).contains(URI_HEAD)) {
            int n = URI_HEAD.length();
            file = HTTP_HEAD + file.substring(n);
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(file).build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return new File(response.body().string());

        } catch (IOException e) {
            System.err.println("Couldn't transform the file");
        }

        return null;
    }

    public boolean isValidFile(File file) {
        return true;
    }

    public List getData(File file) {
        List result = new ArrayList<>();

        try {
            result.addAll(getDataICS(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
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

    /**
     * Transforms the content of a file into a Map
     * @param file should be formatted as a Calendar in .ics
     * @return returns a type List<Map> where every Map contains an event in the Calendar
     * @throws IOException
     */
    private List getDataICS(File file) throws IOException {
        VCalendar calendar = VCalendar.parse(file.toString());
        List<VEvent> events = calendar.getVEvents();

        return events;
    }
}
