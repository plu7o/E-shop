package valueObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {
    private Map<Article, Integer> cart = new HashMap<>();

    public void addToCart(Article article, int amount) {
        if (!cart.containsKey(article)) {
            cart.put(article, amount);
        } else {
            int newAmount = cart.get(article) + amount;
            cart.put(article, newAmount);
        }
    }

    public boolean removeFromCart(Article article, int amount) {
        boolean succeeded = false;
        if (!cart.containsKey(article)) {
            // throw No Article in cart
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

    public void emptyCart() {
        cart.clear();
    }

    public Map<Article, Integer> getCart() {
        return cart;
    }

    public double getTotal() {
        double total = 0;
        for (Article article : cart.keySet()) {
            total += article.getPrice()*cart.get(article);
        }
        return total;
    }

    public String toString() {
        String str = "";
        for (Article article : cart.keySet()) {
            str += article  + " | Amount: " + cart.get(article) + "\n";
        }
        return str;
    }


  }
