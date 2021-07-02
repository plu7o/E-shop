package shop.common.interfaces;

import java.io.IOException;
import java.util.List;
import shop.common.valueObject.*;
import shop.common.exceptions.*;

public interface ShopInterface {

    /**
     * Methode zum Beenden einer Verbindung zum Server.
     *
     * @throws IOException
     */
    public abstract void disconnect() throws IOException;

    /**
     * Methode, die eine Liste mit allen im Bestand erhältlichen Artikel zurückgibt.
     * @return Liste mit allen erhältlichen Artikel im Bestand des Shops.
     */
    public abstract List<Article> getAllAvailableArticles();

    /**
     * Methode, die eine Liste mit allen Artikel zurückgibt.
     * @return Liste mit allen Artikel im Bestand des Shops.
     */
    public abstract List<Article> getAllArticles();

    /**
     * Methode zum Suchen von Artikel anhand des Namens. Es wird eine Liste von Artikeln
     * zurückgegeben, die alle Artikel mit exakt übereinstimmendem Namen enthält.
     *
     * @param name Name des gesuchten Artikels
     * @return Liste der gefundenen Artikel (evtl. leer)
     */
    public abstract List<Article> searchArticle(String name);

    /**
     * Durchsucht das Inventar und gibt den gewünschten Artikel, falls vorhanen, zurück
     *
     * @param articleNr Artikelnummer des gewünschten Artikels
     * @throws ArticleNotFoundException
     * @return Article, falls vorhanden
     */
    public abstract Article getArticle(int articleNr) throws ArticleNotFoundException;

    /**
     * Methode zum Einfügen eines neuen Artikels in den Bestand.
     * Wenn der Artikel bereits im Bestand ist, wird der Bestand nicht geändert.
     *
     * @param loggedInUser User der den Artikel einfügen will
     * @param name Name des Artikels
     * @param price Preis des Artikels
     * @param stock Bestand des Artikels
     * @param available ob der Artikel auf Bestand ist oder nicht
     * @throws ArticleAlreadyExistsException wenn der Artikel bereits existiert
     */
    public abstract void addArticle(User loggedInUser, String name, double price, int stock, boolean available) throws ArticleAlreadyExistsException;

    /**
     * Methode zum Einfügen eines neuen Massenartikels in den Bestand.
     * Wenn der Massenartikel bereits im Bestand ist, wird der Bestand nicht geändert
     *
     * @param loggedInUser User der den Massenartikel einfügen will
     * @param name Name des Massenartikels
     * @param price Preis des Massenartikels
     * @param stock Bestand des Massenartikels
     * @param available ob der Massenartikels auf Bestand ist oder nicht
     * @param packageSize Größe einer Packung
     * @throws ArticleAlreadyExistsException, wenn der Massenartikel bereits existiert
     */
    public abstract void addMassArticle(User loggedInUser, String name, double price, int stock, boolean available, int packageSize) throws ArticleAlreadyExistsException;

    /**
     * Methode zum Löschen eines Artikels aus dem Bestand.
     *
     * @param loggedInUser User der den Artikel löschen will
     * @param articleNr Nummer des Artikels
     */
    public abstract void deleteArticle(User loggedInUser, int articleNr);

    /**
     * Methode um den Artikel zu bearbeiten
     *
     * @param loggedInUser User der den Artikel bearbeitet
     * @param article Artikel der geändert werden soll
     * @param name Name des Artikels
     * @param price Preis des Artikels
     * @param stock Bestand des Artikels
     * @param available Erhältlichkeit des Artikels
     */
    public abstract void updateArticleData(User loggedInUser, Article article, String name, double price, int stock, boolean available);

    /**
     * Methode, um den Bestand eines Artikels zu erhöhen
     *
     * @param loggedInUser User der den Bestand erhöhen will
     * @param article Artikel dessen bestand erhöht wird
     * @param amount Anzahl mit der der bestand erhöht werden soll
     */
    public abstract void addStock(User loggedInUser, Article article, int amount);

    /**
     * Methode, um den Bestand eines Artikels zu reduzieren
     *
     * @param loggedInUser User der den Bestand reduzieren will
     * @param article Artikel dessen bestand reduziert wird
     * @param amount Anzahl mit der der bestand reduziert wird
     */
    public abstract void reduceStock(User loggedInUser, Article article, int amount);

    /**
     * Methode, die eine Liste mit allen Kunden zurückgibt.
     * @return Liste mit allen Kunden im KundenBestand des Shops.
     */
    public abstract List<User> getCustomers();

    /**
     * Methode, die eine Liste mit allen Mitarbeiter zurückgibt.
     * @return Liste mit allen Mitarbeiter im MitarbeiterBestand des Shops.
     */
    public abstract List<User> getStaff();

    /**
     * Methode zum Suchen von Kunden anhand der UserID. Es wird eine Liste von Kunden
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten Kunden
     * @return Liste der gefundenen Kunden (evtl. leer)
     */
    public abstract List<User> searchCustomer (int userID);

    /**
     * Methode zum Suchen von User anhand der UserID. Es wird eine Liste von User
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten User
     * @return Liste der gefundenen User (evtl. leer)
     */
    public abstract List<User> searchUsers(int userID);

    /**
     * Methode zum Suchen von Mitarbeiter anhand der UserID. Es wird eine Liste von Mitarbeiter
     * zurückgegeben, die alle User mit exakt übereinstimmender UserID enthält.
     *
     * @param userID UserID des gesuchten Mitarbeiter
     * @return Liste der gefundenen Mitarbeiter (evtl. leer)
     */
    public abstract List<User> searchStaff (int userID);

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
    public abstract User addCustomer(String name, String username, String password) throws UserAlreadyExistsException;

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
    public abstract User addStaff(User loggedInUser, String name, String username, String password) throws UserAlreadyExistsException;

    /**
     * Methode zum Löschen eines Users.
     *
     * @param loggedInUser User der den User löschen will
     * @param user User Objekt das gelöscht werden soll
     */
    public abstract void deleteUser(User loggedInUser, User user);

    /**
     * Durchsucht die User Liste und gibt den gewünschten User, falls vorhanden, zurück
     * @param userNr
     * @return User, falls vorhanden
     */
    public abstract User getUser(int userNr);

    /**
     * Methode um den User zu bearbeiten
     *
     * @param loggedInUser User der den Artikel bearbeitet
     * @param user user der geändert werden soll
     * @param name Name des Users
     * @param username Username des Users
     * @param password Passwort des Users
     * @param address Adresse des Users
     */
    public abstract void updateUserData(User loggedInUser, User user, String name, String username, String password, String address);

    /**
     * Methode, zum einloggen
     * Checkt ob Username und Passwort mit einem User im Userbestand übereinstimmt
     *
     * @param username Username des Users
     * @param password Passwort des Users
     * @return User, falls Username und Passwort übereinstimmt
     * @throws LoginFailedException, wenn Username und Passwort nicht übereinstimmen
     */
    public abstract User login(String username, String password) throws LoginFailedException;

    /**
     * Methode, zum Registrieren von neuen Kunden
     * Checkt ob Kunde schon im Kundenbestand existiert
     *
     * @param name Name des Users
     * @param username Username des Users
     * @param password Passwort des Users
     * @throws UserAlreadyExistsException
     */
    public abstract void signup(String name, String username, String password) throws UserAlreadyExistsException;

    /**
     * Methode zum Speichern des Userbestands in einer Datei.
     *
     * @throws IOException
     */
    public abstract void saveUser() throws IOException;

    /**
     * Methode zum Speichern des Artikelbestands in einer Datei.
     *
     * @throws IOException
     */
    public abstract void saveArticle() throws IOException;

    /**
     * Methode zum Speichern des Massenartikelbestands in einer Datei.
     *
     * @throws IOException
     */
    public abstract void saveMassArticle() throws IOException;

    /**
     * Methode, um ein Artikel dem Warenkorb eines Kunden hinzufügen
     *
     * @param user User mit Warenkorb
     * @param article Artikel der hinzugefügt wird
     * @param amount Anzahl der Artikel die hinzugefügt werden
     */
    public abstract void addToCart(User user, Article article, int amount);

    /**
     * Methode, um ein Artikel aus dem Warenkorb eines User zu entfernen
     *
     * @param user User mit Warenkorb
     * @param article Artikel der entfernt wird
     * @param amount Anzahl der Artikel die entfernt werden
     */
    public abstract void removeFromCart(User user, Article article, int amount);

    /**
     * Methode die den Warenkorb eines Kunden Leert
     * @param user User mit Warenkorb
     */
    public abstract void emptyCart(User user);

    /**
     * Methode, die ein Rechnungs-Objekt mit allen Artikel die sich im Warenkorb befinden erzeugt
     * Rechnungs-Objekt wird beim Kauf erzeugt und enthält Rechnungsdatum, Artikel, Kunde und Preis
     *
     * @param user User der den Kauf getätigt hat
     * @return Rechnung
     */
    public abstract Invoice buy(User user);

    List<User> getAllUsers();
}
