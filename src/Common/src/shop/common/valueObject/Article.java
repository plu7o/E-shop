package shop.common.valueObject;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Article {
    private String name;
    private final int articleNr;
    private double price;
    private int stock;
    private boolean available;
    private String priceStr;

    private List<String> stockLog = new ArrayList<>();
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public Article(String name, int articleNr, double price, int stock, boolean available) {
        this.articleNr = articleNr;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.available = available;
        updatePriceStr();
    }

    public String toString() {
        String availability = available ? "in stock" : "SOLD OUT";

        return ("Nr: " + articleNr + " | Article-name: " + name + " | Price: " + price + "€" + " | Availability: " + availability  );
    }

    public boolean equals(Object otherArticle) {
        if (otherArticle instanceof Article) {
            return ((this.articleNr == ((Article) otherArticle).articleNr)
                    && (this.name.equals(((Article) otherArticle).name)));
        } else return false;
    }

    private void logStock(int amount) {
        LocalDateTime now = LocalDateTime.now();
        stockLog.add(format.format(now) + " | amount: " + amount + " ");
    }

    private void updatePriceStr() {
        priceStr = (int)price + ",";
        int afterComma = (int)(price * 100 - (int) price * 100);
        if (afterComma == 0)      { priceStr += "00"; }
        else if (afterComma < 10) { priceStr += afterComma + "0"; }
        else                      { priceStr += afterComma; }
        priceStr += "€";
    }

    //Modifier
    /**
     * fügt einem Gegenstand Lagerbestand hinzu
     * @param amount Anzahl zum hinzufügen
     */
    public void addStock(int amount)    { this.stock += amount; logStock(amount); }

    /**
     * entfehrnt Lagerbestand von einem Gegenstand
     * @param amount Anzahl zum entfehrnen
     */
    public void reduceStock(int amount) { this.stock -= amount; logStock(-amount); }

    //Getter
    public String getName()      { return this.name; }

    public int getArticleNr()    { return articleNr; }

    public double getPrice()     { return price; }

    public String getPriceStr()  { return priceStr; }

    public int getStock()        { return stock; }

    public boolean isAvailable() { return available; }

    public List<String> getStockLog() { return stockLog; }

    //Setter
    public void setName(String name)            { this.name = name; }

    public void setPrice(double price)          { this.price = price; updatePriceStr(); }

    public void setStock(int stock)             { logStock(stock - this.stock); this.stock = stock; }

    public void setAvailable(boolean available) { this.available = available; }

    public void setStockLog(List<String> log)   { this.stockLog = log;}
}
