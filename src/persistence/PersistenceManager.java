package persistence;
import java.io.IOException;
import valueObject.Article;

public interface PersistenceManager {
    public void openForReading(String dataSource) throws IOException;
    public void openForWriting(String dataSource) throws IOException;
    public boolean close();

    public Article loadArticle() throws IOException;

    public boolean saveArticle(Article article) throws IOException;

    public User loadUser() throws IOException;

    public boolean saveUser(User user) throws IOException;
}
