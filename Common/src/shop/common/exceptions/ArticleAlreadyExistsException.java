package shop.common.exceptions;
import shop.common.valueObject.Article;

public class ArticleAlreadyExistsException extends Exception {
    public ArticleAlreadyExistsException(Article article, String msg) {
        super("Article: " + article.getName() + "already exists");
    }
}
