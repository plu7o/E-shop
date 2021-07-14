package shop.server.domain;

import shop.common.exceptions.ArticleAlreadyExistsException;
import shop.common.exceptions.ArticleNotFoundException;
import shop.common.valueObject.Article;
import shop.common.valueObject.MassArticle;
import shop.server.persistence.FilePersistenceManager;
import shop.server.persistence.PersistenceManager;

import java.io.IOException;
import java.util.*;

public class ArticleAdministration {
    private List<Article> inventory = new ArrayList<>();

    private final PersistenceManager pm = new FilePersistenceManager();

    /**
     * Erstellt einen neuen Artikel und fügt ihn, falls noch nicht vorhanden, dem Inventar hinzu
     * @param name         Name des Artikels
     * @param price        Preis des Artikels
     * @param stock        Bestand des Artikels
     * @param available    ob der artikel auf bestand ist oder nicht
     * @return die Artikelnummer des neuen Artikels
     * @throws ArticleAlreadyExistsException wenn der Artikel bereits existiert
     */
    public int add(String name, double price, int stock, boolean available) throws ArticleAlreadyExistsException {
        Article article = new Article(name, articleNrGen(), price, stock, available);
        if (nameInUse(name)) {
            throw new ArticleAlreadyExistsException(article, "");
        } else {
            inventory.add(article);
            return article.getArticleNr();
        }
    }

    /**
     * Erstellt einen neuen Massenartikel und fügt ihn, falls noch nicht vorhanden, dem Inventar hinzu
     * @param name        Name des Artikels
     * @param price       Preis des Artikels
     * @param stock       Bestand des Artikels
     * @param available   ob der artikel auf bestand ist oder nicht
     * @param packageSize Verpackungsgröße
     * @return die Artikelnummer des neuen Massenartikel
     * @throws ArticleAlreadyExistsException wenn der Massenartikel bereits existiert
     */
    public int addMassArticle(String name, double price, int stock, boolean available, int packageSize) throws ArticleAlreadyExistsException {
        MassArticle massArticle = new MassArticle(name, articleNrGen(), price, stock, available, packageSize);
        if (nameInUse(name)) {
            throw new ArticleAlreadyExistsException(massArticle, "");
        } else {
            inventory.add(massArticle);
            return massArticle.getArticleNr();
        }
    }

    /**
     * Fügt einen neuen Artikel, falls noch nicht vorhanden, dem Inventar hinzu
     * @param article der hinzuzufügende Artikel
     * @throws ArticleAlreadyExistsException wenn der Artikel bereits existiert
     */
    public void addArticle(Article article) throws ArticleAlreadyExistsException {
        if (inventory.contains(article)) {
            throw new ArticleAlreadyExistsException(article, "");
        }
        inventory.add(article);
    }

    /**
     * Fügt einen neuen Massenartikel, falls noch nicht vorhanden, dem Inventar hinzu
     * @param massArticle der hinzuzufügende Massenartikel
     * @throws ArticleAlreadyExistsException wenn der Massenartikel bereits existiert
     */
    public void addMassArticleToInventory(MassArticle massArticle) throws ArticleAlreadyExistsException {
        if (inventory.contains(massArticle)) {
            throw new ArticleAlreadyExistsException(massArticle, "");
        }
        inventory.add(massArticle);
    }

    /**
     * löscht einen Artikel aus dem Inventar
     * @param articleNr ID des zu löschenden Artikels
     */
    public void delete(int articleNr) {
        int position = getPosOfArticleViaArticleNr(articleNr);
        if (position >= 0) { inventory.remove(position); }
    }

    /**
     * ändert die Werte eines Artikels
     * @param name      Name des Artikels
     * @param price     Preis des Artikels
     * @param stock     Bestand des Artikels
     * @param available ob der artikel auf bestand ist oder nicht
     */
    public void changeArticleData(Article article, String name, double price, int stock, boolean available) {
        if (!name.equals("")) { article.setName(name); }
        if (price > 0)        { article.setPrice(price); }
        if (stock >= 0)       { article.setStock(stock); }
        article.setAvailable(available);
    }

    /**
     * ändert die Werte eines Massenartikels
     * @param name        Name des Artikels
     * @param price       Preis des Artikels
     * @param stock       Bestand des Artikels
     * @param available   ob der artikel auf bestand ist oder nicht
     * @param packageSize Verpackungsgröße
     */
    public void changeMassArticleData(MassArticle article, String name, double price, int stock, boolean available, int packageSize) {
        if (!name.equals("")) { article.setName(name); }
        if (price > 0)        { article.setPrice(price); }
        if (stock >= 0)       { article.setStock(stock); }
        if (packageSize > 0)  { article.setPackageSize(packageSize); }
        article.setAvailable(available);
    }

    /**
     * Durchsucht das Inventar und gibt die Position des gewünschten Artikels, falls vorhanden, zurück
     * andernfalls wird -1 zurückgegeben
     * @param articleNr ID des gesuchten Artikels
     * @return Position im Inventar
     */
    private int getPosOfArticleViaArticleNr(int articleNr) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getArticleNr() == articleNr) { return i; }
        }
        return -1;
    }

    /**
     * Durchsucht das Inventar und gibt die Position des gewünschten Artikels, falls vorhanden, zurück
     * andernfalls wird -1 zurückgegeben
     * @param name Name des gesuchten Artikels
     * @return Position im Inventar
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

    /**
     * Gibt das Inventar nach Name sortiert zurück
     */
    public List<Article> getInventorySortedByName() {
        List<Article> toSort = inventory;
        Collections.sort(toSort, Comparator.comparing(shop.common.valueObject.Article::getName));
        return toSort;
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

    /**
     * Gibt alle verfügbaren Artikel des Inventars nach Name sortiert zurück
     * Ruft getOnlyAvailable() und getInventorySortedByName() auf
     */
    public List<Article> getAllAvailableArticlesSortedByName() {
        return getOnlyAvailable(getInventorySortedByName());
    }

    //misc
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

    /**
     * Durchsucht das Inventar und gibt den gewünschten Artikel, falls vorhanen, zurück
     * @param name Name des Artikels
     */
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
     * Durchsucht das Inventar und gibt den gewünschten Artikel, falls vorhanen, zurück
     * @param articleNr ID des Artikels
     * @throws ArticleNotFoundException wenn der Artikel nicht gefunden wurde
     */
    public Article getArticle(int articleNr) throws ArticleNotFoundException {
        for (Article article : inventory) {
            if ((article).getArticleNr() == articleNr) {
                return article;
            }
        }
        return null;
    }

    /**
     * Lädt alle Artikel aus der Datei "data"
     * @param data Name der Datei, in der gespeichert wurde
     * @throws IOException
     */
    public void readArticleData(String data) throws IOException {
        pm.openForReading(data);
        Article article;
        do {
            article = pm.loadArticle();
            if (article != null) {
                try {
                    addArticle(article);
                } catch (ArticleAlreadyExistsException e) {}
            }
        } while (article != null);
        pm.close();
    }

    /**
     * Lädt alle Massenartikel aus der Datei "data"
     * @param data Name der Datei, in der gespeichert wurde
     * @throws IOException
     */
    public void readMassArticleData(String data) throws IOException {
        pm.openForReading(data);
        MassArticle massArticle;
        do {
            massArticle = pm.loadMassArticle();
            if (massArticle != null) {
                try {
                    addMassArticleToInventory(massArticle);
                } catch (ArticleAlreadyExistsException e) {}
            }
        } while (massArticle != null);
        pm.close();
    }

    /**
     * Gibt jeden Artikel an den FilePersistenceManager zum speichern weiter
     * @param data Name der Datei, in der gespeichert werden soll
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
     * @param data Name der Datei, in der gespeichert werden soll
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

    /**
     * Überprüft, ob es einen Artikel mit dem Namen "name" gibt
     * @param name Der zu überprüfende Name
     * @return boolean ob vergeben
     */
    private boolean nameInUse(String name) {
        for (Article article : inventory) {
            if (article.getName().equals(name)) return true;
        }
        return false;
    }
}
