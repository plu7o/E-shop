package domain;
import java.io.IOException;
import java.util.List;

import domain.exceptions.LoginFailedException;
import domain.exceptions.UserAlreadyExistsException;
import valueObject.Article;
import valueObject.User;

public class Shop {
    private String file = "";

    private ArticleAdministration articleAdministration;
    private UserAdministration userAdministration;

    public Shop(String file) throws IOException, UserAlreadyExistsException {
        this.file = file;

        articleAdministration = new ArticleAdministration();
        articleAdministration.add("article1", 7.80,0, false);
        articleAdministration.add("article2", 5.00,15, true);
        articleAdministration.add("article3", 4.50,20, true);

        userAdministration = new UserAdministration();
        addCustomer("test", "tester", "test");
        addStaff("admin", "admin", "root");
    }

    public List<Article> getAllAvailableArticles() {
        return articleAdministration.getAllAvailableArticles();
    }

    public List<Article> searchArticle(String name) {
        return articleAdministration.searchArticle(name);
    }

    public void addArticle(String name, double price, int stock, boolean available) { articleAdministration.add(name, price, stock, available); }

    public void deleteArticle(int articleNr) {
        articleAdministration.delete(articleNr);
    }


    public List<User> getCustomers() { return userAdministration.getCustomerList(); }

    public List<User> getStaff() { return userAdministration.getStaff(); }

    public List<User> searchCustomer (int userID) { return userAdministration.searchCustomer(userID); }

    public List<User> searchStaff (int userID) { return userAdministration.searchStaff(userID); }

    public User addCustomer(String name, String username, String password) throws UserAlreadyExistsException {
        User user = new User(name, userAdministration.userIDGen(), username, password, false, true);
        userAdministration.add(user);
        return user;
    }

    public User addStaff(String name, String username, String password) throws UserAlreadyExistsException {
        User user = new User(name, userAdministration.staffIDGen(), username, password, true, false);
        userAdministration.add(user);
        return user;
    }

    public void updateUserData(User user, String name, String username, String password, String adress) {
        userAdministration.changeUserData(user, name, username, password, adress);
    }

    public User login(String username, String password ) throws LoginFailedException {
        return userAdministration.login(username, password);
    }

    public void signup(String name, String username, String password) throws UserAlreadyExistsException {
        userAdministration.register(name, username, password);
    }
}
