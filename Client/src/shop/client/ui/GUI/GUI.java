package shop.client.ui.GUI;

import shop.client.net.ShopFassade;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.User;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;

public class GUI extends JFrame {

    public static final int DEFAULT_PORT = 6789;
    private ShopInterface shop;
    private User loggedInUser;

    public GUI(String title, String host, int port) throws IOException {
        shop = new ShopFassade(host, port);

        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        initialize();
    }

    private void initialize() {

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowCloser());

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new GridBagLayout());
        add(welcomePanel, BorderLayout.CENTER);
        setSize(640, 480);
        setVisible(true);
    }

    public static void main(String[] args) {
        int portArg = 0;
        String hostArg = null;
        InetAddress ia = null;

        if (args.length > 2) {
            System.out.println("Aufruf: java <Klassenname> [<hostname> [<port>]]");
            System.exit(0);
        }
        switch (args.length) {
            case 0 -> {
                try {
                    ia = InetAddress.getLocalHost();
                } catch (Exception e) {
                    System.out.println("XXX InetAdress-Fehler: " + e);
                    System.exit(0);
                }
                hostArg = ia.getHostName(); // host ist lokale Maschine
                portArg = DEFAULT_PORT;
            }
            case 1 -> {
                portArg = DEFAULT_PORT;
                hostArg = args[0];
            }
            case 2 -> {
                hostArg = args[0];
                try {
                    portArg = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Aufruf: java GUI [<hostname> [<port>]]");
                    System.exit(0);
                }
            }
        }
        final String host = hostArg;
        final int port = portArg;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    GUI gui = new GUI("E-SHOP", host, port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
