package valueObject;

public class Article {
    private String name;
    private int articleNr;
    private double price;
    private int stock;
    private boolean available;

    public Article(String name, int articleNr, double price, int stock, boolean available) {
        this.articleNr = articleNr;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.available = available;
    }

    public String toString() {
        String availability = available ? "in stock" : "SOLD OUT";
        return ("Nr: " + articleNr + " | Article-name: " + name + " | Price: " + price + "€" + " | Availability: " + availability);
    }

    public boolean equals(Object otherArticle) {
        if(otherArticle instanceof Article) {
            return ((this.articleNr == ((Article) otherArticle).articleNr)
                    && (this.name.equals(((Article) otherArticle).name)));
        } else
            return false;
    }

    //Modifier
    public void addStock(int amount)    { this.stock += amount; }

    public void reduceStock(int amount) { this.stock -= amount; }

    //Getter
    public String getName()      { return this.name; }

    public int getArticleNr()    { return articleNr; }

    public double getPrice()     { return price; }

    public int getStock()        { return stock; }

    public boolean isAvailable() { return available; }

    //Setter
    public void setName(String name)            { this.name = name; }

    public void setArticleNr(int articleNr)     { this.articleNr = articleNr; }

    public void setPrice(float price)           { this.price = price; }

    public void setStock(int stock)             { this.stock = stock; }

    public void setAvailable(boolean available) { this.available = available; }
}
