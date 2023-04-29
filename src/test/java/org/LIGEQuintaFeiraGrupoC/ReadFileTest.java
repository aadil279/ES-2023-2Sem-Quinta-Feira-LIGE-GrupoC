package org.LIGEQuintaFeiraGrupoC;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ReadFileTest {
    @Test
    void testGetFile() {
        File file = ReadFile.getFile("https://www.cambridgeenglish.org/images/210434-converting-practice-test-scores-to-cambridge-english-scale-scores.pdf");
        assertTrue(file.exists());

        file = ReadFile.getFile("210434-converting-practice-test-scores-to-cambridge-english-scale-scores.pdf");
        assertTrue(file.exists());

        assertNull(ReadFile.getFile("InvalidFile"));
        assertNull(ReadFile.getFile("https://randomlink.fake"));

        file = ReadFile.getFile("webcal://fenix.iscte-iul.pt/publico/publicPersonICalendar.do?method=iCalendar&username=larcs@iscte.pt&password=z7wVbBXBgvFsLak6Fniz4axxxOgdJAQ9lYpC50hHZNJW4peqyW7dn7OkMOzR8cdskCctRO4p2m3UZXBcI3iuHSLqLUAHOvVXJEeUY9ay7dxNXx3O6RRdKh0VsLKmrfoE");
        assertTrue(file.exists());

        file.delete();
    }

    /*@Test
    void testIsValidFile() {
    }*/

    @Test
    void testConvertoToJSON() {
        File csvFile = ReadFile.getFile(System.getProperty("user.dir")+File.separator+"testFiles"+File.separator+"horario_exemplo.csv");
        File jsonFile = ReadFile.convertToJSON(csvFile);
        assertTrue(jsonFile.exists());

        File invalidFile = ReadFile.getFile("https://www.cambridgeenglish.org/images/210434-converting-practice-test-scores-to-cambridge-english-scale-scores.pdf");
        File invalidJson = ReadFile.convertToJSON(invalidFile);
        assertNull(invalidJson);
        jsonFile.delete();
        invalidFile.delete();
    }

    @Test
    void testConvertToCSV() {
        File jsonFile = ReadFile.convertToJSON(ReadFile.getFile(System.getProperty("user.dir")+File.separator+"testFiles"+File.separator+"horario_exemplo.csv"));
        File csvFile = ReadFile.convertToCSV(jsonFile);

        assertTrue(csvFile.exists());
        csvFile.delete();
        jsonFile.delete();
    }

    @Test
    void testConvertFromCSVToList() {
        File csvFile = ReadFile.getFile(System.getProperty("user.dir")+File.separator+"testFiles"+File.separator+"horario_exemplo.csv");
        List csvList = new ArrayList();

        try {
            csvList.add(ReadFile.getData(csvFile));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        csvList.forEach(b -> System.out.println(b.toString()));
        assertTrue(!csvList.isEmpty());
    }

    @Test
    void testConvertFromJSONToList() {
        File jsonFile = ReadFile.getFile(System.getProperty("user.dir")+File.separator+"testFiles"+File.separator+"horario_exemplo.json");
        List jsonList = new ArrayList();

        try {
            jsonList.add(ReadFile.getData(jsonFile));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        jsonList.forEach(b -> System.out.println(b.toString()));
        assertTrue(!jsonList.isEmpty());
    }

    @Test
    void testDataOnlineFileISCTE() {
        File file = ReadFile.getFile("testFiles/horario_exemplo.ics");

        List list = null;
        try {
            list = ReadFile.getData(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assert(list!=null);
        assert(!list.isEmpty());

        System.out.println("Reading file");
        int i=0;
        for(Object o : list) {
            System.out.println("element "+i+" : "+o.toString());
            i++;
        }
        System.out.println("---------READ----------");
    }

    @Test
    void testGetHeaders() {
        File jsonFile = ReadFile.getFile(System.getProperty("user.dir")+File.separator+"testFiles"+File.separator+"horario_exemplo.json");
        List<Map<String,?>> jsonList = new ArrayList();

        try {
            jsonList.addAll(ReadFile.getData(jsonFile));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(!jsonList.isEmpty());
        Map<String,String> headers = ReadFile.getHeaders(jsonList.get(0).keySet());

        headers.forEach((b,v) -> System.out.println(b + " = "+ v));
        assertTrue(!headers.isEmpty());
    }
}
