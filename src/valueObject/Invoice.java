package valueObject;
import valueObject.Article;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        str += "|----------INVOICE----------|\n";
        str += "| Date: " + date + "          |\n";
        str += "| UserID: " + userNr + "               |\n";
        str += "| name: " + name + "                |\n";
        str += "|---------------------------|\n";

        for (Article article : shoppingCart.keySet()) {
            price = article.getPrice() * shoppingCart.get(article);
            str += "| " + article.getName() + " x" + shoppingCart.get(article) + "        " + turnToEuro(price) + " |\n";
            total += price;
        }

        this.totalStr = turnToEuro(total);
        str += "|---------------------------|\n";
        str += "| Summe: " + totalStr + "             |\n";
        str += "|---------------------------|\n";
        str += "\n";
        str += "Danke für Ihren einkauf!\n";
        return str;
    }

    public void print() {
        System.out.println(string);
    }

    private String turnToEuro(double price) {
        return (int)price + "," + (int)(price*100-(int)price*100) + "€" ;
    }

    //Getter
    public float getTotal() { return total; }

    public String getString() { return string; }

    public String getTotalString() { return totalStr; }
}
