package shop.client.ui.GUI;

import shop.client.net.ShopFassade;
import shop.client.ui.GUI.panel.*;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.Article;
import shop.common.valueObject.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import shop.common.valueObject.Invoice;
import java.util.Map;

public class GUI extends JFrame implements LoginPanel.LoginListener, SearchArticlePanel.SearchResultListener, CustomerMenuPanel.CustomerMenuListener {

    public static final int DEFAULT_PORT = 6789;
    private ShopInterface shop;
    private User loggedInUser;

    private LoginPanel loginPanel;
    private SearchArticlePanel searchArticlePanel;
    private ArticleTablePanel articleTablePanel;
    private UserTabelPanel userTabelPanel;
    private JScrollPane user_ScrollPane;
    private JScrollPane article_ScrollPane;
    private JPanel welcomePanel;
    private CustomerMenuPanel customerMenuPanel;


    public GUI(String title, String host, int port) throws IOException {
        super(title);
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

    private void initialize() throws IOException {

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowCloser());

        setupMenu();

        searchArticlePanel = new SearchArticlePanel(shop, this);
        loginPanel = new LoginPanel(shop, this);

        List<Article> articles = shop.getAllArticles();
        articleTablePanel = new ArticleTablePanel(articles);
        article_ScrollPane = new JScrollPane(articleTablePanel);

        List<User> users = shop.getAllUsers();
        userTabelPanel = new UserTabelPanel(users);
        user_ScrollPane = new JScrollPane(userTabelPanel);

        JLabel welcome = new JLabel("Welcome to", SwingConstants.CENTER);
        JLabel name = new JLabel("E-SHOP", SwingConstants.CENTER);
        welcome.setFont(new Font("Arial", Font.BOLD, 25));
        name.setFont(new Font("Arial", Font.BOLD, 35));
        JPanel titlepanel = new JPanel();
        titlepanel.setLayout(new GridLayout(2, 1));
        titlepanel.add(welcome);
        titlepanel.add(name);
        welcomePanel = new JPanel();
        welcomePanel.setLayout(new GridBagLayout());
        welcomePanel.add(titlepanel);
        welcomePanel.setBorder(BorderFactory.createBevelBorder(10, Color.white, Color.black));

        add(loginPanel, BorderLayout.WEST);
        add(welcomePanel, BorderLayout.CENTER);
        setSize(640, 480);
        setVisible(true);
    }

    private void setupMenu() {
        setLayout(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new HelpMenu();
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void setPanel(String panelname) {
        switch (panelname) {
            case "user" -> {
                getContentPane().removeAll();
                add(loginPanel, BorderLayout.WEST);
                add(welcomePanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
            case "customer" -> {
                getContentPane().removeAll();
                add(loginPanel, BorderLayout.WEST);
                add(article_ScrollPane, BorderLayout.CENTER);
                add(searchArticlePanel, BorderLayout.NORTH);
                customerMenuPanel = new CustomerMenuPanel(shop, this, articleTablePanel, loggedInUser);
                add(customerMenuPanel, BorderLayout.EAST);
                revalidate();
                repaint();
            }
            case "staff" -> {
                getContentPane().removeAll();
                add(loginPanel, BorderLayout.WEST);
                add(user_ScrollPane, BorderLayout.CENTER);
                add(searchArticlePanel, BorderLayout.NORTH);
                revalidate();
                repaint();
            }
            case "invoice" -> {

            }
            case "cart" -> {

            }
        }
    }

    @Override
    public void onLogin(User user) {
        loggedInUser = user;
        if (loggedInUser == null) {
            setPanel("user");
        }
        else if (loggedInUser.isCustomer()) {
            setPanel("customer");
        }
        else if (loggedInUser.isStaff()) {
            setPanel("staff");
        }
    }

    @Override
    public void onSearchResult(List<Article> articles) {
        articleTablePanel.updateArticles(articles);
    }

    @Override
    public void onAddedToCart() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onRemovedFromCart() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onShowCart(Map shoppingCart) {

        setPanel("cart");
    }

    @Override
    public void onBuy(Invoice invoice) {

        setPanel("invoice");
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
    class HelpMenu extends JMenu implements ActionListener {

        public HelpMenu() {
            super("Help");

            JMenu m = new JMenu("About");
            JMenuItem mi = new JMenuItem("Programmers");
            mi.addActionListener(this);
            m.add(mi);
            mi = new JMenuItem("Stuff");
            mi.addActionListener(this);
            m.add(mi);
            this.add(m);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("something"+ e.getActionCommand());
        }
    }
}
