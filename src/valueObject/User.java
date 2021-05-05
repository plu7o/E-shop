package valueObject;
import valueObject.Article;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String name;
    private int userNr;
    private String password;
    private String address;
    private boolean staff = false;
    private Map<Article, Integer> shoppingCart = new HashMap<>(); // TODO

    public User (String name, int userNr, String password) {
        this.name = name;
        this.userNr = userNr;
        this.password = password;
    }

    //Getter
    public String getName()     { return name; }

    public int getUserNr()      { return userNr; }

    public String getPassword() { return password; }

    public boolean isStaff()    { return staff; }

    public Map<Article, Integer> getShoppingCart() { return shoppingCart; }

    //Setter
    public void setName(String name)         { this.name = name; }

    public void setUserNr(int userNr)        { this.userNr = userNr; }

    public void setPassword(String password) { this.password = password; }

    public void setStaff(boolean staff)      { this.staff = staff; }

    public void setShoppingCart(Map<Article, Integer> shoppingCart) { this.shoppingCart = shoppingCart; }

    public void setAddress(String address) {this.address = address;}
}
