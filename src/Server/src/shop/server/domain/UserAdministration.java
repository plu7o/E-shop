package shop.server.domain;

import shop.common.exceptions.LoginFailedException;
import shop.common.exceptions.UserAlreadyExistsException;
import shop.common.exceptions.UserNotFoundException;
import shop.common.exceptions.ShoppingCartException;
import shop.common.valueObject.Article;
import shop.common.valueObject.Invoice;
import shop.common.valueObject.User;
import shop.server.persistence.*;

import java.io.IOException;
import java.util.*;

public class UserAdministration {
    private List<User> customers = new Vector<User>();
    private List<User> staff = new Vector<User>();
    private List<User> users = new Vector<User>();

    private PersistenceManager pm = new FilePersistenceManager();

    /**
     * Fügt einen neuen User, falls noch nicht vorhanden, hinzu
     *
     * @param user der hinzuzufügende User
     * @throws UserAlreadyExistsException wenn der User bereits existiert
     */
    public void add(User user) throws UserAlreadyExistsException {
        if (user.isCustomer()) {
            if (customers.contains(user)) {
                throw new UserAlreadyExistsException("");
            }
            customers.add(user);
        }
        if (user.isStaff()) {
            if (staff.contains(user)) {
                throw new UserAlreadyExistsException("");
            }
            staff.add(user);
        }
    }

    /**
     * Entfehrnt einen User
     *
     * @param user der zu entfernende User
     */
    public void delete(User user) {
        if (user.isCustomer()) { customers.remove(user); }
        if (user.isStaff())    { staff.remove(user); }
    }

    /**
     * Gibt eine Liste aller User zurück
     * @return Liste aller User
     */
    public List<User> getAllUsers() {
        users.addAll(customers);
        users.addAll(staff);
        return users;
    }

    /**
     * Nimmt eine ID entgegen und gibt den dazugehörigen User wieder zurück
     * @param userNr ID des gesuchten Users
     * @return der gesuchte User
     */
    public User getUser(int userNr) {
        for (User user : getAllUsers()) {
            if (user.getUserNr() == userNr) {
                return user;
            }
        }
        return null;
    }

    /**
     * Nimmt einen Namen entgegen und gibt den dazugehörigen Nutzer wieder zurück
     * @param name Name des gesuchten Nutzers
     * @return Liste mit dem gesuchten Nutzer
     */
    public List<User> searchUser(String name) {
        List<User> search = new Vector<>();
        Iterator<User> iter = getAllUsers().iterator();
        while (iter.hasNext()) {
            User user = iter.next();
            if (user.getName() == name) {
                search.add(user);
            }
        }
        Collections.sort(search, Comparator.comparingInt(shop.common.valueObject.User::getUserNr));
        return search;
    }

    /**
     * Nimmt eine ID entgegen und gibt den dazugehörigen Nutzer wieder zurück
     * @param userNr ID des gesuchten Nutzers
     * @return Liste mit dem gesuchten Nutzer
     */
    public List<User> getUserAsList(int userNr) {
        List<User> search = new Vector<User>();
        for (User user : users) {
            if (user.getUserNr() == userNr) {
                search.add(user);
            }
        }
        return search;
    }

    /**
     * Nimmt einen Namen entgegen und gibt den dazugehörigen Kunden wieder zurück
     * @param name Name des gesuchten Kunden
     * @return Liste mit dem gesuchten Kunden
     */
    public List<User> searchCustomer(String name) {
        List<User> search = new Vector<User>();
        Iterator<User> iter = customers.iterator();
        while (iter.hasNext()) {
            User user = iter.next();
            if (user.getName() == name) {
                search.add(user);
            }
        }
        Collections.sort(search, Comparator.comparingInt(shop.common.valueObject.User::getUserNr));
        return search;
    }

    /**
     * Nimmt eine ID entgegen und gibt den dazugehörigen Kunden wieder zurück
     * @param userNr ID des gesuchten Kunden
     * @return Liste mit dem gesuchten Kunden
     */
    public List<User> getCustomer(int userNr) {
        List<User> search = new Vector<User>();
        for (User user : customers) {
            if (user.getUserNr() == userNr) {
                search.add(user);
            }
        }
        return search;
    }

    /**
     * Nimmt einen Namen entgegen und gibt den dazugehörigen Angestellten wieder zurück
     * @param name Name des gesuchten Angestellten
     * @return Liste mit dem gesuchten Angestellten
     */
    public List<User> searchStaff(String name) {
        List<User> search = new Vector<>();

        Iterator<User> iter = staff.iterator();
        while (iter.hasNext()) {
            User user = iter.next();
            if (user.getName() == name) {
                search.add(user);
            }
        }

        Collections.sort(search, Comparator.comparing(shop.common.valueObject.User::getName));
        return search;
    }

    /**
     * Nimmt eine ID entgegen und gibt den dazugehörigen Angestellten wieder zurück
     * @param userNr ID des gesuchten Angestellten
     * @return Liste mit dem gesuchten Angestellten
     */
    public List<User> getStaff(int userNr) {
        List<User> search = new Vector<User>();
        for (User user : staff) {
            if (user.getUserNr() == userNr) {
                search.add(user);
            }
        }
        return search;
    }

    /**
     * Gibt eine Liste aller Kunden zurück
     * @return Liste aller Kunden
     */
    public List<User> getCustomerList() {
        Collections.sort(customers, Comparator.comparing(shop.common.valueObject.User::getName));
        return new Vector<User>(customers);
    }

    /**
     * Gibt eine Liste aller Angestellten zurück
     * @return Liste aller Angestellten
     */
    public List<User> getStaff() {
        Collections.sort(staff, Comparator.comparing(shop.common.valueObject.User::getName));
        return new Vector<User>(staff);
    }

    /**
     * Loggt einen Nutzer ein
     * @param username Nutzername des einzuloggenden Nutzers
     * @param password Passwort des einzuloggenden Nutzers
     * @return der eingeloggte Nutzer
     * @throws LoginFailedException
     */
    public User login(String username, String password) throws LoginFailedException {
        User user = findUsername(username);

        if (user.getPassword().equals(password)) {
            return user;
        }
        throw new LoginFailedException("Username or Password is incorrect");
    }

    /**
     * Registriert einen neuen Nutzer
     * @param name     Name des neuen Nutzers
     * @param username Nutzername des neuen Nutzers
     * @param password Passwort des neuen Nutzers
     * @return ID des neuen Nutzers
     * @throws UserAlreadyExistsException, wenn der Nutzer bereits existiert
     */
    public int register(String name, String username, String password) throws UserAlreadyExistsException {
        try {
            User user = findUsername(username);
            throw new UserAlreadyExistsException("");
        } catch (LoginFailedException e) {
            User user = new User(name, userIDGen(), username, password, false, true);
            customers.add(user);
            return user.getUserNr();
        }
    }

    /**
     * nimmt einen Nutzernamen entgegen und gibt den dazugehörigen Nutzer zurück
     * @param username Name des gesuchten Nutzers
     * @return der gesuchte Nutzer
     * @throws LoginFailedException wenn der Nutzer nicht gefunden wurde
     */
    private User findUsername(String username) throws LoginFailedException {
        for (User user : customers) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        for (User user : staff) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        throw new LoginFailedException("404 User Not Found");
    }

    /**
     * Geht aller Kunden duch und gibt die nächste zu verwendende UserNr zurück
     * @return die nächste UserNr
     */
    public int userIDGen() {
        int userID = 100;
        for (User user : customers) {
            if (userID <= user.getUserNr()) {
                userID = user.getUserNr() + 1;
            }
        }
        return userID;
    }

    /**
     * Geht aller Angestellten duch und gibt die nächste zu verwendende UserNr zurück
     * @return die nächste UserNr
     */
    public int staffIDGen() {
        double Id = Math.floor((Math.random() * 100));
        int staffID = (int) Id;
        for (User user : staff) {
            while (staffID == user.getUserNr()) {
                Id = Math.floor((Math.random() * 100));
                staffID = (int) Id;
            }
        }
        return staffID;
    }

    /**
     * Nimmt einen Nutzer und änder die Daten wie angegeben
     * @param user     Nutzer, der bearbeitet werden soll
     * @param name     Neuer Name des Nutzers
     * @param username Neuer Nutzername des Nutzers
     * @param password Neues Passwort des Nutzers
     * @param address  Neue Adresse des Nutzers
     */
    public void changeUserData(User user, String name, String username, String password, String address) {
        if (!name.equals(""))     { user.setName(name); }
        if (!username.equals("")) { user.setUsername(username); }
        if (!password.equals("")) { user.setPassword(password); }
        if (!address.equals(""))  { user.setAddress(address); }
    }

    /**
     * Lädt alle Nutzer aus der Datei "data"
     * @param data Name der Datei, in der gespeichert wurde
     * @throws IOException
     */
    public void readUserData(String data) throws IOException {
        pm.openForReading(data);
        User user;
        do {
            user = pm.loadUser();
            if (user != null) {
                try {
                    add(user);
                } catch (UserAlreadyExistsException e) {}
            }
        } while (user != null);
        pm.close();
    }

    /**
     * Gibt jeden Nutzer an den FilePersistenceManager zum speichern weiter
     * @param data Name der Datei, in der gespeichert werden soll
     * @throws IOException
     */
    public void saveUser(String data) throws IOException {
        pm.openForWriting(data);
        for (User user : getAllUsers()) {
            pm.saveUser(user);
        }
        pm.close();
    }

    /**
     * Fügt einen Artikel in den Warenkorb von user hinzu
     * @param user    Der Nutzer, der die Aktion ausführt
     * @param article Der hinzuzufügende Artikel
     * @param amount  Die Menge des Artikels
     * @throws ShoppingCartException
     */
    public void addToCart(User user, Article article, int amount) throws ShoppingCartException {
        int updatedStock = article.getStock() - amount;
        if (!(updatedStock <= 0)) {
            user.getShoppingCart().addToCart(article, amount);
            article.setStock(updatedStock);
        } else if (updatedStock == 0) {
            user.getShoppingCart().addToCart(article, amount);
            article.setStock(updatedStock);
            article.setAvailable(false);
        } else {
            throw new ShoppingCartException("");
        }
    }

    /**
     * Entfernt einen Artikel aus dem Warenkorb
     * @param user    Der Nutzer, der die Aktion ausführt
     * @param article Der zu entfernende Artikel
     * @param amount  Die Menge die zu entfernen ist
     * @throws ShoppingCartException
     */
    public void removeFromCart(User user, Article article, int amount) throws ShoppingCartException {
        if (user.getShoppingCart().removeFromCart(article, amount)) {
            article.addStock(amount);
            article.setAvailable(true);
        } else {
            throw new ShoppingCartException("");
        }
    }

    /**
     * leert den Warenkob komplett
     * @param user Der Nutzer, dessen Warenkorb geleert werden soll
     */
    public void emptyCart(User user) {
        for (Article article : user.getShoppingCart().getCart().keySet()) {
             article.addStock(user.getShoppingCart().getCart().get(article));
        }
        user.getShoppingCart().emptyCart();
    }

    /**
     * Nimmt einen Nutzer und gibt eine, zu seinem Warenkorb passende, Abrechnung zurück
     * @param user Der Nutzer, der etwas kaufen möchte
     * @return Abrechnung
     */
    public Invoice buy(User user) {
        Invoice invoice = new Invoice(user);
        return invoice;
    }
}
