package domain;
//import org.jetbrains.annotations.NotNull;
import valueObject.Article;
import valueObject.Invoice;
import java.util.Comparator.*;
import java.util.Collections;

import java.util.*;

public class ArticleAdministration {
    Article article = new Article("name", 0, 0, 1, true );
    private List<Article> inventory = new ArrayList<>();

    public void add(String name, int articleNr, double price, int stock, boolean available) {
        inventory.add(new Article(name, articleNr, price, stock, available));
    }

    public void add(Article article) {
        inventory.add(article);
    }

    public void delete(int articleNr) {
        int position = getPosOfArticleViaArticleNr(articleNr);
        if (position >= 0) { inventory.remove(position); }
    }

    public boolean buy(Map<Article, Integer> shoppingCart, int userNr) {
        /** Summe berechnen **/
        for (Article article : shoppingCart.keySet()) {
            if (article.getStock() < shoppingCart.get(article)) {
                System.out.println("Du willst mehr, als wir haben.");
                return false;
            }
        }
        Invoice invoice = new Invoice(shoppingCart, userNr);
        invoice.print();
        // TODO bezahlen (preis = invoice.getTotal()
        /** Artikelanzahl im Inventar reduzieren **/
        for (Article article : shoppingCart.keySet()) {
            inventory.get(getPosOfArticleViaArticleNr(article.getArticleNr())).reduceStock(shoppingCart.get(article));
        }
        return true;
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

    //Getter - get all
    public List<Article> getAllArticles() { return new ArrayList<Article>(inventory); }

    //Getter - get all - sorted
    public List<Article> getInventorySortedByArticleNr() {
        List<Article> sorted = inventory;
        int j = 0;
        Article tmp;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getArticleNr() < sorted.get(i-1).getArticleNr()) {
                j = i-1;
                while(j >= 0 && sorted.get(i).getArticleNr() < sorted.get(j).getArticleNr()) {
                    j--;
                }
                tmp = inventory.get(i);
                inventory.remove(i);
                inventory.add(j+1, tmp);
            }
        }
        return sorted;
    }

    public List<Article> getInventorySortedByPrice() {
        List<Article> sorted = inventory;
        int j = 0;
        Article tmp;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getPrice() < sorted.get(i-1).getPrice()) {
                j = i-1;
                while(j >= 0 && sorted.get(i).getPrice() < sorted.get(j).getPrice()) {
                    j--;
                }
                tmp = inventory.get(i);
                inventory.remove(i);
                inventory.add(j+1, tmp);
            }
        }
        return sorted;
    }

    public List<Article> getInventorySortedByStock() {
        List<Article> sorted = inventory;
        int j = 0;
        Article tmp;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).getStock() < sorted.get(i-1).getStock()) {
                j = i-1;
                while(j >= 0 && sorted.get(i).getStock() < sorted.get(j).getStock()) {
                    j--;
                }
                tmp = inventory.get(i);
                inventory.remove(i);
                inventory.add(j+1, tmp);
            }
        }
        return sorted;
    }

    //Getter - get available
    public List<Article> getAllAvailableArticles() {
        List<Article> onlyAvailable = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isAvailable()) {
                onlyAvailable.add(inventory.get(i));
            }
        }
        return onlyAvailable;
    }

    //Getter - get available - sorted
    public List<Article> getAllAvailableArticlesSortedByArticleNr() {
        List<Article> onlyAvailable = new ArrayList<>();
        List<Article> sorted = getInventorySortedByArticleNr();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).isAvailable()) {
                onlyAvailable.add(sorted.get(i));
            }
        }
        return onlyAvailable;
    }

    public List<Article> getAllAvailableArticlesSortedByPrice() {
        List<Article> onlyAvailable = new ArrayList<>();
        List<Article> sorted = getInventorySortedByPrice();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).isAvailable()) {
                onlyAvailable.add(sorted.get(i));
            }
        }
        return onlyAvailable;
    }

    public List<Article> getAllAvailableArticlesSortedByStock() {
        List<Article> onlyAvailable = new ArrayList<>();
        List<Article> sorted = getInventorySortedByStock();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).isAvailable()) {
                onlyAvailable.add(sorted.get(i));
            }
        }
        return onlyAvailable;
    }

    //misc
    private String turnToEuro(float price) {
        return (int)price + "," + (int)(price*100-(int)price*100) + "€" ;
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
}
