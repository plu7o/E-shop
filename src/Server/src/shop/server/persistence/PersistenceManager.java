package shop.server.persistence;
import java.io.IOException;
import shop.common.valueObject.*;

public interface PersistenceManager {
    void openForReading(String dataSource) throws IOException;
    void openForWriting(String dataSource) throws IOException;
    void openForAppending(String dataSource) throws IOException;
    boolean close();

    Article loadArticle() throws IOException;

    MassArticle loadMassArticle() throws IOException;

    User loadUser() throws IOException;

    boolean saveArticle(Article article) throws IOException;

    boolean saveMassArticle(MassArticle massArticle) throws IOException;

    boolean saveUser(User user) throws IOException;

    boolean saveLog(String log) throws IOException;
}
