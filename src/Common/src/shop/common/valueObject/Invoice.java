package shop.common.valueObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Invoice {

    private int userNr;
    private float total;
    private String date;
    private Map<Article, Integer> shoppingCart = new HashMap<Article, Integer>();
    private String string;
    private String totalStr;
    private String name;

    public Invoice(User user) { this(user.getUserNr(), user.getName(), user.getShoppingCart().getCart()); }

    public Invoice(int userNr, String name, Map<Article, Integer> shoppingCart) {
        this.userNr = userNr;
        this.name = name;
        this.shoppingCart = shoppingCart;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.date = format.format(now);

        total = 0;
        for (Article article : this.shoppingCart.keySet()) {
            total += article.getPrice() * shoppingCart.get(article);
        }
        this.totalStr = turnToEuro(total);
    }

    public String toString() {
        double price = 0;
        String str = "";
        str += "+----------INVOICE----------+\n";
        str += "| Date: " + date + "          |\n";
        str += "| UserID: " + userNr + String.join("", Collections.nCopies(18 - String.valueOf(userNr).length(), " ")) + "|\n";
        str += "| name: " + name + String.join("", Collections.nCopies(20 - name.length(), " ")) + "|\n";
        str += "+---------------------------+\n";

        String priceStr = "";
        String articleName = "";
        int articleAmount = 0;
        int spaceAmount = 0;
        String spaces = "";
        for (Article article : shoppingCart.keySet()) {
            price = article.getPrice() * shoppingCart.get(article);
            priceStr = turnToEuro(price);
            articleName = article.getName();
            articleAmount = shoppingCart.get(article);
            spaceAmount = 23 - priceStr.length() - articleName.length() - String.valueOf(articleAmount).length();
            if (spaceAmount >= 0) {
                spaces = String.join("", Collections.nCopies(spaceAmount, " "));
            } else {
                spaces = "";
            }
            str += "| " + articleName + " x" + articleAmount + spaces + priceStr + " |\n";
            total += price;
        }

        this.totalStr = turnToEuro(total);
        str += "+---------------------------+\n";
        str += "| Summe: " + this.totalStr + String.join("", Collections.nCopies(18 - totalStr.length(), " ")) + " |\n";
        str += "+---------------------------+\n";
        str += "\n";
        str += "Danke für Ihren einkauf!\n";
        return str;
    }

    /**
     * Nimmt einen Double und verändert ihn so zu einem String, das er in der deutschen Schreibweise ist
     * @param price Preis
     * @return String mit Komma und Eurozeichen
     */
    private String turnToEuro(double price) {
        String str = (int) price + ",";
        int afterComma = (int) (price * 100 - (int) price * 100);
        if (afterComma == 0) {
            str += "00";
        } else if (afterComma < 10) {
            str += afterComma + "0";
        } else {
            str += afterComma;
        }
        str += "€";
        return str;
    }

    //Getter
    public float getTotal()        { return total; }

    public String getString()      { return string; }

    public String getTotalString() { return totalStr; }

    public int getUserNr()         { return userNr; }

    public String getDate()        { return date; }

    public String getTotalStr()    { return totalStr; }

    public String getName()        { return name; }

    public Map<Article, Integer> getShoppingCart() { return shoppingCart; }
}
