package shop.common.valueObject;

public class MassArticle extends Article {
    private int packageSize;
    private String name;
    private int articleNr;
    private double price;
    private int stock;
    private boolean available;
    // private boolean massArticle;

    public MassArticle(String name, int articleNr, double price, int stock, boolean available, int packageSize) {
        super(name, articleNr, price, stock, available);
        this.packageSize = packageSize;
        this.articleNr = articleNr;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.available = available;
        // this.massArticle = massArticle;
    }
    public String toString() {
        String availability = this.available ? "in stock" : "SOLD OUT";
        return ("Nr: " + articleNr + " | Article-name: " + name + " | Price: " + price + "€" + " | Availability: " + availability + " | Package Size: " + packageSize );
    }

    //Getter
    public int getPackageSize() { return packageSize; }
    //public boolean isMassArticle() { return massArticle; }

    //Setter
    public void setPackageSize  (int packageSize) {this.packageSize = packageSize; }
    //public void setMassArticle(boolean massArticle)   { this.massArticle = massArticle; }
}
