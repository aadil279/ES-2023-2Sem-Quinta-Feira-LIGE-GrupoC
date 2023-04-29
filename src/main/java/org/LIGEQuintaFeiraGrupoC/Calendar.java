package org.LIGEQuintaFeiraGrupoC;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import jfxtras.scene.layout.VBox;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;


public class Calendar extends Application {

    private List<Map<?,?>> events = new ArrayList<>();

    public Calendar(List<Map<?,?>> events) {
            this.events.addAll(events);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Agenda agenda;
        agenda = new Agenda();
        agenda.appointments().add(
                new Agenda.AppointmentImplLocal()
                        .withStartLocalDateTime(LocalDate.now().atTime(13,00))
                        .withEndLocalDateTime(LocalDate.now().atTime(16,00))
                        .withDescription("ES")
                        .withSummary("Engenharia de Software")
        );

        AgendaSkinSwitcher ss = new AgendaSkinSwitcher(createAgendaFromList(events));
        //AgendaSkinSwitcher ss = new AgendaSkinSwitcher(agenda);
        VBox root = new VBox(agenda, ss);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static Agenda createAgendaFromList(List<Map<?,?>> list) {
        Agenda agenda = new Agenda();

        for(Map<?,?> m : list) {
            CharSequence inicDateChar = (CharSequence)(m.get("Data da aula").toString() + " "+ m.get("Hora início da aula").toString());
            LocalDateTime inicDate = LocalDateTime.parse(inicDateChar, DateTimeFormatter.ofPattern("dd/mm/yyyy hh:mm:ss"));

            CharSequence endTimeChar = (CharSequence)(m.get("Hora fim da aula").toString());
            LocalDateTime endTime = LocalDateTime.parse(endTimeChar, DateTimeFormatter.ofPattern("hh:mm:ss"));
            LocalDateTime endDate = inicDate.plusHours(endTime.getHour()).plusMinutes(endTime.getMinute()).plusSeconds(endTime.getSecond());

            String description = m.toString();
            String summary = m.get("Unidade Curricular").toString();

            agenda.appointments().add(
                    new Agenda.AppointmentImplLocal()
                            .withStartLocalDateTime(inicDate)
                            .withEndLocalDateTime(endDate)
                            .withDescription(description)
                            .withSummary(summary)
            );
        }

        return agenda;
    }
}
