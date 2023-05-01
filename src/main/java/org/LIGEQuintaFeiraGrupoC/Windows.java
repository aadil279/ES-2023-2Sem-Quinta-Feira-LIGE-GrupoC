package org.LIGEQuintaFeiraGrupoC;

import javafx.application.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Windows extends JFrame {

    private static final String SELECT_FILE = "Select file";
    private static final String SELECT_DIR = "Select a directory";

    boolean fileSelected = false;
    boolean dirSelected = false;
    File resultantFile;
    File resultantDir;
    String fileURL;

    private void process() {
        waitingWindows();

        if(!fileSelected) {
            resultantFile = ReadFile.getFile(fileURL);
        }

        setVisible(false);
        Application.launch(Calendar.class, new String[]{resultantFile.getName()});
        //successWindow();
    }
/*
    private void successWindow() {
        setVisible(false);
        removeAll();

        setTitle("Success inserting file");
        JPanel panel = new JPanel();
        JLabel label = new JLabel(resultantFile.getName());
        JButton continueToCalendar = new JButton("Continue");
        continueToCalendar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                Application.launch(Calendar.class,new String[]{resultantFile.getName()});
            }
        });
        panel.add(label);
        panel.add(continueToCalendar, BorderLayout.CENTER);
        add(panel);

        //setVisible(true);
    }*/

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
        JLabel fileURLLabel = new JLabel("or insert file URL:");
        JTextField fileURLInput = new JTextField("url");
        JButton okButton = new JButton("OK");
        JButton selectFile = new JButton(SELECT_FILE);
        JButton selectDir = new JButton(SELECT_DIR);

        selectFile.addActionListener(e -> {
            File tempFile = getFileFromUser(SELECT_FILE);
            if(tempFile != null) {
                resultantFile = tempFile;
                fileSelected = true;
            } else {
                showError("Couldn't find the file", "Error selecting file");
            }
        });

        selectDir.addActionListener(e -> {
            File tempFile = getDirectoryFromUser(SELECT_FILE);
            if(tempFile != null) {
                resultantDir = tempFile;
                dirSelected = true;
            } else {
                showError("Couldn't find the choosen directory", "Error selecting dir");
            }
        });

        // Add the components to the panel
        panel.add(selectFile);
        panel.add(fileURLLabel);
        panel.add(fileURLInput);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(selectDir);

        // Set up the OK button
        okButton.addActionListener(e -> {
            if(fileSelected || !fileURLInput.getText().isEmpty()) {
                if(!dirSelected) {
                    showError("No dir selected", "Couldn't execute");
                } else {
                    fileURL = fileURLInput.getText();
                    process();
                    disable();
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
/*
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
    }*/
    /**
     * Displays a file chooser dialog box and returns the selected file.
     * @param dialogTitle The title to display on the dialog box.
     * @return The selected file.
     */
    public static File getFileFromUser(String dialogTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);

        // Show the dialog box and wait for the user to select a file
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    /**
     * Displays a file chooser dialog box and returns the selected directory.
     * @param dialogTitle The title to display on the dialog box.
     * @return The selected directory.
     */
    public static File getDirectoryFromUser(String dialogTitle) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog box and wait for the user to select a directory
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    /**
     * Moves a file to a new directory.
     * @param file The file to move.
     * @param targetDirectory The target directory to move the file into.
     * @return The new file object representing the moved file, or null if an error occurred.
     */
    public static File moveFile(File file, File targetDirectory) {
        // Create a File object representing the target file in the new directory
        File targetFile = new File(targetDirectory.getAbsolutePath() + File.separator + file.getName());

        try {
            // Move the file to the target directory
            Files.move(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return targetFile;
        } catch (IOException e) {
            System.err.println("An error occurred while moving the file: " + e.getMessage());
            return null;
        }
    }
    /**
     * Shows an error message in a dialog box.
     * @param message The error message to display.
     * @param title The title of the dialog box.
     */
    public static void showError(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
