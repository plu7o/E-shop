package shop.client.net;

import shop.common.exceptions.ArticleAlreadyExistsException;
import shop.common.exceptions.ArticleNotFoundException;
import shop.common.exceptions.LoginFailedException;
import shop.common.exceptions.UserAlreadyExistsException;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.Article;
import shop.common.valueObject.Invoice;
import shop.common.valueObject.MassArticle;
import shop.common.valueObject.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Klasse mit Fassade des Shop's auf Client-Seite.
 * Die Klasse stellt die von der GUI/CUI erwarteten Methoden zur Verfügung
 * und realisiert (transparent für die GUI) die Kommunikation mit dem
 * Server.
 *
 * @author Ricard
 */
public class ShopFassade implements ShopInterface {

    private User loggedInUser;
    private Socket socket = null;
    private BufferedReader sin;
    private PrintStream sout;


    /**
     * Konstruktor, der die Verbindung zum Server aufbaut (Socket) und dieser
     * Grundlage Eingabe- und Ausgabestreams für die Kommunikation mit dem
     * Server erzeugt.
     *
     * @param host Rechner, auf dem der Server läuft
     * @param port Port, auf dem der Server auf Verbindungsanfragen warten
     * @throws IOException
     */

    public ShopFassade(String host, int port) throws IOException {
        try {
            // Socket-Objekt fuer die Kommunikation mit Host/Port erstellen
            socket = new Socket(host, port);

            // Stream-Objekt für Text I/O über Socket erzeugen
            InputStream is = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            sin = new BufferedReader(new InputStreamReader(is));
            sout = new PrintStream(out, true);


        } catch (IOException e) {
            System.err.println("Error opening socket stream: " + e);
            // Wenn im "try"-Block Fehler auftreten, dann Socket schließen:
            if (socket != null)
                socket.close();
            System.err.println("Socket Closed");
            System.exit(0);
        }
        // Verbindung erfolgreich hergestellt: IP-Adresse und Port ausgeben
        System.err.println("Connected: " + socket.getInetAddress() + ":"
                + socket.getPort());

        // Begrüßungsmeldung vom Server lesen
        String message = sin.readLine();
        System.out.println(message);

    }

    private Article readArticlefromServer() throws IOException {
        String reply;

        reply = sin.readLine();
        int articleNr = Integer.parseInt(reply);
        String name = sin.readLine();
        reply = sin.readLine();
        double price = Double.parseDouble(reply);
        reply = sin.readLine();
        int stock = Integer.parseInt(reply);
        reply = sin.readLine();
        boolean available = Boolean.parseBoolean(reply);

        return new Article(name, articleNr, price, stock, available);
    }

    private Invoice readInvoiceFromServer() throws IOException {
        String reply;
        HashMap<Article, Integer> shoppingCart = new HashMap<>();

        String name = sin.readLine();
        reply = sin.readLine();
        int userId = Integer.parseInt(reply);
        try {
            reply = sin.readLine();
            int shoppingCartSize = Integer.parseInt(reply);
            for (int i = 0; i < shoppingCartSize; i++) {
                Article article = readArticlefromServer();
                reply = sin.readLine();
                int value = Integer.parseInt(reply);
                shoppingCart.put(article, value);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return new Invoice(userId, name, shoppingCart);
    }

    private Article readMassArticlefromServer() throws IOException {
        String reply;

        String name = sin.readLine();
        reply = sin.readLine();
        int articleNr = Integer.parseInt(reply);
        reply = sin.readLine();
        double price = Double.parseDouble(reply);
        reply = sin.readLine();
        int stock = Integer.parseInt(reply);
        reply = sin.readLine();
        boolean available = Boolean.parseBoolean(reply);
        reply = sin.readLine();
        int packageSize = Integer.parseInt(reply);

        return new MassArticle(name, articleNr, price, stock, available, packageSize);
    }

    private User readUserFromServer() throws IOException, ClassNotFoundException {
        String reply;
        HashMap<Article, Integer> shoppingCart = new HashMap<>();

        String name = sin.readLine();
        reply = sin.readLine();
        int userNr = Integer.parseInt(reply);
        String username = sin.readLine();
        String password = sin.readLine();
        reply = sin.readLine();
        boolean staff = Boolean.parseBoolean(reply);
        reply = sin.readLine();
        boolean customer = Boolean.parseBoolean(reply);
        String address = sin.readLine();
        try {
            reply = sin.readLine();
            int shoppingCartSize = Integer.parseInt(reply);
            for (int i = 0; i < shoppingCartSize; i++) {
                Article article = readArticlefromServer();
                reply = sin.readLine();
                int value = Integer.parseInt(reply);
                shoppingCart.put(article, value);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        User user = new User(name, userNr, username, password, staff, customer, address);
        user.getShoppingCart().setCart(shoppingCart);
        return user;
    }


    /**
     * Methode zum Beenden einer Verbindung zum Server.
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        // Kennzeichen für gewählte Aktion senden
        sout.println("q");
        // (Parameter sind hier nicht zu senden)

        // Antwort vom Server lesen:
        String reply = "ERROR";
        try {
            reply = sin.readLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println(reply);
    }

    /**
     * Methode, die eine Liste mit allen im Bestand erhältlichen Artikel zurückgibt.
     *
     * @return Liste mit allen erhältlichen Artikel im Bestand des Shops.
     */
    public List<Article> getAllAvailableArticles() {
        List<Article> articles = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("getAllAvailableArticles");

        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int articlesSize = Integer.parseInt(reply);
            for (int i = 0; i < articlesSize; i++) {
                Article article = readArticlefromServer();
                articles.add(article);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return articles;
    }

    /**
     * Methode, die eine Liste mit allen Artikel zurückgibt.
     *
     * @return Liste mit allen Artikel im Bestand des Shops.
     */
    public List<Article> getAllArticles() {
        List<Article> articles = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("getAllArticles");

        // Antwort vom Server lesen
        String reply = "?";
        try {

            reply = sin.readLine();
            int articlesSize = Integer.parseInt(reply);
            for (int i = 0; i < articlesSize; i++) {
                Article article = readArticlefromServer();
                articles.add(article);

            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return articles;
    }

    /**
     * Methode zum Suchen von Artikel anhand des Namens. Es wird eine Liste von Artikeln
     * zurückgegeben, die alle Artikel mit exakt übereinstimmendem Namen enthält.
     *
     * @param name Name des gesuchten Artikels
     * @return Liste der gefundenen Artikel (evtl. leer)
     */
    public List<Article> searchArticle(String name) {
        List<Article> articles = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("searchArticle");
        // Parameter für Aktion senden
        sout.println(name);
        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int articlesSize = Integer.parseInt(reply);
            for (int i = 0; i < articlesSize; i++) {
                Article article = readArticlefromServer();
                articles.add(article);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return articles;
    }

    /**
     * Durchsucht das Inventar und gibt den gewünschten Artikel, falls vorhanen, zurück
     *
     * @param articleNr Artikelnummer des gewünschten Artikels
     * @return Article, falls vorhanden
     * @throws ArticleNotFoundException
     */
    public Article getArticle(int articleNr) throws ArticleNotFoundException {
        Article article = null;
        // Server Command für gewählte Aktion
        sout.println("getArticle");
        // Parameter für Aktion senden
        sout.println(articleNr);
        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                article = readArticlefromServer();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());

        }
        return article;
    }

    /**
     * Methode zum Einfügen eines neuen Artikels in den Bestand.
     * Wenn der Artikel bereits im Bestand ist, wird der Bestand nicht geändert.
     *
     * @param loggedInUser User der den Artikel eingefügen will
     * @param name         Name des Artikels
     * @param price        Preis des Artikels
     * @param stock        Bestand des Artikels
     * @param available    ob der artikel auf bestand ist oder nicht
     * @throws ArticleAlreadyExistsException wenn der Artikel bereits existiert
     */
    public void addArticle(User loggedInUser, String name, double price, int stock, boolean available) throws ArticleAlreadyExistsException {
        sout.println("addArticle");
        sout.println(loggedInUser.getUserNr());
        sout.println(name);
        sout.println(price);
        sout.println(stock);
        sout.println(available);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Article added");
            } else {
                String message = sin.readLine();
                throw new ArticleAlreadyExistsException(null, message);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode zum Einfügen eines neuen Massenartikels in den Bestand.
     * Wenn der Massenartikel bereits im bestand ist, wird der Bestand nicht geändert
     *
     * @param loggedInUser User der den Massenartikel eingefügen will
     * @param name         Name des Massenartikels
     * @param price        Preis des Massenartikels
     * @param stock        Bestand des Massenartikels
     * @param available    ob der Massenartikels auf Bestand ist oder nicht
     * @param packageSize  Größe einer Packung
     * @throws ArticleAlreadyExistsException, wenn der Massenartikel bereits existiert
     */
    public void addMassArticle(User loggedInUser, String name, double price, int stock, boolean available, int packageSize) throws ArticleAlreadyExistsException {
        sout.println("addMassArticle");
        sout.println(loggedInUser.getUserNr());
        sout.println(name);
        sout.println(price);
        sout.println(stock);
        sout.println(available);
        sout.println(packageSize);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Mass Article added");
            } else {
                // Fehler: Exception (re-)konstruieren
                String message = sin.readLine();
                throw new ArticleAlreadyExistsException(null, message);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode zum Löschen eines Artikels aus dem Bestand.
     *
     * @param loggedInUser User der den Artikel löschen will
     * @param articleNr    Nummer des Artikels
     */
    public void deleteArticle(User loggedInUser, int articleNr) {
        sout.println("deleteArticle");
        sout.println(loggedInUser.getUserNr());
        sout.println(articleNr);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Article Deleted");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode um den Artikel zu bearbeiten
     *
     * @param loggedInUser User der den Artikel berabeitet
     * @param article      Artikel der geändert werden soll
     * @param name         Name des Artikels
     * @param price        Preis des Artikels
     * @param stock        Bestand des Artikels
     * @param available    Erhälichkeit des Artikels
     */
    public void updateArticleData(User loggedInUser, Article article, String name, double price, int stock, boolean available) {
        sout.println("updateArticleData");
        sout.println(loggedInUser.getUserNr());
        sout.println(article.getArticleNr());
        sout.println(name);
        sout.println(price);
        sout.println(stock);
        sout.println(available);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Article Updated");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void updateMassArticleData(User loggedInUser, MassArticle article, String name, double price, int stock, boolean available, int packageSize) {

    }

    /**
     * Methode, um den Bestand eines Artikels zu erhöhen
     *
     * @param loggedInUser User der den Bestand erhöhen will
     * @param article      Artikel dessen bestand erhöht wird
     * @param amount       Anzahl mit der der bestand erhöht werden soll
     */
    public void addStock(User loggedInUser, Article article, int amount) {
        sout.println("addStock");
        sout.println(loggedInUser.getUserNr());
        sout.println(article.getArticleNr());
        sout.println(amount);


        String reply = "ERROR";
        try {
            reply = sin.readLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        //System.out.println(reply);
    }

    /**
     * Methode, um den Bestand eines Artikels zu reduzieren
     *
     * @param loggedInUser User der den Bestand reduzieren will
     * @param article      Artikel dessen bestand reduziert wird
     * @param amount       Anzahl mit der der bestand reduziert wird
     */
    public void reduceStock(User loggedInUser, Article article, int amount) {
        sout.println("reduceStock");
        sout.println(loggedInUser.getUserNr());
        sout.println(article.getArticleNr());
        sout.println(amount);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Stock Reduced");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode, die eine Liste mit allen Kunden zurückgibt.
     *
     * @return Liste mit allen Kunden im KundenBestand des Shops.
     */
    public List<User> getCustomers() {
        List<User> customers = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("getCustomers");

        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int customersSize = Integer.parseInt(reply);
            for (int i = 0; i < customersSize; i++) {
                User customer = readUserFromServer();
                customers.add(customer);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return customers;
    }

    /**
     * Methode, die eine Liste mit allen Mitarbeiter zurückgibt.
     *
     * @return Liste mit allen Mitarbeiter im MitarbeiterBestand des Shops.
     */
    public List<User> getStaff() {
        List<User> staff = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("getStaff");

        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int customersSize = Integer.parseInt(reply);
            for (int i = 0; i < customersSize; i++) {
                User customer = readUserFromServer();
                staff.add(customer);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return staff;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("getAllUsers");

        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int customersSize = Integer.parseInt(reply);
            for (int i = 0; i < customersSize; i++) {
                User user = readUserFromServer();
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return users;
    }

    /**
     * Methode zum Suchen von Kunden anhand der UserID. Es wird eine Liste von Kunden
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten Kunden
     * @return Liste der gefundenen Kunden (evtl. leer)
     */
    public List<User> searchCustomer(int userID) {
        List<User> customers = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("searchCustomer");
        sout.println(userID);
        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int customersSize = Integer.parseInt(reply);
            for (int i = 0; i < customersSize; i++) {
                User customer = readUserFromServer();
                customers.add(customer);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return customers;
    }

    /**
     * Methode zum Suchen von User anhand der UserID. Es wird eine Liste von User
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten User
     * @return Liste der gefundenen User (evtl. leer)
     */
    public List<User> searchUsers(int userID) {
        List<User> users = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("searchUsers");
        sout.println(userID);

        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int usersSize = Integer.parseInt(reply);
            for (int i = 0; i < usersSize; i++) {
                User user = readUserFromServer();
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return users;
    }

    /**
     * Methode zum Suchen von Mitarbeiter anhand der UserID. Es wird eine Liste von Mitarbeiter
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten Mitarbeiter
     * @return Liste der gefundenen Mitarbeiter (evtl. leer)
     */
    public List<User> searchStaff(int userID) {
        List<User> staff = new ArrayList<>();

        // Server Command für gewählte Aktion
        sout.println("searchStaff");
        sout.println(userID);
        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            int staffSize = Integer.parseInt(reply);
            for (int i = 0; i < staffSize; i++) {
                User user = readUserFromServer();
                staff.add(user);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return staff;
    }

    /**
     * Methode zum Einfügen eines neuen Kunden.
     * Wenn der Kunden bereits im Kundenbestand ist, wird der KundenBestand nicht geändert
     *
     * @param name     Name des Kunden
     * @param username Username des Kunden
     * @param password Passwort des Kunden
     * @return User-Objekt, das im Erfolgsfall eingefügt wurde
     * @throws UserAlreadyExistsException, wenn der Kunde bereits existiert
     */
    public User addCustomer(String name, String username, String password) throws UserAlreadyExistsException {
        sout.println("addCustomer");
        // Parameter für Aktion senden
        sout.println(name);
        sout.println(username);
        sout.println(password);

        // Antwort vom Server lesen:
        String antwort = "ERROR";
        try {
            antwort = sin.readLine();
            if (antwort.equals("SUCCESS")) {
                // Eingefügtes Buch vom Server lesen ...
                User user = readUserFromServer();
                // ... und zurückgeben
                return user;
            } else {
                // Fehler: Exception (re-)konstruieren
                String message = sin.readLine();
                throw new UserAlreadyExistsException(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Methode zum Einfügen eines neuen Mitarbeiter.
     * Wenn der Mitarbeiter bereits im Mitarbeiterbestand ist, wird der MitarbeiterBestand nicht geändert
     *
     * @param loggedInUser User der den Mitarbeiter hinzufügen will
     * @param name         Name des Mitarbeiter
     * @param username     Username des Mitarbeiter
     * @param password     Passwort des Mitarbeiter
     * @return User-Objekt, das im Erfolgsfall eingefügt wurde
     * @throws UserAlreadyExistsException, wenn der Mitarbeiter bereits existiert
     */
    public User addStaff(User loggedInUser, String name, String username, String password) throws UserAlreadyExistsException {
        sout.println("addStaff");
        // Parameter für Aktion senden
        sout.println(loggedInUser.getUserNr());
        sout.println(name);
        sout.println(username);
        sout.println(password);

        // Antwort vom Server lesen:
        String antwort = "ERROR";
        try {
            antwort = sin.readLine();
            if (antwort.equals("SUCCESS")) {
                // Eingefügtes Buch vom Server lesen ...
                User user = readUserFromServer();
                // ... und zurückgeben
                return user;
            } else {
                // Fehler: Exception (re-)konstruieren
                String message = sin.readLine();
                throw new UserAlreadyExistsException(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Methode zum Löschen eines Users.
     *
     * @param loggedInUser User der den User löschen will
     * @param user         User Objekt das gelöscht werden soll
     */
    public void deleteUser(User loggedInUser, User user) {
        sout.println("deleteUser");
        sout.println(loggedInUser.getUserNr());
        sout.println(user.getUserNr());

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("User Deleted");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Durchsucht die User Liste und gibt den gewünschten User, falls vorhanen, zurück
     *
     * @param userNr
     * @return User, falls vorhanden
     */
    public User getUser(int userNr) {
        User user = null;
        // Server Command für gewählte Aktion
        sout.println("getUser");
        // Parameter für Aktion senden
        sout.println(userNr);
        // Antwort vom Server lesen
        String reply = "?";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                user = readUserFromServer();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());

        }
        return user;
    }

    /**
     * Methode um den User zu bearbeiten
     *
     * @param loggedInUser User der den Artikel berabeitet
     * @param user         user der geändert werden soll
     * @param name         Name des Users
     * @param username     Username des Users
     * @param password     passwort des Users
     * @param address      Adresse des Users
     */
    public void updateUserData(User loggedInUser, User user, String name, String username, String password, String address) {
        sout.println("updateUserData");
        sout.println(loggedInUser.getUserNr());
        sout.println(user.getUserNr());
        sout.println(name);
        sout.println(username);
        sout.println(password);
        sout.println(address);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("User Updated");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode, zum einlogen
     * Checkt ob Username und Passwort mit einem User im Userbestand übereinstimmt
     *
     * @param username Username des Users
     * @param password Passwort des Users
     * @return User, falls Username und Passwort übereinstimmt
     * @throws LoginFailedException, wenn Username und Passwort nicht übereinstimmen
     */
    public User login(String username, String password) throws LoginFailedException {
        sout.println("login");
        sout.println(username);
        sout.println(password);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                loggedInUser = readUserFromServer();
                return loggedInUser;
            } else {
                String message = sin.readLine();
                throw new LoginFailedException(message);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /**
     * Methode, zum Registrieren von neuen Kunden
     * Checkt ob Kunde schon im Kundenbestand existiert
     *
     * @param name     Name des Users
     * @param username Username des Users
     * @param password Passwort des Users
     * @throws UserAlreadyExistsException
     */
    public void signup(String name, String username, String password) throws UserAlreadyExistsException {
        sout.println("signup");
        sout.println(name);
        sout.println(username);
        sout.println(password);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Successfully Registered");
            } else {
                String message = sin.readLine();
                throw new UserAlreadyExistsException(message);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode zum Speichern des Userbestands in einer Datei.
     *
     * @throws IOException
     */
    public void saveUser() throws IOException {
        sout.println("saveUser");

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("User saved");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode zum Speichern des Artikelbestands in einer Datei.
     *
     * @throws IOException
     */
    public void saveArticle() throws IOException {
        sout.println("saveArticle");

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Articles saved");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode zum Speichern des Massen-Artikelbestands in einer Datei.
     *
     * @throws IOException
     */
    public void saveMassArticle() throws IOException {
        sout.println("saveMassArticle");

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Mass Articles saved");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode, um ein Artikel dem Warenkorb eines Kunden hinzufügen
     *
     * @param user    User mit Warenkorb
     * @param article Artikel der hinzugefügt wird
     * @param amount  Anzahl der Artikel die hinzugefügt werden
     */
    public void addToCart(User user, Article article, int amount) {
        sout.println("addToCart");
        sout.println(user.getUserNr());
        sout.println(article.getArticleNr());
        sout.println(amount);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Article added to cart");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode, um ein Artikel aus dem Warenkorb eines User zu entfernen
     *
     * @param user    User mit Warenkorb
     * @param article Artikel der entfernt wird
     * @param amount  Anzahl der Artikel die entfernt werden
     */
    public void removeFromCart(User user, Article article, int amount) {
        sout.println("removeFromCart");
        sout.println(user.getUserNr());
        sout.println(article.getArticleNr());
        sout.println(amount);

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Article removed from cart");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode die den Warenkorb eines Kunden Leert
     *
     * @param user User mit Warenkorb
     */
    public void emptyCart(User user) {
        sout.println("emptyCart");
        sout.println(user.getUserNr());

        String reply = "ERROR";
        try {
            reply = sin.readLine();
            if (reply.equals("SUCCESS")) {
                System.out.println("Cart emptied");
            } else {
                String message = sin.readLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Methode, die ein Rechnungs-Objekt mit allen Artikel die sich im Warenkorb befinden erzeugt
     * Rechnungs-Objekt wird beim Kauf erzeugt und enthält Rechnungs datum, Artikel, Kunde und Preis
     *
     * @param user User der den Kauf getätigt hat
     * @return Rechnung
     */
    public Invoice buy(User user) {
        Invoice invoice = null;
        sout.println("buy");
        sout.println(user.getUserNr());

        try {
            invoice = readInvoiceFromServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return invoice;
    }
}
