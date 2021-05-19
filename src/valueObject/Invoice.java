package valueObject;
import valueObject.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Invoice {

    private int userNr;
    private float total = 0;
    private String date;
    private Map<Article, Integer> shoppingCart = new HashMap<Article, Integer>();
    private String string;
    private String totalStr;
    private String name;

    public Invoice (User user) {
        this.userNr = user.getUserNr();
        this.name = user.getName();
        this.shoppingCart = user.getShoppingCart().getCart();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        this.date = format.format(now);
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
            spaces = String.join("", Collections.nCopies(spaceAmount, " "));
            str += "| " + articleName + " x" + articleAmount + spaces + priceStr + " |\n";
            total += price;
        }

        this.totalStr = turnToEuro(total);
        str += "+---------------------------+\n";
        str += "| Summe: " + totalStr + String.join("", Collections.nCopies(18 - totalStr.length(), " ")) + " |\n";
        str += "+---------------------------+\n";
        str += "\n";
        str += "Danke für Ihren einkauf!\n";
        return str;
    }

    public void print() {
        System.out.println(string);
    }

    private String turnToEuro(double price) {
        String str = (int)price + ",";
        int afterComma = (int)(price*100-(int)price*100);
        if (afterComma == 0)      { str += "00"; }
        else if (afterComma < 10) { str += afterComma + "0"; }
        else                      { str += afterComma; }
        str += "€";
        return str;
    }

    //Getter
    public float getTotal() { return total; }

    public String getString() { return string; }

    public String getTotalString() { return totalStr; }
}
