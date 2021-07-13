package shop.client.ui.GUI;

import shop.client.net.ShopFassade;
import shop.client.ui.GUI.panel.*;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.Article;
import shop.common.valueObject.Invoice;
import shop.common.valueObject.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class GUI extends JFrame implements LoginPanel.LoginListener, SearchArticlePanel.SearchResultListener,
        CustomerMenuPanel.CustomerMenuListener, StaffMenuPanel.StaffMenuListener, TotalBarPanel.TotalBarListener, ArticleSortPanel.ArticleSortListener {

    public static final int DEFAULT_PORT = 6789;
    private ShopInterface shop;
    private User loggedInUser;

    private LoginPanel loginPanel;
    private SearchArticlePanel searchArticlePanel;

    private ArticleTablePanel articleTablePanel;
    private JScrollPane article_ScrollPane;

    private UserTabelPanel userTabelPanel;
    private JScrollPane user_ScrollPane;

    private CartTablePanel cartTablePanel;
    private JScrollPane cart_ScrollPane;

    private TotalBarPanel totalBarPanel;
    private ArticleSortPanel articleSortPanel;
    private JPanel welcomePanel;
    private CustomerMenuPanel customerMenuPanel;
    private StaffMenuPanel staffMenuPanel;



    public GUI(String title, String host, int port) throws IOException {
        super(title);
        shop = new ShopFassade(host, port);

        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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


    private void setPanel(String panelname, String tabelname) {
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
                add(searchArticlePanel, BorderLayout.NORTH);
                customerMenuPanel = new CustomerMenuPanel(shop, this, articleTablePanel, loggedInUser);
                add(customerMenuPanel, BorderLayout.EAST);

                JPanel tablePanel = new JPanel();
                tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
                GridBagConstraints c = new GridBagConstraints();
                articleSortPanel = new ArticleSortPanel(shop, this);
                c.anchor = GridBagConstraints.FIRST_LINE_START;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;
                tablePanel.add(articleSortPanel);
                c.fill = GridBagConstraints.BOTH;
                c.ipady = 40;
                c.ipadx = 40;//make this component tall
                c.gridx = 0;
                c.gridy = 1;
                tablePanel.add(article_ScrollPane);
                add(tablePanel, BorderLayout.CENTER);

                revalidate();
                repaint();
            }
            case "staff" -> {
                getContentPane().removeAll();
                add(loginPanel, BorderLayout.WEST);
                if (tabelname.equals("User")) {
                    add(user_ScrollPane, BorderLayout.CENTER);
                } else if (tabelname.equals("Article")) {
                    add(article_ScrollPane, BorderLayout.CENTER);
                }
                add(searchArticlePanel, BorderLayout.NORTH);
                staffMenuPanel = new StaffMenuPanel(shop, this, articleTablePanel, loggedInUser);
                add(staffMenuPanel, BorderLayout.EAST);
                revalidate();
                repaint();
            }
            case "invoice" -> {
                //display invoice
            }
            case "cart" -> {
                getContentPane().removeAll();
                add(loginPanel, BorderLayout.WEST);
                add(cart_ScrollPane, BorderLayout.CENTER);
                totalBarPanel = new TotalBarPanel(shop, this, loggedInUser);
                add(totalBarPanel, BorderLayout.SOUTH);
                revalidate();
                repaint();


            }
        }
    }

    @Override
    public void onLogin(User user) {
        loggedInUser = user;
        if (loggedInUser == null) {
            setPanel("user", null);
        }
        else if (loggedInUser.isCustomer()) {
            setPanel("customer", null);
        }
        else if (loggedInUser.isStaff()) {
            setPanel("staff", "Article");
        }
    }

    @Override
    public void onSearchResult(List<Article> articles) {
        articleTablePanel.updateArticles(articles);
    }

    @Override
    public void onArticleAdded() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onArticleDeleted() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onUserAdded() {
        List<User> currentUsers = shop.getAllUsers();
        userTabelPanel.updateUser(currentUsers);
    }

    @Override
    public void onUserDeleted() {
        List<User> currentUsers = shop.getAllUsers();
        userTabelPanel.updateUser(currentUsers);
    }

    @Override
    public void onShowArticle() {
        setPanel("staff", "Article");
    }

    @Override
    public void onShowUser() {
        setPanel("staff", "User");
    }

    @Override
    public void onAddedToCart() {
        //update User shoppincart
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onRemovedFromCart() {
        //update User shoppincart
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onShowCart(Map<Article, Integer> shoppingCart) {
        cartTablePanel = new CartTablePanel(shoppingCart);
        cart_ScrollPane = new JScrollPane(cartTablePanel);
        setPanel("cart", null);
    }

    @Override
    public void onEmptyCart() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onBuy(Invoice invoice) {
        //update User shoppincart
        setPanel("invoice", null);
    }

    @Override
    public void onSortID(List<Article> sortedList) {
        List<Article> currentArticles = sortedList;
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onSortName(List<Article> sortedList) {
        List<Article> currentArticles = sortedList;
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onSortPrice(List<Article> sortedList) {
        List<Article> currentArticles = sortedList;
        articleTablePanel.updateArticles(currentArticles);
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
                    System.out.println("XXX InetAddress-Fehler: " + e);
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
    class BackMenu extends JMenu implements ActionListener {

        public BackMenu() {
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
