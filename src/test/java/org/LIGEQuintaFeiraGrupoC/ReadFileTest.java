package org.LIGEQuintaFeiraGrupoC;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class ReadFileTest {
    final String horarioISCTE = "webcal://fenix.iscte-iul.pt/publico/publicPersonICalendar.do?method=iCalendar&username=larcs@iscte.pt&password=z7wVbBXBgvFsLak6Fniz4axxxOgdJAQ9lYpC50hHZNJW4peqyW7dn7OkMOzR8cdskCctRO4p2m3UZXBcI3iuHSLqLUAHOvVXJEeUY9ay7dxNXx3O6RRdKh0VsLKmrfoE";

    @Test
    void getFile() {
    }

    @Test
    void isValidFile() {
    }

    @Test
    void getData() {
    }

    @Test
    void testGetFile() {
    }

    @Test
    void testGetOnlineFileISCTE() {
        ReadFile read = new ReadFile();
        File file = read.getFile(horarioISCTE);

        assert(file != null);
        assert(!file.toString().isEmpty());

        System.out.println("Reading file\n" +file.toString());
        System.out.println("---------READ----------");
    }

    @Test
    void testDataOnlineFileISCTE() {
        ReadFile read = new ReadFile();
        File file = read.getFile(horarioISCTE);

        assert(file != null);
        assert(!file.toString().isEmpty());

        List list = read.getData(file);

        assert(list!=null);
        System.out.println(list.isEmpty());

        System.out.println("Reading file");
        for(Object o : list) {
            System.out.println(o.toString());
        }
        System.out.println("---------READ----------");
    }
}
