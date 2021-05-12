package valueObject;

public class User {

    private String name;
    private int userNr;
    private String username;
    private String password;
    private String address;
    private boolean staff;
    private boolean customer;

    private ShoppingCart shoppingCart; // TODO

    public User (String name, int userNr, String username, String password) {
        this(name, userNr, username, password, false, false);
    }

    public User (String name, int userNr, String username, String password, boolean staff, boolean customer) {
        this.name = name;
        this.userNr = userNr;
        this.username = username;
        this.password = password;
        this.staff = staff;
        this.customer = customer;
        this.address = address;
        this.shoppingCart = new ShoppingCart();
    }

    public boolean equals(Object otherUser) {
        if(otherUser instanceof User) {
            return ((this.userNr == ((User) otherUser).userNr)
                    && (this.name.equals(((User) otherUser).name)));
        } else
            return false;
    }

    //Getter
    public String getUsername() { return username; }

    public String getAddress()  { return address; }

    public String getName()     { return name; }

    public int getUserNr()      { return userNr; }

    public String getPassword() { return password; }

    public boolean isStaff()    { return staff; }

    public boolean isCustomer() { return customer; }

    public ShoppingCart getShoppingCart() { return shoppingCart; }

    //Setter
    public void setUsername(String username) { this.username = username; }

    public void setAddress(String address)   {this.address = address;}

    public void setName(String name)         { this.name = name; }

    public void setUserNr(int userNr)        { this.userNr = userNr; }

    public void setPassword(String password) { this.password = password; }

    public void setStaff(boolean staff)      { this.staff = staff; }

    public void setCustomer(boolean staff)   { this.customer = customer; }
}
