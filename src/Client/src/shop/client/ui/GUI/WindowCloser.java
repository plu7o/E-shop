package shop.client.ui.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowCloser extends WindowAdapter {
    @Override
    public void windowClosing(WindowEvent e) {
        Window window = e.getWindow();
        int result = JOptionPane.showConfirmDialog(window, "Are you sure you want to quit?");
        if (result == 0) {
            window.setVisible(false);
            window.dispose();
            System.exit(0);
        }
    }
}
