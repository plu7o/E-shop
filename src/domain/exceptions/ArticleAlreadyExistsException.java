package domain.exceptions;
import valueObject.Article;

public class ArticleAlreadyExistsException extends Exception {
    public ArticleAlreadyExistsException(Article article, String msg) {
        super("Article: " + article.getName() + "already exists");
    }
}
