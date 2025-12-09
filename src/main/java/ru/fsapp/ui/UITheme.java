package ru.fsapp.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Class for managing application styles and theme.
 */
public class UITheme {

    // Colors for dark theme
    public static final Color BG_PRIMARY = new Color(30, 30, 30);      // Dark background
    public static final Color BG_SECONDARY = new Color(45, 45, 45);    // Light background
    public static final Color BG_TERTIARY = new Color(55, 55, 55);     // Even lighter
    public static final Color TEXT_PRIMARY = new Color(240, 240, 240); // White text
    public static final Color TEXT_SECONDARY = new Color(180, 180, 180); // Gray text
    public static final Color ACCENT_COLOR = new Color(0, 120, 215);   // Blue accent
    public static final Color ACCENT_HOVER = new Color(0, 150, 255);   // Light blue
    public static final Color BORDER_COLOR = new Color(60, 60, 60);    // Borders
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);  // Green
    public static final Color ERROR_COLOR = new Color(244, 67, 54);    // Red

    /**
     * Applies styles to the application.
     */
    public static void applyTheme() {
        try {
            // Set "Metal" Look&Feel (works everywhere)
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

            // Override UI colors
            UIManager.put("Panel.background", BG_PRIMARY);
            UIManager.put("Panel.foreground", TEXT_PRIMARY);

            UIManager.put("Frame.background", BG_PRIMARY);
            UIManager.put("Frame.foreground", TEXT_PRIMARY);

            UIManager.put("Button.background", BG_SECONDARY);
            UIManager.put("Button.foreground", TEXT_PRIMARY);
            UIManager.put("Button.focus", ACCENT_COLOR);

            UIManager.put("TextField.background", BG_TERTIARY);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.caretForeground", TEXT_PRIMARY);
            UIManager.put("TextField.border", BorderFactory.createLineBorder(BORDER_COLOR, 1));

            UIManager.put("Tree.background", BG_SECONDARY);
            UIManager.put("Tree.foreground", TEXT_PRIMARY);
            UIManager.put("Tree.textBackground", BG_SECONDARY);
            UIManager.put("Tree.textForeground", TEXT_PRIMARY);
            UIManager.put("Tree.selectionBackground", ACCENT_COLOR);
            UIManager.put("Tree.selectionForeground", Color.WHITE);

            UIManager.put("ScrollPane.background", BG_PRIMARY);
            UIManager.put("ScrollPane.foreground", TEXT_PRIMARY);
            UIManager.put("ScrollBar.background", BG_SECONDARY);
            UIManager.put("ScrollBar.thumb", BG_TERTIARY);

            UIManager.put("Label.background", BG_PRIMARY);
            UIManager.put("Label.foreground", TEXT_PRIMARY);

            UIManager.put("TitledBorder.titleColor", ACCENT_COLOR);

        } catch (Exception e) {
            System.err.println("Error applying theme: " + e.getMessage());
        }
    }

    /**
     * Creates a styled button.
     */
    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ACCENT_COLOR);
            }
        });

        return button;
    }

    /**
     * Creates a styled text field.
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBackground(BG_TERTIARY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        return field;
    }

    /**
     * Creates a styled label for details.
     */
    public static JLabel createDetailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setForeground(TEXT_PRIMARY);
        label.setBackground(BG_PRIMARY);
        return label;
    }
}
