package domain;
//import org.jetbrains.annotations.NotNull;
import valueObject.Article;
import java.util.Comparator.*;
import java.util.Collections;

import java.util.*;

public class ArticleAdministration {
    Article article = new Article("name", 0, 0, 1, true );

    private List<Article> inventory = new ArrayList<>();

    public void add(String name, int articleNr, double price, int stock, boolean available) {
        inventory.add(new Article(name, articleNr, price, stock, available));
    }

    public void delete(int articleNr) {
        int position = getPosOfArticleViaArticleNr(articleNr);
        if (position >= 0) { inventory.remove(position); }
    }

    public void buy(Map<Article, Integer> shoppingCart) {
        /** Summe berechnen **/
        float total = 0;
        for (Article article : shoppingCart.keySet()) {
            if (article.getStock() < shoppingCart.get(article)) {
                System.out.println("Du willst mehr, als wir haben.");
                return;
            } else {
                total += article.getPrice() * shoppingCart.get(article);
            }
        }
        System.out.println("Das kostet dich " + turnToEuro(total));
        // TODO bezahlen
        /** Artikelanzahl im Inventar reduzieren **/
        for (Article article : shoppingCart.keySet()) {
            inventory.get(getPosOfArticleViaArticleNr(article.getArticleNr())).reduceStock(shoppingCart.get(article));
        }
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

    // für Ausgabe
    public List<Article> getAllAvailableArticles() {
        List<Article> toPrint = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isAvailable()) {
                toPrint.add(inventory.get(i));
            }
        }
        return toPrint;
    }

    public List<Article> getAllArticles() {
        return new ArrayList<Article>(inventory);
    }

    private List<Article> getInventorySortedByArticleNr() {
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
    private List<Article> getInventorySortedByPrice() {
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
    private List<Article> getInventorySortedByStock() {
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

    //
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

