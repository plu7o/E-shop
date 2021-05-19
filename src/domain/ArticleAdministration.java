package domain;
import domain.exceptions.ArticleAlreadyExistsException;
import domain.exceptions.ArticleNotFoundException;
import domain.exceptions.LoginFailedException;
import valueObject.Article;
import valueObject.Invoice;
import valueObject.ShoppingCart;
import valueObject.User;
import persistence.*;
import java.io.IOException;

import java.util.Collections;
import java.util.*;

public class ArticleAdministration {
    private List<Article> inventory = new ArrayList<>();

    private PersistenceManager pm = new FilePersistenceManager();

    public int add(String name, double price, int stock, boolean available) throws ArticleAlreadyExistsException {
        Article article = new Article(name, articleNrGen(), price, stock, available);
        if (inventory.contains(article)) {
            throw new ArticleAlreadyExistsException(article, "");
        }
        inventory.add(article);
        return article.getArticleNr();
    }

    public void addArticle(Article article) throws ArticleAlreadyExistsException {
        if (inventory.contains(article)) {
            throw new ArticleAlreadyExistsException(article, "");
        }
        Article newArticle = article;
        inventory.add(newArticle);
    }
    
    public void delete(int articleNr) {
        int position = getPosOfArticleViaArticleNr(articleNr);
        if (position >= 0) { inventory.remove(position); }
    }

    public void changeArticleData(Article article, String name, double price, int stock, boolean available) {
        if (!name.equals("")) { article.setName(name); }
        if (price > 0)        { article.setPrice(price); }
        if (stock >= 0)      { article.setStock(stock); }
        article.setAvailable(available);
    }

    private int getPosOfArticleViaArticleNr(int articleNr) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getArticleNr() == articleNr) { return i; }
        }
        return -1;
    }

    private int getPosOfArticleViaName(String name) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getName().equals(name)) { return i; }
        }
        return -1;
    }

    public void save() {} // TODO Aufgabe 2

    //Inventory - get all
    public List<Article> getAllArticles() { return new ArrayList<Article>(inventory); }

    //Inventory - get all sorted
    public List<Article> getInventorySortedByArticleNr() {
        List<Article> sorted = inventory;
        int j;
        Article tmp;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getArticleNr() < sorted.get(i-1).getArticleNr()) {
                j = i-1;
                while(j >= 0 && sorted.get(i).getArticleNr() < sorted.get(j).getArticleNr()) { j--; }
                tmp = inventory.get(i);
                inventory.remove(i);
                inventory.add(j+1, tmp);
            }
        }
        return sorted;
    }

    public List<Article> getInventorySortedByPrice() {
        List<Article> sorted = inventory;
        int j;
        Article tmp;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getPrice() < sorted.get(i-1).getPrice()) {
                j = i-1;
                while(j >= 0 && sorted.get(i).getPrice() < sorted.get(j).getPrice()) { j--; }
                tmp = inventory.get(i);
                inventory.remove(i);
                inventory.add(j+1, tmp);
            }
        }
        return sorted;
    }

    public List<Article> getInventorySortedByStock() {
        List<Article> sorted = inventory;
        int j;
        Article tmp;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getStock() < sorted.get(i-1).getStock()) {
                j = i-1;
                while(j >= 0 && sorted.get(i).getStock() < sorted.get(j).getStock()) { j--; }
                tmp = inventory.get(i);
                inventory.remove(i);
                inventory.add(j+1, tmp);
            }
        }
        return sorted;
    }

    //Inventory - get available
    private List<Article> getOnlyAvailable(List<Article> list) {
        List<Article> onlyAvailable = new ArrayList<>();
        for (Article article : list) {
            if (article.isAvailable()) {
                onlyAvailable.add(article);
            }
        }
        return onlyAvailable;
    }

    public List<Article> getAllAvailableArticles() { return getOnlyAvailable(inventory); }

    //Inventory - get available sorted
    public List<Article> getAllAvailableArticlesSortedByArticleNr() {
        return getOnlyAvailable(getInventorySortedByArticleNr());
    }

    public List<Article> getAllAvailableArticlesSortedByPrice() {
        return getOnlyAvailable(getInventorySortedByPrice());
    }

    public List<Article> getAllAvailableArticlesSortedByStock() {
        return getOnlyAvailable(getInventorySortedByStock());
    }

    //misc
    private String turnToEuro(float price) {
        return (int)price + "," + (int)(price*100-(int)price*100) + "€" ;
    }

    public int articleNrGen() {
        int articleNr = 100;
        for (Article article : inventory) {
            if (articleNr <= article.getArticleNr()) {
                articleNr = article.getArticleNr() + 1;
            }
        }
        return articleNr;
    }

    public List<Article> searchArticle(String name) {
        List<Article> search = new Vector<>();
        for (Article article : inventory) {
            if ((article).getName().equals(name))
                search.add(article);
        }
        Collections.sort(search, Comparator.comparingInt(Article::getArticleNr));
        return search;
    }

    public Article getArticle(int articleNr) throws ArticleNotFoundException {
        for (Article article : inventory) {
            if ((article).getArticleNr() == articleNr) {
                return article;
            }
        }
        return null;
    }

    public void readArticleData(String data) throws IOException {
        pm.openForReading(data);
        Article article;
        do {
            article = pm.loadArticle();
            if (article != null) {
                try {
                    addArticle(article);
                } catch (ArticleAlreadyExistsException e) {

                }
            }
        } while (article != null);
        pm.close();
    }

    public void saveArticle(String data) throws IOException {
        pm.openForWriting(data);
        for (Article article : inventory) {
            pm.saveArticle(article);
        }
        pm.close();
    }
}
