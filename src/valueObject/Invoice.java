package valueObject;
import valueObject.Article;

import java.util.HashMap;
import java.util.Map;

public class Invoice {

    private int userNr;
    float total = 0;
    //private String date; //TODO date als string oder anderes Format?
    private Map<Article, Integer> shoppingCart = new HashMap<>();

    public Invoice (Map<Article, Integer> shoppingCart, int userNr) {
        this.userNr = userNr;
        this.shoppingCart = shoppingCart;
        //this.date //TODO jetziges datum
    }

    public void print() {
        double price = 0;
        System.out.println("--- Invoice ---");
        System.out.println("Kundennummer: " + userNr);
        //System.out.println("Datum: " + date);
        System.out.println("");
        for (Article article : shoppingCart.keySet()) {
            price = article.getPrice() * shoppingCart.get(article);
            System.out.println(article.getName() + " x" + shoppingCart.get(article) + " - " + price + "€");
            total += price;
        }
        System.out.println("Summe: " + total + "€");
        System.out.println("");
        System.out.println("---------------");
    }

    //Getter
    public float getTotal() { return total; }
}
