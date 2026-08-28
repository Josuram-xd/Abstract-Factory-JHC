package com.legalai;

import com.legalai.ui.swing.AnalizadorFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainSwing {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, se usa el look and feel por defecto de Swing.
        }
        SwingUtilities.invokeLater(() -> new AnalizadorFrame().setVisible(true));
    }
}
