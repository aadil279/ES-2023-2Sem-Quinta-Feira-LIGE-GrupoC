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

}
