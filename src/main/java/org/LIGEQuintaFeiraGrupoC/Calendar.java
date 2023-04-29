package org.LIGEQuintaFeiraGrupoC;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import jfxtras.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class Calendar extends Application {

    private List<Map<?,?>> events = new ArrayList<>();

    @Override
    public void init() throws IOException {
        events = ReadFile.getData(ReadFile.getFile(getParameters().getRaw().get(0)));
        System.out.println(events);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Agenda agenda = createAgendaFromList(events);
        AgendaSkinSwitcher ss = new AgendaSkinSwitcher(agenda);

        VBox root = new VBox(agenda, ss);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static Agenda createAgendaFromList(List<Map<?,?>> list) {
        Agenda agenda = new Agenda();

        for(Map<?,?> m : list) {
            CharSequence dateChar = (CharSequence)(m.get("Data da aula").toString());
            LocalDate date = LocalDate.parse(dateChar, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            CharSequence inicTimeChar = (CharSequence)(m.get("Hora início da aula").toString());
            LocalTime inicTime = LocalTime.parse(inicTimeChar, DateTimeFormatter.ofPattern("HH:mm:ss"));

            CharSequence endTimeChar = (CharSequence)(m.get("Hora fim da aula").toString());
            LocalTime endTime = LocalTime.parse(endTimeChar, DateTimeFormatter.ofPattern("HH:mm:ss"));

            LocalDateTime startDateTime = inicTime.atDate(date);
            LocalDateTime endDateTime = endTime.atDate(date);
            System.out.println(startDateTime + " " + endDateTime);

            String description = m.toString();
            String summary = m.get("Unidade Curricular").toString();

            agenda.appointments().add(
                    new Agenda.AppointmentImplLocal()
                            .withStartLocalDateTime(startDateTime)
                            .withEndLocalDateTime(endDateTime)
                            .withDescription(description)
                            .withSummary(summary)
            );
        }

        return agenda;
    }
}
