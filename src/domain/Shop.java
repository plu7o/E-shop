package domain;
import java.util.List;
import valueObject.Article;
import valueObject.User;

public class Shop {
    private String file = "";

    private ArticleAdministration articleAdministration;
    private UserAdministration userAdministration;

    public Shop(String file) {
        this.file = file;

        articleAdministration = new ArticleAdministration();
        articleAdministration.add("article1", 1, 7.80,0, false);
        articleAdministration.add("article2", 2, 5.00,15, true);
        articleAdministration.add("article3", 3, 4.50,20, true);

        userAdministration = new UserAdministration();
        userAdministration.register("Test", "tester", "test");
    }

    public List<Article> getAllAvailableArticles() {
        return articleAdministration.getAllAvailableArticles();
    }

    public List<Article> searchArticle(String name) {
        return articleAdministration.searchArticle(name);
    }



    public List<User> getCustomers() { return userAdministration.getCustomerList(); }

    public List<User> getStaff() { return userAdministration.getStaff(); }

    public List<User> searchCustomer (int userID) { return userAdministration.searchCustomer(userID); }

    public List<User> searchStaff (int userID) { return userAdministration.searchStaff(userID); }

    public void updateUserData(User user, String name, String username, String password, String adress) {
        userAdministration.changeUserData(user, name, username, password, adress);
    }






}
