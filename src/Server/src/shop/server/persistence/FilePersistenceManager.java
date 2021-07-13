package shop.server.persistence;
import shop.common.valueObject.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilePersistenceManager implements PersistenceManager {
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    public void openForReading(String file) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
    }

    public void openForWriting(String file) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
    }

    public void openForAppending(String file) throws IOException {
        writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    }

    public boolean close() {
        if (writer != null)
            writer.close();
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String readText() throws IOException{
        if (reader != null) {
            return reader.readLine();
        } else {
            return "";
        }
    }

    private void writeText(String data) {
        if (writer != null) {
            writer.println(data);
        }
    }

    public Article loadArticle() throws IOException {
        String name = readText();
        if (name == null) {
            return null;
        }
        String articleNrStr = readText();
        int articleNr = Integer.parseInt(articleNrStr);
        String priceStr = readText();
        double price = Double.parseDouble(priceStr);
        String stockStr = readText();
        int stock = Integer.parseInt(stockStr);
        String availableStr = readText();
        boolean available = Boolean.parseBoolean(availableStr);
        String stockLogStr = readText();
        Article article = new Article(name, articleNr, price, stock, available);
        if (stockLogStr != null) {
            List<String> stockLog = new ArrayList<>(Arrays.asList(stockLogStr.split("#")));
            article.setStockLog(stockLog);
        }
        return article;
    }

    public MassArticle loadMassArticle() throws IOException {
        String name = readText();
        if (name == null) {
            return null;
        }
        String articleNrStr = readText();
        int articleNr = Integer.parseInt(articleNrStr);
        String priceStr = readText();
        double price = Double.parseDouble(priceStr);
        String stockStr = readText();
        int stock = Integer.parseInt(stockStr);
        String availableStr = readText();
        boolean available = Boolean.parseBoolean(availableStr);
        String packageSizeStr = readText();
        int packageSize = Integer.parseInt(packageSizeStr);
        String stockLogStr = readText();
        MassArticle massArticle = new MassArticle(name, articleNr, price,  stock, available, packageSize);
        if (stockLogStr != null) {
            List<String> stockLog = new ArrayList<>(Arrays.asList(stockLogStr.split("#")));
            massArticle.setStockLog(stockLog);
        }
        return massArticle;
    }

    public boolean saveArticle(Article article) throws IOException {
        writeText(article.getName());
        writeText(article.getArticleNr() + "");
        writeText(article.getPrice() + "");
        writeText(article.getStock() + "");
        writeText(article.isAvailable() + "");
        writeText(String.join("#", article.getStockLog()) + "");
        return true;
    }

    public boolean saveMassArticle(MassArticle massArticle) throws IOException {
        writeText(massArticle.getName());
        writeText(massArticle.getArticleNr() + "");
        writeText(massArticle.getPrice() + "");
        writeText(massArticle.getStock() + "");
        writeText(massArticle.isAvailable() + "");
        writeText(massArticle.getPackageSize() + "");
        writeText(String.join("#", massArticle.getStockLog()) + "");
        return true;
    }

    public User loadUser() throws IOException {
        String name = readText();
        if (name == null) {
            return null;
        }
        String userNrStr = readText();
        int userNr = Integer.parseInt(userNrStr);
        String username = readText();
        String password = readText();
        String staffStr = readText();
        boolean staff = Boolean.parseBoolean(staffStr);
        String customerStr = readText();
        boolean customer = Boolean.parseBoolean(customerStr);
        String address = readText();

        return new User(name, userNr, username, password, staff, customer, address);
    }

    public boolean saveUser(User user) throws IOException {
        writeText(user.getName() + "");
        writeText(user.getUserNr() + "");
        writeText(user.getUsername() + "");
        writeText(user.getPassword() + "");
        writeText(user.isStaff() + "");
        writeText(user.isCustomer() + "");
        writeText(user.getAddress() + "");
        return true;
    }

    public boolean saveLog(String log) throws IOException {
        writeText(log + "");
        return true;
    }
}
