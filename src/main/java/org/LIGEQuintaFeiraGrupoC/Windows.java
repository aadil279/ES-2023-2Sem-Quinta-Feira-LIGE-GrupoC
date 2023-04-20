package org.LIGEQuintaFeiraGrupoC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.util.List;

public class Windows extends JFrame {

    private JTextField fileNameField;
    private JRadioButton jsonRadioButton;
    private JRadioButton csvRadioButton;
    private JTextField finalPathField;

    private String file;
    //private String endPath;
    //private String type;

    private void process() {
        ReadFile reader = new ReadFile();
        File processedFile = reader.getFile(file);

        try {
            List fileElements = reader.getData(processedFile);

            if(!fileElements.isEmpty()) {
                successWindow(fileElements);
            }
        } catch (IOException e) {
            System.err.println("Not a valid type");
        }
    }

    private void successWindow(List fileElements) {
        setVisible(false);
        removeAll();

        setTitle("Success");
        JPanel panel = new JPanel();

        for (Object fileElement : fileElements) {
            JTextField text = new JTextField(fileElement.toString());
            panel.add(text);
        }

        setVisible(true);
    }

    public Windows() {
        // Set up the JFrame
        setTitle("Convert file to...");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the JPanel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setLayout(new GridLayout(4, 2));

        // Create the components
        JLabel fileNameLabel = new JLabel("File Name:");
        fileNameField = new JTextField();
        JLabel fileTypeLabel = new JLabel("File Type:");
        jsonRadioButton = new JRadioButton("JSON");
        csvRadioButton = new JRadioButton("CSV");
        JLabel finalPathLabel = new JLabel("Final Path:");
        finalPathField = new JTextField();
        JButton okButton = new JButton("OK");

        // Add the components to the panel
        panel.add(fileNameLabel);
        panel.add(fileNameField);
        panel.add(fileTypeLabel);
        panel.add(jsonRadioButton);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(csvRadioButton);
        panel.add(finalPathLabel);
        panel.add(finalPathField);

        // Add radio buttons to a button group to ensure only one can be selected at a time
        ButtonGroup fileTypeGroup = new ButtonGroup();
        fileTypeGroup.add(jsonRadioButton);
        fileTypeGroup.add(csvRadioButton);

        // Set up the OK button
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the information from the fields
                String fileName = fileNameField.getText();
                String fileType = "";
                if (jsonRadioButton.isSelected()) {
                    fileType = "JSON";
                } else if (csvRadioButton.isSelected()) {
                    fileType = "CSV";
                }
                String finalPath = finalPathField.getText();

                // Check if the final path is valid
                if (isValidPath(finalPath)) {
                    // Do something with the information (in this case, just print it to the console)
                    file = fileName;
                    //type = fileType;
                    //endPath = finalPath;

                    waitingWindows();

                    process();
                } else {
                    // Display an error message if the final path is not valid
                    JOptionPane.showMessageDialog(panel, "Invalid final path", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add the components to the JFrame and show it
        add(panel, BorderLayout.CENTER);
        add(okButton, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void waitingWindows() {
        setVisible(false);
        removeAll();
        setTitle("Waiting");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("please wait");
        panel.add(label, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Function to check if a path is valid
    private boolean isValidPath(String path) {
        boolean result = false;
        File file = new File(path);
        try {
            if(!file.getCanonicalPath().isEmpty())
                result = true;
        } catch (IOException e) {
            result = false;
        }

        return result;
    }
}
