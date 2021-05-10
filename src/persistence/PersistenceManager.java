package persistence;
import java.io.IOException;
import valueObject.Article;

public interface PersistenceManager {
    public void openForReading(String dataSource) throws IOException;
    public void openForWriting(String dataSource) throws IOException;
    public boolean close();
}
