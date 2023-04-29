package org.LIGEQuintaFeiraGrupoC;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import jfxtras.internal.scene.control.skin.agenda.base24hour.AgendaSkinTimeScale24HourAbstract;
import jfxtras.scene.control.ImageViewButton;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.AgendaSkinSwitcher;
import jfxtras.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


public class Calendar extends Application {

    private List<Map<String, ?>> events = new ArrayList<>();

    @Override
    public void init() throws IOException {
        events = ReadFile.getData(ReadFile.getFile(getParameters().getRaw().get(0)));
    }

    /**
     * Initializes a window containing the agenda from a file
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        Agenda agenda = createAgendaFromList(events);
        AgendaSkinSwitcher ss = new CustomSkinSwitcher(agenda);

        System.out.println(agenda.getLocalDateTimeRangeCallback());
        VBox root = new VBox(agenda, ss);
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Creates a JFXtras Agenda object from a List (that can be obtained using ReadFile class)
     * @param list  List containing calendar data
     * @return  Agenda object
     * @see ReadFile
     */
    public static Agenda createAgendaFromList(List<Map<String, ?>> list) {
        Agenda agenda = new Agenda();

        for(Map<?,?> m : list) {
            CharSequence dateChar = m.get("Data da aula").toString();
            LocalDate date = LocalDate.parse(dateChar, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            CharSequence inicTimeChar = m.get("Hora início da aula").toString();
            LocalTime inicTime = LocalTime.parse(inicTimeChar, DateTimeFormatter.ofPattern("HH:mm:ss"));

            CharSequence endTimeChar = m.get("Hora fim da aula").toString();
            LocalTime endTime = LocalTime.parse(endTimeChar, DateTimeFormatter.ofPattern("HH:mm:ss"));

            LocalDateTime startDateTime = inicTime.atDate(date);
            LocalDateTime endDateTime = endTime.atDate(date);

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

    /**
     * Class derived from AgendaSwitcher, with the added fucntion of having a button for month view
     */
    protected class CustomSkinSwitcher extends AgendaSkinSwitcher {

        public CustomSkinSwitcher(Agenda agenda) {
            super(agenda);
            getChildren().add(createMonthButton(agenda));
        }

        private ImageViewButton createMonthButton(Agenda agenda) {
            ImageViewButton button = createIcon("month", "month view");
            button.setOnMouseClicked((actionEvent) -> {agenda.setSkin(new AgendaSkinTimeScale24HourAbstract<Object>(agenda) {
                @Override
                protected List<LocalDate> determineDisplayedLocalDates() {
                    LocalDate start = LocalDate.now().withDayOfMonth(1);
                    LocalDate end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
                    return start.datesUntil(end).collect(Collectors.toList());
                }
            });});
            return button;
        }

        private ImageViewButton createIcon(String type, String tooltip) {
            ImageViewButton imageView = new ImageViewButton();
            imageView.getStyleClass().add(type + "-icon");
            imageView.setPickOnBounds(true);
            Tooltip.install(imageView, new Tooltip(tooltip));
            return imageView;
        }
    }
}
