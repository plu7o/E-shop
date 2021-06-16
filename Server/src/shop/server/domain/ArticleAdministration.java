package shop.server.domain;

import shop.common.exceptions.ArticleAlreadyExistsException;
import shop.common.exceptions.ArticleNotFoundException;
import shop.common.valueObject.Article;
import shop.common.valueObject.MassArticle;
import shop.server.persistence.FilePersistenceManager;
import shop.server.persistence.PersistenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class ArticleAdministration {
    private List<Article> inventory = new ArrayList<>();

    private final PersistenceManager pm = new FilePersistenceManager();

    public int add(String name, double price, int stock, boolean available) throws ArticleAlreadyExistsException {
        Article article = new Article(name, articleNrGen(), price, stock, available);
        if (inventory.contains(article)) {
            throw new ArticleAlreadyExistsException(article, "");
        }
        inventory.add(article);
        return article.getArticleNr();
    }

    public int addMassArticle(String name, double price, int stock, boolean available, int packageSize) throws ArticleAlreadyExistsException {
        MassArticle massArticle = new MassArticle(name, articleNrGen(), price, stock, available, packageSize);
        if (inventory.contains(massArticle)) {
            throw new ArticleAlreadyExistsException(massArticle, "");
        }
        inventory.add(massArticle);
        return massArticle.getArticleNr();
    }

    public void addArticle(Article article) throws ArticleAlreadyExistsException {
        if (inventory.contains(article)) {
            throw new ArticleAlreadyExistsException(article, "");
        }
        inventory.add(article);
    }

    public void addMassArticleToInventory(MassArticle massArticle) throws ArticleAlreadyExistsException {
        if (inventory.contains(massArticle)) {
            throw new ArticleAlreadyExistsException(massArticle, "");
        }
        inventory.add(massArticle);
    }

    public void delete(int articleNr) {
        int position = getPosOfArticleViaArticleNr(articleNr);
        if (position >= 0) { inventory.remove(position); }
    }

    public void changeArticleData(Article article, String name, double price, int stock, boolean available) {
        if (!name.equals("")) { article.setName(name); }
        if (price > 0)        { article.setPrice(price); }
        if (stock >= 0)       { article.setStock(stock); }
        article.setAvailable(available);
    }

    /**
     * Durchsucht das Inventar und gibt die Position des gewünschten Artikels, fals vorhanen, zurück
     * andernfall wird -1 zurückgegeben
     * @param articleNr
     * @return
     */
    private int getPosOfArticleViaArticleNr(int articleNr) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getArticleNr() == articleNr) { return i; }
        }
        return -1;
    }

    /**
     * Durchsucht das Inventar und gibt die Position des gewünschten Artikels, fals vorhanen, zurück
     * andernfall wird -1 zurückgegeben
     * @return
     */
    private int getPosOfArticleViaName(String name) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getName().equals(name)) { return i; }
        }
        return -1;
    }

    //Inventory - get all
    /**
     * Gibt das Inventar unsortiert zurück
     */
    public List<Article> getAllArticles() { return new ArrayList<>(inventory); }

    //Inventory - get all sorted
    /**
     * Gibt das Inventar nach Artikelnummer sortiert zurück
     */
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

    /**
     * Gibt das Inventar nach Preis sortiert zurück
     */
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

    /**
     * Gibt das Inventar nach Lagerbestand sortiert zurück
     */
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
    /**
     * Gibt alle verfügbaren Artikel des Inputs zurück
     */
    private List<Article> getOnlyAvailable(List<Article> list) {
        List<Article> onlyAvailable = new ArrayList<>();
        for (Article article : list) {
            if (article.isAvailable()) {
                onlyAvailable.add(article);
            }
        }
        return onlyAvailable;
    }

    /**
     * Gibt alle verfügbaren Artikel des Inventars zurück
     * Ruft getOnlyAvailable() auf
     */
    public List<Article> getAllAvailableArticles() { return getOnlyAvailable(inventory); }

    //Inventory - get available sorted
    /**
     * Gibt alle verfügbaren Artikel des Inventars nach Artikelnummer sortiert zurück
     * Ruft getOnlyAvailable() und getInventorySortedByArticleNr() auf
     */
    public List<Article> getAllAvailableArticlesSortedByArticleNr() {
        return getOnlyAvailable(getInventorySortedByArticleNr());
    }

    /**
     * Gibt alle verfügbaren Artikel des Inventars nach Preis sortiert zurück
     * Ruft getOnlyAvailable() und getInventorySortedByStock() auf
     */
    public List<Article> getAllAvailableArticlesSortedByPrice() {
        return getOnlyAvailable(getInventorySortedByStock());
    }

    /**
     * Gibt alle verfügbaren Artikel des Inventars nach Lagerbestand sortiert zurück
     * Ruft getOnlyAvailable() und getInventorySortedByStock() auf
     */
    public List<Article> getAllAvailableArticlesSortedByStock() {
        return getOnlyAvailable(getInventorySortedByStock());
    }

    //misc
    private String turnToEuro(float price) {
        return (int)price + "," + (int)(price*100-(int)price*100) + "€" ;
    }

    /**
     * Geht alle Artikel durch und gibt die nächste zu verwendende Artikelnummer zurück
     */
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
        search.sort(Comparator.comparingInt(Article::getArticleNr));
        return search;
    }

    /**
     * Durchsucht das Inventar und gibt den gewünschten Artikel, fals vorhanen, zurück
     * @param articleNr
     * @throws ArticleNotFoundException
     */
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

    public void readMassArticleData(String data) throws IOException {
        pm.openForReading(data);
        MassArticle massArticle;
        do {
            massArticle = pm.loadMassArticle();
            if (massArticle != null) {
                try {
                    addMassArticleToInventory(massArticle);
                } catch (ArticleAlreadyExistsException e) {

                }
            }
        } while (massArticle != null);
        pm.close();
    }

    /**
     * Gibt jeden Artikel an den FilePersistenceManager zum speichern weiter
     * @param data
     * @throws IOException
     */
    public void saveArticle(String data) throws IOException {
        pm.openForWriting(data);
        for (Article article : inventory) {
            if (!(article instanceof MassArticle)) {
                pm.saveArticle(article);
            }
        }
        pm.close();
    }

    /**
     * Gibt jeden Massenartikel an den FilePersistenceManager zum speichern weiter
     * @param data
     * @throws IOException
     */
    public void saveMassArticle(String data) throws IOException {
        pm.openForWriting(data);
        for (Article massArticle : inventory) {
            if (massArticle instanceof MassArticle) {
                pm.saveMassArticle((MassArticle) massArticle);
            }
        }
        pm.close();
    }
}
