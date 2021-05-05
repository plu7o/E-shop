package domain;
import java.util.List;
import valueObject.Article;

public class Shop {
    private String file = "";

    private ArticleAdministration articleAdministration;
    private UserAdministration userAdministration;

    public Shop(String file) {
        this.file = file;

        articleAdministration = new ArticleAdministration();
        userAdministration = new UserAdministration();
    }

    public List<Article> getAllAvailableArticles() {
        return articleAdministration.getAllAvailableArticles();
    }

    public List<Article> searchArticle(String name) {
        return articleAdministration.searchArticle(name);
    }
}
