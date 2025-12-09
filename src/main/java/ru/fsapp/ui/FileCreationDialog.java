package ru.fsapp.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for creating new files.
 * Allows user to select file name and its extension.
 *
 * @author Ahmed
 * @version 1.0
 * @since 2025-12-05
 */
public class FileCreationDialog extends JDialog {
    private JTextField nameField;
    private JComboBox<String> extensionCombo;
    private boolean confirmed = false;

    private static final String[] EXTENSIONS = {
            "txt", "pdf", "doc", "docx", "xls", "xlsx",
            "csv", "json", "xml", "html", "css", "js",
            "java", "py", "cpp", "c", "h", "sql",
            "md", "log", "ini", "conf", "yaml", "yml"
    };

    public FileCreationDialog(JFrame parent) {
        super(parent, "Create New File", true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setResizable(false);

        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(FlatUITheme.BG_PRIMARY);
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // File name field
        JLabel nameLabel = new JLabel("File name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        nameLabel.setForeground(FlatUITheme.TEXT_PRIMARY);
        main.add(nameLabel);
        main.add(Box.createVerticalStrut(5));

        nameField = FlatUITheme.createTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        main.add(nameField);
        main.add(Box.createVerticalStrut(12));

        // Extension selection
        JLabel extLabel = new JLabel("File type (extension):");
        extLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        extLabel.setForeground(FlatUITheme.TEXT_PRIMARY);
        main.add(extLabel);
        main.add(Box.createVerticalStrut(5));

        extensionCombo = new JComboBox<>(EXTENSIONS);
        extensionCombo.setSelectedItem("txt");
        extensionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        extensionCombo.setBackground(FlatUITheme.BG_SECONDARY);
        extensionCombo.setForeground(FlatUITheme.TEXT_PRIMARY);
        main.add(extensionCombo);
        main.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(FlatUITheme.BG_PRIMARY);

        JButton okBtn = FlatUITheme.createButton("Create");
        okBtn.addActionListener(e -> onOk());
        buttons.add(okBtn);

        JButton cancelBtn = FlatUITheme.createButton("Cancel");
        cancelBtn.addActionListener(e -> onCancel());
        buttons.add(cancelBtn);

        main.add(buttons);

        add(main);
    }

    private void onOk() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter file name!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        confirmed = true;
        dispose();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getFileName() {
        return nameField.getText().trim();
    }

    public String getExtension() {
        return (String) extensionCombo.getSelectedItem();
    }
}
