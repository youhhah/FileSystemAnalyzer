package ru.fsapp;

import ru.fsapp.ui.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Main application for file system analysis.
 * Entry point to the application with graphical interface.
 *
 * @author Student
 * @version 1.0
 * @since 2025-12-05
 */
public class FileSystemAnalyzerApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
