package org.LIGEQuintaFeiraGrupoC;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

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
        file.delete();
    }

    /*@Test
    void testIsValidFile() {
    }

    @Test
    void testGetData() {
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
}
