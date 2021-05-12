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

    public Invoice (Map<Article, Integer> shoppingCart, int userNr) {
        this.userNr = userNr;
        this.shoppingCart = shoppingCart;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        this.date = format.format(now);
        makeString();
    }

    private void makeString() {
        double price = 0;
        String str = "";
        str += "--- Invoice ---";
        str += "Kundennummer: " + userNr;
        str += "Datum: " + date;
        str += "";
        for (Article article : shoppingCart.keySet()) {
            price = article.getPrice() * shoppingCart.get(article);
            str += article.getName() + " x" + shoppingCart.get(article) + " - " + turnToEuro(price);
            total += price;
        }
        this.totalStr = turnToEuro(total);
        str += "Summe: " + totalStr;
        str += "";
        str += "---------------";
        this.string = str;
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
