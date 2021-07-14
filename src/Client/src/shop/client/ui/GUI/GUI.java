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
        CustomerMenuPanel.CustomerMenuListener, StaffMenuPanel.StaffMenuListener, TotalBarPanel.TotalBarListener, ArticleSortPanel.ArticleSortListener, InvoicePanel.InvoicePanelListener {

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
    private InvoicePanel invoicePanel;



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

    /**
     * Methode Baut die GUI Panel beim initialisieren zusammen
     */
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

    /**
     * Methode erstellt Hilfs menu stellt es dar
     */
    private void setupMenu() {
        setLayout(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new HelpMenu();
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    /**
     * Methode baut die passenden Panels zusammen
     * @param panelname  gibt an welche panel zusammen gebaut werden sollen
     * @param tabelname  gibt an welche tabelle benutz werden soll
     */
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
                tablePanel.add(articleSortPanel);
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
                    JPanel tablePanel = new JPanel();
                    tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
                    GridBagConstraints c = new GridBagConstraints();
                    tablePanel.add(articleSortPanel);
                    tablePanel.add(article_ScrollPane);
                    add(tablePanel, BorderLayout.CENTER);
                    add(article_ScrollPane, BorderLayout.CENTER);
                    add(searchArticlePanel, BorderLayout.NORTH);
                }
                staffMenuPanel = new StaffMenuPanel(shop, this, articleTablePanel, loggedInUser);
                add(staffMenuPanel, BorderLayout.EAST);
                revalidate();
                repaint();
            }
            case "invoice" -> {
                getContentPane().removeAll();
                add(loginPanel, BorderLayout.WEST);
                add(invoicePanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
            case "cart" -> {
                getContentPane().removeAll();
                add(loginPanel, BorderLayout.WEST);
                JPanel tablePanel = new JPanel();
                tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.PAGE_AXIS));
                tablePanel.add(cart_ScrollPane);
                totalBarPanel = new TotalBarPanel(shop, this, loggedInUser);
                tablePanel.add(totalBarPanel, BorderLayout.SOUTH);
                add(tablePanel, BorderLayout.CENTER);

                revalidate();
                repaint();
            }
        }
    }

    /**
     * Imlemtierung der LoginListener methode
     * ruft setPanel() auf und übergibt dan passenden Panel name und Table name
     * @param user       eingeloggter User
     */
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

    /**
     * Implemtierung der SearchResultListener Methode
     * updated die Artikel tabelle mit der passenden Artikeln liste
     * @param articles   Liste von Artikeln
     */
    @Override
    public void onSearchResult(List<Article> articles) {
        articleTablePanel.updateArticles(articles);
    }

    /**
     * Implemtierung der StaffMenuListener methode
     * updated die Artikel tabelle mit der passenden Artikeln liste
     */
    @Override
    public void onArticleAdded() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }
    /**
     * Implemtierung der StaffMenuListener methode
     * updated die Artikel tabelle mit der passenden Artikeln liste
     */
    @Override
    public void onArticleDeleted() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }
    /**
     * Implemtierung der StaffMenuListener methode
     * updated die User tabelle mit der passenden User liste
     */
    @Override
    public void onUserAdded() {
        List<User> currentUsers = shop.getAllUsers();
        userTabelPanel.updateUser(currentUsers);
    }

    /**
     * Implemtierung der StaffMenuListener methode
     * updated die User tabelle mit der passenden User liste
     */
    @Override
    public void onUserDeleted() {
        List<User> currentUsers = shop.getAllUsers();
        userTabelPanel.updateUser(currentUsers);
    }

    /**
     * Implemtierung der StaffMenuListener methode
     * ruft setPanel auf und übergibt den passen panelname und tablename
     */
    @Override
    public void onShowArticle() {
        setPanel("staff", "Article");
    }

    /**
     * Implemtierung der StaffMenuListener methode
     * ruft setPanel auf und übergibt den passen panelname und tablename
     */
    @Override
    public void onShowUser() {
        setPanel("staff", "User");
    }

    /**
     * Implemtierung der CustomerMenuListener Methode
     * updated die Artikel tabelle mit der passenden Artikeln liste
     */
    @Override
    public void onAddedToCart() {
        //update User shoppincart
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }


    /**
     * Implemtierung der CustomerMenuListener Methode
     * updated die Artikel tabelle mit der passenden Artikeln liste
     */
    @Override
    public void onRemovedFromCart() {
        //update User shoppincart
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }


    /**
     * Implemtierung der CustomerMenuListener Methode
     * erzeugt eine neues CartTablePanel damit wird ein JScrollPane objekt erzuegt
     * ruft dann setPanel auf
     * @param shoppingCart       Warenkorb des Kunden
     */
    @Override
    public void onShowCart(Map<Article, Integer> shoppingCart) {
        cartTablePanel = new CartTablePanel(shoppingCart);
        cart_ScrollPane = new JScrollPane(cartTablePanel);
        setPanel("cart", null);
    }

    /**
     * Implemtierung der CustomerMenuListener Methode
     * updated die Artikel tabelle mit der passenden Artikeln liste
     */
    @Override
    public void onEmptyCart() {
        List<Article> currentArticles = shop.getAllArticles();
        articleTablePanel.updateArticles(currentArticles);
    }

    /**
     * Implemtierung der CustomerMenuListener Methode
     * Erzuegt neues InvoicePanel mit invoice
     * ruft setPanel auf
     * @param invoice    rechnungs objekt
     */
    @Override
    public void onBuy(Invoice invoice) {
        //update User shoppincart
        invoicePanel = new InvoicePanel(invoice, this);
        setPanel("invoice", null);
    }

    /**
     * Implemtierung der ArticleSortListener Methode
     * updated die Artikel tabelle mit der passenden sortierten Artikel liste
     * @param sortedList        Sortierte Liste
     */
    @Override
    public void onSortID(List<Article> sortedList) {
        List<Article> currentArticles = sortedList;
        articleTablePanel.updateArticles(currentArticles);
    }

    /**
     * Implemtierung der ArticleSortListener Methode
     * updated die Artikel tabelle mit der passenden sortierten Artikel liste
     * @param sortedList        Sortierte Liste
     */
    @Override
    public void onSortName(List<Article> sortedList) {
        List<Article> currentArticles = sortedList;
        articleTablePanel.updateArticles(currentArticles);
    }

    /**
     * Implemtierung der ArticleSortListener Methode
     * updated die Artikel tabelle mit der passenden sortierten Artikel liste
     * @param sortedList        Sortierte Liste
     */
    @Override
    public void onSortPrice(List<Article> sortedList) {
        List<Article> currentArticles = sortedList;
        articleTablePanel.updateArticles(currentArticles);
    }

    @Override
    public void onSortStock(List<Article> sortedList) {
        List<Article> currentArticles = sortedList;
        articleTablePanel.updateArticles(currentArticles);
    }

    /**
     * Implemtierung der InvoicePanelListener Methode
     * ruft setPanel auf um die passenden Panels anzuzeigen
     */
    @Override
    public void onAccept() {
        setPanel("customer", null);
    }

    /**
     * Implemtierung der TotalBarListener Methode
     * ruft setPanel auf um die passenden Panels anzuzeigen
     */
    @Override
    public void onBack() {
        setPanel("customer", null);
    }

    /**
     * Main Methode der GUI
     * holt sich die Netzwerk information falls nicht vorhanden
     * Erzeugt GUI objekt mit eigenen Thread
     */
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

    /**
     * Class HelpMenu
     * erzeugt Hilfs Menu aus einzelnen Komponenten
     */
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
