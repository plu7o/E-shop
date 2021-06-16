package shop.client.net;
import jdk.incubator.vector.Vector;
import shop.common.exceptions.ArticleAlreadyExistsException;
import shop.common.exceptions.ArticleNotFoundException;
import shop.common.exceptions.LoginFailedException;
import shop.common.exceptions.UserAlreadyExistsException;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasse mit Fassade des Shop's auf Client-Seite.
 * Die Klasse stellt die von der GUI/CUI erwarteten Methoden zur Verfügung
 * und realisiert (transparent für die GUI) die Kommunikation mit dem
 * Server.
 * @author Ricardo, Bennet, FLorian
 */
public class ShopFassade implements ShopInterface{

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
            sout = new PrintStream(out);

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

        String name = sin.readLine();
        reply = sin.readLine();
        int articleNr = Integer.parseInt(reply);
        reply = sin.readLine();
        double price = Double.parseDouble(reply);
        reply = sin.readLine();
        int stock = Integer.parseInt(reply);
        reply = sin.readLine();
        boolean available = Boolean.parseBoolean(reply);

        return new Article(name, articleNr, price,  stock, available);
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

        return new MassArticle( name, articleNr, price,  stock, available, packageSize);
    }

    private User readUserFromServer() throws IOException {
        String reply;

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

        return new User (name, userNr, username, password, staff, customer, address);
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
        String antwort = "Fehler";
        try {
            antwort = sin.readLine();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println(antwort);
    }

    /**
     * Methode, die eine Liste mit allen im Bestand erhältlichen Artikel zurückgibt.
     * @return Liste mit allen erhältlichen Artikel im Bestand des Shops.
     */
    public List<Article> getAllAvailableArticles() {
        List<Article> articles = new Vector<>();

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
     * @return Liste mit allen Artikel im Bestand des Shops.
     */
    public List<Article> getAllArticles() {
        List<Article> articles = new Vector<>();

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
        List<Article> articles = new Vector<>();

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
     * @throws ArticleNotFoundException
     * @return Article, falls vorhanden
     */
    public Article getArticle(int articleNr) throws ArticleNotFoundException {
        Article article;
        // Server Command für gewählte Aktion
        sout.println("getArticle");
        // Parameter für Aktion senden
        sout.println(articleNr);
        // Antwort vom Server lesen
        String reply = "?";
        try {
            article = readArticlefromServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
        return article;
    }

    /**
     * Methode zum Einfügen eines neuen Artikels in den Bestand.
     * Wenn der Artikel bereits im Bestand ist, wird der Bestand nicht geändert.
     *
     * @param loggedInUser User der den Artikel eingefügen will
     * @param name Name des Artikels
     * @param price Preis des Artikels
     * @param stock Bestand des Artikels
     * @param available ob der artikel auf bestand ist oder nicht
     * @throws ArticleAlreadyExistsException wenn der Artikel bereits existiert
     */
    public void addArticle(User loggedInUser, String name, double price, int stock, boolean available) throws ArticleAlreadyExistsException;

    /**
     * Methode zum Einfügen eines neuen Massenartikels in den Bestand.
     * Wenn der Massenartikel bereits im bestand ist, wird der Bestand nicht geändert
     *
     * @param loggedInUser User der den Massenartikel eingefügen will
     * @param name Name des Massenartikels
     * @param price Preis des Massenartikels
     * @param stock Bestand des Massenartikels
     * @param available ob der Massenartikels auf Bestand ist oder nicht
     * @param packageSize Größe einer Packung
     * @throws ArticleAlreadyExistsException, wenn der Massenartikel bereits existiert
     */
    public void addMassArticle(User loggedInUser, String name, double price, int stock, boolean available, int packageSize) throws ArticleAlreadyExistsException;

    /**
     * Methode zum Löschen eines Artikels aus dem Bestand.
     *
     * @param loggedInUser User der den Artikel löschen will
     * @param articleNr Nummer des Artikels
     */
    public void deleteArticle(User loggedInUser, int articleNr);

    /**
     * Methode um den Artikel zu bearbeiten
     *
     * @param loggedInUser User der den Artikel berabeitet
     * @param article Artikel der geändert werden soll
     * @param name Name des Artikels
     * @param price Preis des Artikels
     * @param stock Bestand des Artikels
     * @param available Erhälichkeit des Artikels
     */
    public void updateArticleData(User loggedInUser, Article article, String name, double price, int stock, boolean available);

    /**
     * Methode, um den Bestand eines Artikels zu erhöhen
     *
     * @param loggedInUser User der den Bestand erhöhen will
     * @param article Artikel dessen bestand erhöht wird
     * @param amount Anzahl mit der der bestand erhöht werden soll
     */
    public void addStock(User loggedInUser, Article article, int amount);

    /**
     * Methode, um den Bestand eines Artikels zu reduzieren
     *
     * @param loggedInUser User der den Bestand reduzieren will
     * @param article Artikel dessen bestand reduziert wird
     * @param amount Anzahl mit der der bestand reduziert wird
     */
    public void reduceStock(User loggedInUser, Article article, int amount);

    /**
     * Methode, die eine Liste mit allen Kunden zurückgibt.
     * @return Liste mit allen Kunden im KundenBestand des Shops.
     */
    public List<User> getCustomers();

    /**
     * Methode, die eine Liste mit allen Mitarbeiter zurückgibt.
     * @return Liste mit allen Mitarbeiter im MitarbeiterBestand des Shops.
     */
    public List<User> getStaff();

    /**
     * Methode zum Suchen von Kunden anhand der UserID. Es wird eine Liste von Kunden
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten Kunden
     * @return Liste der gefundenen Kunden (evtl. leer)
     */
    public List<User> searchCustomer (int userID);

    /**
     * Methode zum Suchen von User anhand der UserID. Es wird eine Liste von User
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten User
     * @return Liste der gefundenen User (evtl. leer)
     */
    public List<User> searchUsers(int userID);

    /**
     * Methode zum Suchen von Mitarbeiter anhand der UserID. Es wird eine Liste von Mitarbeiter
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten Mitarbeiter
     * @return Liste der gefundenen Mitarbeiter (evtl. leer)
     */
    public List<User> searchStaff (int userID);

    /**
     * Methode zum Einfügen eines neuen Kunden.
     * Wenn der Kunden bereits im Kundenbestand ist, wird der KundenBestand nicht geändert
     *
     * @param name Name des Kunden
     * @param username Username des Kunden
     * @param password Passwort des Kunden
     * @throws UserAlreadyExistsException, wenn der Kunde bereits existiert
     * @return User-Objekt, das im Erfolgsfall eingefügt wurde
     */
    public User addCustomer(String name, String username, String password) throws UserAlreadyExistsException;

    /**
     * Methode zum Einfügen eines neuen Mitarbeiter.
     * Wenn der Mitarbeiter bereits im Mitarbeiterbestand ist, wird der MitarbeiterBestand nicht geändert
     *
     * @param loggedInUser User der den Mitarbeiter hinzufügen will
     * @param name Name des Mitarbeiter
     * @param username Username des Mitarbeiter
     * @param password Passwort des Mitarbeiter
     * @throws UserAlreadyExistsException, wenn der Mitarbeiter bereits existiert
     * @return User-Objekt, das im Erfolgsfall eingefügt wurde
     */
    public User addStaff(User loggedInUser, String name, String username, String password) throws UserAlreadyExistsException;

    /**
     * Methode zum Löschen eines Users.
     *
     * @param loggedInUser User der den User löschen will
     * @param user User Objekt das gelöscht werden soll
     */
    public void deleteUser(User loggedInUser, User user);

    /**
     * Durchsucht die User Liste und gibt den gewünschten User, falls vorhanen, zurück
     * @param userNr
     * @return User, falls vorhanden
     */
    public User getUser(int userNr);

    /**
     * Methode um den User zu bearbeiten
     *
     * @param loggedInUser User der den Artikel berabeitet
     * @param user user der geändert werden soll
     * @param name Name des Users
     * @param username Username des Users
     * @param password passwort des Users
     * @param address Adresse des Users
     */
    public void updateUserData(User loggedInUser, User user, String name, String username, String password, String address);

    /**
     * Methode, zum einlogen
     * Checkt ob Username und Passwort mit einem User im Userbestand übereinstimmt
     *
     * @param username Username des Users
     * @param password Passwort des Users
     * @return User, falls Username und Passwort übereinstimmt
     * @throws LoginFailedException, wenn Username und Passwort nicht übereinstimmen
     */
    public User login(String username, String password) throws LoginFailedException;

    /**
     * Methode, zum Registrieren von neuen Kunden
     * Checkt ob Kunde schon im Kundenbestand existiert
     *
     * @param name Name des Users
     * @param username Username des Users
     * @param password Passwort des Users
     * @throws UserAlreadyExistsException
     */
    public void signup(String name, String username, String password) throws UserAlreadyExistsException;

    /**
     * Methode zum Speichern des Userbestands in einer Datei.
     *
     * @throws IOException
     */
    public void saveUser() throws IOException;

    /**
     * Methode zum Speichern des Artikelbestands in einer Datei.
     *
     * @throws IOException
     */
    public void saveArticle() throws IOException;

    /**
     * Methode zum Speichern des Massen-Artikelbestands in einer Datei.
     *
     * @throws IOException
     */
    public void saveMassArticle() throws IOException;

    /**
     * Methode, um ein Artikel dem Warenkorb eines Kunden hinzufügen
     *
     * @param user User mit Warenkorb
     * @param article Artikel der hinzugefügt wird
     * @param amount Anzahl der Artikel die hinzugefügt werden
     */
    public void addToCart(User user, Article article, int amount);

    /**
     * Methode, um ein Artikel aus dem Warenkorb eines User zu entfernen
     *
     * @param user User mit Warenkorb
     * @param article Artikel der entfernt wird
     * @param amount Anzahl der Artikel die entfernt werden
     */
    public void removeFromCart(User user, Article article, int amount);

    /**
     * Methode die den Warenkorb eines Kunden Leert
     * @param user User mit Warenkorb
     */
    public void emptyCart(User user);

    /**
     * Methode, die ein Rechnungs-Objekt mit allen Artikel die sich im Warenkorb befinden erzeugt
     * Rechnungs-Objekt wird beim Kauf erzeugt und enthält Rechnungs datum, Artikel, Kunde und Preis
     *
     * @param user User der den Kauf getätigt hat
     * @return Rechnung
     */
    public Invoice buy(User user);

}
