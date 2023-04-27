package org.LIGEQuintaFeiraGrupoC;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import jfxtras.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;


public class Calendar extends Application {

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
        AgendaSkinSwitcher ss = new AgendaSkinSwitcher(agenda);
        VBox root = new VBox(agenda, ss);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
