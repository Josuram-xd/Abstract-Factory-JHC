package com.legalai;

import com.formdev.flatlaf.FlatLightLaf;
import com.legalai.ui.swing.AnalizadorFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainSwing {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ProgressBar.arc", 10);
        UIManager.put("TabbedPane.selectedBackground", java.awt.Color.WHITE);

        SwingUtilities.invokeLater(() -> new AnalizadorFrame().setVisible(true));
    }
}
