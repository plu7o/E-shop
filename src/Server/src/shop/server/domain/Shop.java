package shop.server.domain;

import java.io.IOException;
import java.util.*;

import shop.common.exceptions.*;
import shop.common.valueObject.*;
import shop.common.interfaces.ShopInterface;

public class Shop implements ShopInterface {
    private String file;

    private final ArticleAdministration articleAdministration;
    private final UserAdministration userAdministration;
    private final LogAdministration logAdmin;

    public Shop(String file) throws IOException {
        this.file = file;

        articleAdministration = new ArticleAdministration();
        articleAdministration.readArticleData(file + "_A.txt");
        articleAdministration.readMassArticleData(file + "_MA.txt");

        userAdministration = new UserAdministration();
        userAdministration.readUserData(file + "_U.txt");

        logAdmin = new LogAdministration();
    }

    @Override
    public void disconnect() throws IOException {}

    public List<Article> getAllAvailableArticles()  { return articleAdministration.getAllAvailableArticles(); }

    public List<Article> getAllArticles()           { return articleAdministration.getAllArticles(); }

    public List<Article> searchArticle(String name) { return articleAdministration.searchArticle(name); }

    public Article getArticle(int articleNr) throws ArticleNotFoundException {
        return articleAdministration.getArticle(articleNr);
    }

    public void addArticle(User loggedInUser, String name, double price, int stock, boolean available) throws ArticleAlreadyExistsException {
        int newArticleNr = articleAdministration.add(name, price, stock, available);
        String[] toLog = {String.valueOf(loggedInUser.getUserNr()), loggedInUser.getUsername(), String.valueOf(newArticleNr), String.valueOf(stock), String.valueOf(price)};
        logAdmin.log(logAdmin.NEW_ARTICLE, toLog);
    }

    public void addMassArticle(User loggedInUser, String name, double price, int stock, boolean available, int packageSize) throws ArticleAlreadyExistsException {
        int newArticleNr = articleAdministration.addMassArticle(name, price, stock, available, packageSize);
        String[] toLog = {String.valueOf(loggedInUser.getUserNr()), loggedInUser.getUsername(), String.valueOf(newArticleNr), String.valueOf(stock), String.valueOf(price), String.valueOf(packageSize)};
        logAdmin.log(logAdmin.NEW_MASS_A, toLog);
    }

    public void deleteArticle(User loggedInUser, int articleNr) {
        articleAdministration.delete(articleNr);
        String[] toLog = {String.valueOf(loggedInUser.getUserNr()), loggedInUser.getUsername(), String.valueOf(articleNr)};
        logAdmin.log(logAdmin.DELETE_ARTICLE, toLog);
    }

    public void updateArticleData(User loggedInUser, Article article, String name, double price, int stock, boolean available) {
        List<String> toLog = new ArrayList<>();
        toLog.add(String.valueOf(loggedInUser.getUserNr()));
        toLog.add(loggedInUser.getUsername());
        toLog.add(String.valueOf(article.getArticleNr()));
        if (article.getName().equals(name) && !name.equals("")) {
            toLog.add("name: " + name);
        }
        if (article.getPrice() != price && price > 0) {
            toLog.add("price: " + price);
        }
        if (article.getStock() != stock && stock >= 0) {
            toLog.add("stock: " + stock);
        }
        if (article.isAvailable() != available) {
            toLog.add("available: " + available);
        }
        String[] toLogArr = new String[toLog.size()];
        toLog.toArray(toLogArr);
        logAdmin.log(logAdmin.EDIT_ARTICLE, toLogArr);
        articleAdministration.changeArticleData(article, name, price, stock, available);
    }

    public void updateMassArticleData(User loggedInUser, MassArticle article, String name, double price, int stock, boolean available, int packageSize) {
        List<String> toLog = new ArrayList<>();
        toLog.add(String.valueOf(loggedInUser.getUserNr()));
        toLog.add(loggedInUser.getUsername());
        toLog.add(String.valueOf(article.getArticleNr()));
        if (article.getName().equals(name) && !name.equals("")) {
            toLog.add("name: " + name);
        }
        if (article.getPrice() != price && price > 0) {
            toLog.add("price: " + price);
        }
        if (article.getStock() != stock && stock >= 0) {
            toLog.add("stock: " + stock);
        }
        if (article.isAvailable() != available) {
            toLog.add("available: " + available);
        }
        if (article.getPackageSize() != packageSize && packageSize >= 0) {
            toLog.add("packageSize: " + packageSize);
        }
        String[] toLogArr = new String[toLog.size()];
        toLog.toArray(toLogArr);
        logAdmin.log(logAdmin.EDIT_MASS_A, toLogArr);
        articleAdministration.changeArticleData(article, name, price, stock, available);
    }

    public void addStock(User loggedInUser, Article article, int amount) {
        String[] toLog = {String.valueOf(loggedInUser.getUserNr()), loggedInUser.getUsername(), String.valueOf(article.getArticleNr()), String.valueOf(amount)};
        logAdmin.log(logAdmin.ARTICLE_STOCK, toLog);
        article.addStock(amount);
    }

    public void reduceStock(User loggedInUser, Article article, int amount) {
        String[] toLog = {String.valueOf(loggedInUser.getUserNr()), loggedInUser.getUsername(), String.valueOf(article.getArticleNr()), "-" + amount};
        logAdmin.log(logAdmin.ARTICLE_STOCK, toLog);
        article.reduceStock(amount);
    }

    public List<User> getCustomers()              { return userAdministration.getCustomerList(); }
    public List<User> getStaff()                  { return userAdministration.getStaff(); }
    public List<User> getAllUsers()               { return userAdministration.getAllUsers(); }
    public List<User> searchCustomer(int userID)  { return userAdministration.getCustomer(userID); }
    public List<User> searchUsers(int userID)     { return userAdministration.getUserAsList(userID); }
    public List<User> searchStaff(int userID)     { return userAdministration.getStaff(userID); }
    public List<User> searchCustomer(String name) { return userAdministration.searchCustomer(name); }
    public List<User> searchUsers(String name)    { return userAdministration.searchUser(name); }
    public List<User> searchStaff(String name)    { return userAdministration.searchStaff(name); }

    public User addCustomer(String name, String username, String password) throws UserAlreadyExistsException {
        User user = new User(name, userAdministration.userIDGen(), username, password, false, true);
        userAdministration.add(user);
        String[] toLog = {"", "", String.valueOf(user.getUserNr()), user.getUsername()};
        logAdmin.log(logAdmin.NEW_CUSTOMER, toLog);
        return user;
    }

    public User addStaff(User loggedInUser, String name, String username, String password) throws UserAlreadyExistsException {
        User user = new User(name, userAdministration.staffIDGen(), username, password, true, false);
        userAdministration.add(user);
        String[] toLog = {String.valueOf(loggedInUser.getUserNr()), loggedInUser.getUsername(), String.valueOf(user.getUserNr()), user.getUsername()};
        logAdmin.log(logAdmin.NEW_STAFF, toLog);
        return user;
    }

    public void deleteUser(User loggedInUser, User user) {
        String[] toLog = {String.valueOf(loggedInUser.getUserNr()), loggedInUser.getUsername(), String.valueOf(user.getUserNr()), user.getUsername()};
        if (user.isCustomer()) {
            logAdmin.log(logAdmin.DELETE_CUSTOMER, toLog);
        } else {
            logAdmin.log(logAdmin.DELETE_STAFF, toLog);
        }
        userAdministration.delete(user);
    }

    public User getUser(int userNr) {
        return userAdministration.getUser(userNr);
    }

    public void updateUserData(User loggedInUser, User user, String name, String username, String password, String address) {
        List<String> toLog = new ArrayList<>();
        toLog.add(String.valueOf(loggedInUser.getUserNr()));
        toLog.add(loggedInUser.getUsername());
        toLog.add(String.valueOf(user.getUserNr()));
        if (!user.getName().equals(name) && !name.equals("")) {
            toLog.add("name: " + name);
        }
        if (!user.getUsername().equals(username) && !username.equals("")) {
            toLog.add("username: " + username);
        }
        if (!user.getPassword().equals(password) && !password.equals("")) {
            toLog.add("password: ***");
        }
        if (!user.getAddress().equals(address) && !address.equals("")) {
            toLog.add("address: " + address);
        }
        String[] toLogArr = new String[toLog.size()];
        toLog.toArray(toLogArr);
        if (user.isCustomer()) {
            logAdmin.log(logAdmin.EDIT_CUSTOMER, toLogArr);
        } else {
            logAdmin.log(logAdmin.EDIT_STAFF, toLogArr);
        }

        userAdministration.changeUserData(user, name, username, password, address);
    }

    public User login(String username, String password) throws LoginFailedException {
        return userAdministration.login(username, password);
    }

    public void signup(String name, String username, String password) throws UserAlreadyExistsException {
        userAdministration.register(name, username, password);
    }

    public void saveUser() throws IOException {
        userAdministration.saveUser(file + "_U.txt");
    }

    public void saveArticle() throws IOException {
        articleAdministration.saveArticle(file + "_A.txt");
    }

    public void saveMassArticle() throws IOException {
        articleAdministration.saveMassArticle(file + "_MA.txt");
    }

    public void addToCart(User user, Article article, int amount) throws ShoppingCartException{
        userAdministration.addToCart(user, article, amount);
    }

    public void removeFromCart(User user, Article article, int amount) throws ShoppingCartException{
        userAdministration.removeFromCart(user, article, amount);
    }

    public void emptyCart(User user) { userAdministration.emptyCart(user); }

    /**
     * Gibt einem das nach "by" sortierte Inventar, oder, bei Bedarf, nur die verfügbaren Artikel
     * @param by der Parameter, nach dem sortiert werden soll
     * @param onlyAvailable ob nur Verfügbare Artikel ausgegeben werden sollen
     * @return sortierte Liste mit den Artikeln
     */
    public List<Article> getSorted(String by, boolean onlyAvailable) {
        switch (by) {
            case "ID" -> {
                if (!onlyAvailable) { return articleAdministration.getInventorySortedByArticleNr(); }
                else { return articleAdministration.getAllAvailableArticlesSortedByArticleNr(); }
            }
            case "price" -> {
                if (!onlyAvailable) { return articleAdministration.getInventorySortedByPrice(); }
                else { return articleAdministration.getAllAvailableArticlesSortedByPrice(); }
            }
            case "name" -> {
                if (!onlyAvailable) { return articleAdministration.getInventorySortedByName(); }
                else { return articleAdministration.getAllAvailableArticlesSortedByName(); }
            }
            case "stock" -> {
                if (!onlyAvailable) { return articleAdministration.getInventorySortedByStock(); }
                else { return articleAdministration.getAllAvailableArticlesSortedByStock(); }
            }
        }
        return null;
    }

    public Invoice buy(User user) {
        Invoice invoice = userAdministration.buy(user);
        int amountOfItems = 0;
        List<String> toLog = new ArrayList<>();
        toLog.add(String.valueOf(user.getUserNr()));
        toLog.add(user.getUsername());
        toLog.add(invoice.getTotalString());
        Map<Article, Integer> shoppingCart = user.getShoppingCart().getCart();
        for (Article article : shoppingCart.keySet()) {
            toLog.add(article.getArticleNr() + ": " + shoppingCart.get(article));
            amountOfItems += shoppingCart.get(article);
        }
        toLog.add(String.valueOf(amountOfItems));
        String[] toLogArr = new String[toLog.size()];
        toLog.toArray(toLogArr);
        logAdmin.log(logAdmin.TRANSACTION, toLogArr);

        return invoice;
    }
}
