package domain.exceptions;

public class ArticleNotFoundException extends Exception {
    public ArticleNotFoundException(String msg) {
        super(msg);
    }
}
