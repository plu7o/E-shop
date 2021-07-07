package shop.common.valueObject;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private Map<Article, Integer> cart = new HashMap<>();

    /**
     * Fügt dem Warenkorb einen Artikel "amount"-Oft hinzu
     * @param article Der hinzuzufügende Artikel
     * @param amount Die Menge
     */
    public void addToCart(Article article, int amount) {
        if (!cart.containsKey(article)) {
            cart.put(article, amount);
        } else {
            int newAmount = cart.get(article) + amount;
            cart.put(article, newAmount);
        }
    }

    /**
     * Entfehrnt einen Artikel "amount"-Oft aus dem Warenkorb
     * @param article Der zu entfehrnenede Artikel
     * @param amount Die Menge
     */
    public boolean removeFromCart(Article article, int amount) {
        boolean succeeded = false;
        if (!cart.containsKey(article)) {
            //TODO throw No Article in cart
        } else {
            int newAmount = cart.get(article) - amount;
            if (!(newAmount <= 0)) {
                cart.put(article, newAmount);
                succeeded = true;
            } else if (newAmount == 0) {
                cart.remove(article);
                succeeded = true;
            }
        }
        return succeeded;
    }

    public void emptyCart() { cart.clear(); }

    public String toString() {
        String str = "";
        for (Article article : cart.keySet()) {
            str += article  + " | Amount: " + cart.get(article);
        }
        return str;
    }

    //Getter
    public Map<Article, Integer> getCart() { return cart; }

    public double getTotal() {
        double total = 0;
        for (Article article : cart.keySet()) {
            total += article.getPrice()*cart.get(article);
        }
        return total;
    }

    //Setter
    public void setCart(Map<Article, Integer> cart) {
        this.cart = cart;
    }
}
