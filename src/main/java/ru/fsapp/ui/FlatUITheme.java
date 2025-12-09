package ru.fsapp.ui;

import javax.swing.*;
import java.awt.*;

/**
 * UI theme for application with flat design.
 * Contains constants for colors, fonts and element styles.
 *
 * @author Ahmed
 * @version 1.0
 * @since 2025-12-05
 */
public class FlatUITheme {

    // Base colors
    public static final Color BG_PRIMARY = new Color(240, 242, 245);
    public static final Color BG_SECONDARY = new Color(255, 255, 255);
    public static final Color TEXT_PRIMARY = new Color(31, 35, 40);
    public static final Color TEXT_SECONDARY = new Color(101, 109, 118);
    public static final Color BORDER = new Color(208, 215, 222);
    public static final Color ACCENT = new Color(30, 136, 229);

    // Status colors
    public static final Color SUCCESS = new Color(52, 168, 83);
    public static final Color ERROR = new Color(229, 57, 53);
    public static final Color INFO = new Color(66, 133, 244);
    public static final Color WARNING = new Color(251, 188, 52);

    /**
     * Creates a button in interface style.
     *
     * @param text button text
     * @return styled button
     */
    public static JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(25, 118, 210));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT);
            }
        });

        return btn;
    }

    /**
     * Creates a text field in interface style.
     *
     * @return styled text field
     */
    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(BG_SECONDARY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        return tf;
    }
}
