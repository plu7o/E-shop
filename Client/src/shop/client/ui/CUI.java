package shop.client.ui;

import shop.common.exceptions.ArticleAlreadyExistsException;
import shop.common.exceptions.ArticleNotFoundException;
import shop.common.exceptions.LoginFailedException;
import shop.common.exceptions.UserAlreadyExistsException;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.Article;
import shop.common.valueObject.Invoice;
import shop.common.valueObject.User;
import shop.server.domain.Shop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CUI implements ShopInterface {

    private Shop shop;
    private BufferedReader in;
    private User logged_in_user;
    private boolean articleAdministration;
    private boolean userAdministration;

    public CUI(String file) throws IOException, UserAlreadyExistsException {
        //Shop Verwaltungm wird initialisiert
        shop = new Shop(file);

        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void showMenu() {
        if (logged_in_user == null) {
            String menu = """
                    +------------------------+
                    | Login              [1] |
                    | Sign Up            [2] |
                    | Show Products      [3] |
                    | Search             [4] |
                    +------------------------+
                    | Exit               [q] |
                    +------------------------+
                    """;

            System.out.println(menu);
        }
        else if (logged_in_user.isCustomer()) {
            String menu = """
                    +------------------------+
                    | Search             [1] |
                    | Show               [2] |
                    | Add                [3] |
                    | Remove             [4] |
                    | Cart               [5] |
                    | Empty cart         [6] |
                    | Buy                [7] |
                    +------------------------+
                    | Logout             [x] |
                    +------------------------+
                    """;

            System.out.println(menu);
        }
        else if (logged_in_user.isStaff()) {
            String menu;
            if (articleAdministration) {
                menu = """
                        +------------------------+
                        | Article Administration |
                        +------------------------+
                        | Search             [1] |
                        | Show               [2] |
                        | Add                [3] |
                        | Delete             [4] |
                        | Edit Article       [5] |
                        | Add Stock          [6] |
                        | Remove Stock       [7] |
                        | Show Article log   [8] |
                        +------------------------+
                        | Back               [x] |
                        +------------------------+
                        """;
            }
            else if (userAdministration) {
                menu = """
                        +------------------------+
                        | User Administration    |
                        +------------------------+
                        | Search             [1] |
                        | Show Customers     [2] |
                        | Show Staff         [3] |
                        | Add Customer       [4] |
                        | Add Staff          [5] |
                        | Delete             [6] |
                        | Edit User          [7] |
                        +------------------------+
                        | Back               [x] |
                        +------------------------+
                        """;
            } else {
                menu = """
                        +------------------------+
                        | Administrations        |
                        +------------------------+
                        | Articles           [1] |
                        | Users              [2] |
                        +------------------------+
                        | Logout             [x] |
                        +------------------------+
                        """;
            }
            System.out.println(menu);
        }
    }

    private String readInput() throws IOException {
        return in.readLine();
    }

    private void processUserInput(String input) throws IOException, ArticleNotFoundException {
        String username;
        String password;
        String name;
        String articleName;
        int articleNr;
        int amount;

        List<Article> list;

        if (logged_in_user == null) {
            switch (input) {
                case "1":
                    System.out.print(prefix() + "Username: ");
                    //username wird eingelesen
                    username = readInput();
                    System.out.print(prefix() + "Password: ");
                    //password wird eingelesen
                    password = readInput();
                    try {
                        logged_in_user = shop.login(username, password);
                        System.out.println(logged_in_user.getUsername() + " you Successfully logged in!");
                    } catch (LoginFailedException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "2":
                    System.out.print(prefix() + "Name: ");
                    name = readInput();
                    System.out.print(prefix() + "Username: ");
                    username = readInput();
                    System.out.print(prefix() + "Password: ");
                    password = readInput();
                    try {
                        shop.signup(name, username, password);
                    } catch (UserAlreadyExistsException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "3":
                    list = shop.getAllAvailableArticles();
                    showArticleList(list);
                    break;
                case "4":
                    System.out.print(prefix() + "Article-name > ");
                    articleName = readInput();
                    list = shop.searchArticle(articleName);
                    showArticleList(list);
                    break;
                default:
                    System.out.println("Wrong command see menu!");
            }
        } else if (logged_in_user.isCustomer()) {
            switch (input) {
                case "1":
                    System.out.print(prefix() + "Article-name > ");
                    articleName = readInput();
                    list = shop.searchArticle(articleName);
                    showArticleList(list);
                    break;
                case "2":
                    list = shop.getAllAvailableArticles();
                    showArticleList(list);
                    break;
                case "3":
                    try {
                        System.out.print(prefix() + "Article-nr > ");
                        String articleNrStr = readInput();
                        articleNr = Integer.parseInt(articleNrStr);
                        Article article = shop.getArticle(articleNr);
                        System.out.print(prefix() + "Amount > ");
                        String amountStr = readInput();
                        amount = Integer.parseInt(amountStr);
                        shop.addToCart(logged_in_user, article, amount);
                    } catch (ArticleNotFoundException e) {
                        System.out.println(e.getMessage());
                    } catch(NumberFormatException e2) {
                         System.out.println("Wrong input!");
                    }
                    break;
                case "4":
                    try {
                        System.out.print(prefix() + "Article-nr > ");
                        String articleNrStr = readInput();
                        articleNr = Integer.parseInt(articleNrStr);
                        Article article = shop.getArticle(articleNr);

                        System.out.print(prefix() + "Amount > ");
                        String amountStr = readInput();
                        amount = Integer.parseInt(amountStr);
                        shop.removeFromCart(logged_in_user, article, amount);
                    } catch (ArticleNotFoundException e) {
                        System.out.println(e.getMessage());
                    } catch(NumberFormatException e2) {
                        System.out.println("Wrong input!");
                    }
                    break;
                case "5":
                    System.out.println(logged_in_user.getShoppingCart() + " | " + Double.toString(logged_in_user.getShoppingCart().getTotal()));
                    break;
                case "6":
                    shop.emptyCart(logged_in_user);
                    break;
                case "7":
                    Invoice invoice = shop.buy(logged_in_user);
                    System.out.println(invoice);
                    shop.emptyCart(logged_in_user);
                    break;
                case "x":
                    System.out.println("successfully logged out");
                    logged_in_user = null;
                    break;
                default:
                    System.out.println("Wrong command see menu!");
            }
        } else if (logged_in_user.isStaff()) {
            switch (input) {
                case "1" -> articleAdministration = true;
                case "2" -> userAdministration = true;
                case "x" -> {
                    System.out.println("successfully logged out");
                    logged_in_user = null;
                }
                default -> System.out.println("Wrong command see menu!");
            }
        }
    }

    private void processUserAdministrationInput(String input) throws IOException {
        List<User> list;
        int userNr;
        String name;
        String username;
        String password;
        String userNrStr;
        String address;

        User user;

        switch (input) {
            case "1":
                try {
                    System.out.print(prefix() + "User-Nr > ");
                    userNrStr = readInput();
                    userNr = Integer.parseInt(userNrStr);
                    list = shop.searchUsers(userNr);
                    showUserList(list);
                } catch(NumberFormatException e2) {
                    System.out.println("Wrong input!");
                }
                break;
            case "2":
                list = shop.getCustomers();
                showUserList(list);
                break;
            case "3":
                list = shop.getStaff();
                showUserList(list);
                break;
            case "4":
                System.out.print(prefix() + "name > ");
                name = readInput();

                System.out.print(prefix() + "Username > ");
                username = readInput();

                System.out.print(prefix() + "Password > ");
                password = readInput();

                try {
                    shop.addCustomer(name, username, password);
                } catch (UserAlreadyExistsException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "5":
                System.out.print(prefix() + "name > ");
                name = readInput();

                System.out.print(prefix() + "Username > ");
                username = readInput();

                System.out.print(prefix() + "Password > ");
                password = readInput();

                try {
                    shop.addStaff(logged_in_user, name, username, password);
                } catch (UserAlreadyExistsException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "6":
                try {
                    System.out.print(prefix() + "User-Nr > ");
                    userNrStr = readInput();
                    userNr = Integer.parseInt(userNrStr);
                    user = shop.getUser(userNr);
                    shop.deleteUser(logged_in_user, user);
                } catch (NumberFormatException e2) {
                    System.out.println("Wrong input!");
                }
                break;
            case "7":
                try {
                    System.out.print(prefix() + "User-Nr > ");
                    userNrStr = readInput();
                    userNr = Integer.parseInt(userNrStr);
                    user = shop.getUser(userNr);

                    System.out.print(prefix() + "name > ");
                    name = readInput();

                    System.out.print(prefix() + "Username > ");
                    username = readInput();

                    System.out.print(prefix() + "Password > ");
                    password = readInput();

                    System.out.print(prefix() + "Address > ");
                    address = readInput();

                    shop.updateUserData(logged_in_user, user, name, username, password, address);
                    System.out.println("User Updated!");
                } catch (NumberFormatException e2) {
                    System.out.println("Wrong input!");
                }
                break;
            case "x":
                userAdministration = false;
                break;
            default:
                System.out.println("Wrong command see menu!");
                break;
        }
    }

    private void processsArticleAdministartionInput(String input) throws IOException, ArticleNotFoundException {
        List<Article> list;
        String articleName;
        String priceStr;
        String stockStr;
        String availableStr;
        String packageSizeStr;
        int articleNr;
        double price;
        int stock;
        boolean available;
        int packageSize;

        switch (input) {
            case "1":
                System.out.print(prefix() + "Article-name > ");
                articleName = readInput();
                list = shop.searchArticle(articleName);
                showArticleList(list);
                break;
            case "2":
                list = shop.getAllArticles();
                showArticleList(list);
                break;
            case "3":
                try {
                    System.out.print(prefix() + "Select Article type [article] | [mass] > ");
                    String type = readInput();
                    switch (type) {
                        case "article":
                            System.out.print(prefix() + "Article-name > ");
                            articleName = readInput();

                            System.out.print(prefix() + "Price > ");
                            priceStr = readInput();
                            price = Double.parseDouble(priceStr);

                            System.out.print(prefix() + "Stock > ");
                            stockStr = readInput();
                            stock = Integer.parseInt(stockStr);

                            System.out.print(prefix() + "Available > ");
                            availableStr = readInput();
                            available = Boolean.parseBoolean(availableStr);

                            try {
                                shop.addArticle(logged_in_user, articleName, price, stock, available);
                            } catch (ArticleAlreadyExistsException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case "mass":
                            System.out.print(prefix() + "Article-name > ");
                            articleName = readInput();

                            System.out.print(prefix() + "Price > ");
                            priceStr = readInput();
                            price = Double.parseDouble(priceStr);

                            System.out.print(prefix() + "Stock > ");
                            stockStr = readInput();
                            stock = Integer.parseInt(stockStr);

                            System.out.print(prefix() + "Available > ");
                            availableStr = readInput();
                            available = Boolean.parseBoolean(availableStr);

                            System.out.print(prefix() + "Package-Size > ");
                            packageSizeStr = readInput();
                            packageSize = Integer.parseInt(packageSizeStr);

                            try {
                                shop.addMassArticle(logged_in_user, articleName, price, stock, available, packageSize);
                            } catch (ArticleAlreadyExistsException e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                    }
                } catch (NumberFormatException e2) {
                    System.out.println("Wrong input!");
                }
                break;
            case "4":
                try {
                    System.out.print(prefix() + "Article-Nr > ");
                    String articleNrStr = readInput();
                    articleNr = Integer.parseInt(articleNrStr);
                    shop.deleteArticle(logged_in_user, articleNr);
                    System.out.println("Article Deleted!");
                } catch (NumberFormatException e2) {
                    System.out.println("Wrong input!");
                }
                break;
            case "5":
                try {
                    System.out.print(prefix() + "Article-Nr > ");
                    String articleNrStr = readInput();
                    if (!articleNrStr.equals("")) {
                        articleNr = Integer.parseInt(articleNrStr);
                    } else {
                        articleNr = 0;
                    }
                    Article article = shop.getArticle(articleNr);

                    System.out.print(prefix() + "Article-name > ");
                    articleName = readInput();

                    System.out.print(prefix() + "Price > ");
                    priceStr = readInput();
                    price = Double.parseDouble(priceStr);

                    System.out.print(prefix() + "Stock > ");
                    stockStr = readInput();
                    stock = Integer.parseInt(stockStr);

                    System.out.print(prefix() + "Available > ");
                    availableStr = readInput();
                    available = Boolean.parseBoolean(availableStr);

                    shop.updateArticleData(logged_in_user, article, articleName, price, stock, available);
                    System.out.println("Article Updated!");
                } catch (NumberFormatException e2) {
                    System.out.println("Wrong input!");
                }
                break;
            case "6":
                try {
                    System.out.print(prefix() + "Article-Nr > ");
                    String articleNrStr = readInput();
                    articleNr = Integer.parseInt(articleNrStr);
                    Article article = shop.getArticle(articleNr);
                    System.out.println(prefix() + "Amount >  ");
                    String amountStr = readInput();
                    int amount = Integer.parseInt(amountStr);
                    shop.addStock(logged_in_user, article, amount);
                    System.out.println("Stock added!");
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input!");
                }
            case "7":
                try {
                    System.out.print(prefix() + "Article-Nr > ");
                    String articleNrStr = readInput();
                    articleNr = Integer.parseInt(articleNrStr);
                    Article article = shop.getArticle(articleNr);
                    System.out.println(prefix() + "Amount >  ");
                    String amountStr = readInput();
                    int amount = Integer.parseInt(amountStr);
                    shop.reduceStock(logged_in_user, article, amount);
                    System.out.println("Stock reduced!");
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input!");
                }
               case "8":
                try {
                    System.out.print(prefix() + "Article-Nr > ");
                    String articleNrStr = readInput();
                    articleNr = Integer.parseInt(articleNrStr);
                    Article article = shop.getArticle(articleNr);
                    printArticleLog(article.getStockLog());
                } catch (NumberFormatException e) {
                    System.out.println("Wrong input!");
                }
                break;
            case "x":
                articleAdministration = false;
                break;
            default:
                System.out.println("Wrong command see menu!");
                break;
        }
    }

    private void showArticleList(List<Article> articleList) {
        if(articleList.isEmpty()) {
            System.out.println("List is empty :(");
        } else {
            for (Article article : articleList) {
                System.out.println(article);

            }
        }
    }

    private void showUserList(List<User> users){
        if(users.isEmpty()) {
            System.out.println("List is empty :(");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    public void printArticleLog(List<String> log) {
        if (log.isEmpty()) {
            System.out.println("List is empty :(");
            for (String s : log) {
                System.out.println(s);
            }
        }
    }

    private String prefix() {
        String prefix = "";
        if (!(logged_in_user == null)) {
            if (logged_in_user.isCustomer()) {
                prefix = "eshop@" + logged_in_user.getUsername() + "[customer]$ ";
            }
            if (logged_in_user.isStaff()) {
                prefix = "eshop@" + logged_in_user.getUsername() + "[admin]$ ";
            }
        } else {
            prefix = "eshop@guest[~]$ ";
        }
        return prefix;
    }

    public void run() {
        String input = "";
        String banner = "███████╗    ███████╗██╗  ██╗ ██████╗ ██████╗ \n" +
                        "██╔════╝    ██╔════╝██║  ██║██╔═══██╗██╔══██╗\n" +
                        "█████╗█████╗███████╗███████║██║   ██║██████╔╝\n" +
                        "██╔══╝╚════╝╚════██║██╔══██║██║   ██║██╔═══╝ \n" +
                        "███████╗    ███████║██║  ██║╚██████╔╝██║     \n" +
                        "╚══════╝    ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═╝       ";
        System.out.println(banner);
        System.out.println("WELCOME!");
        do {
            //Gibt menu aus
            showMenu();
            try {
                // liest den input ein
                System.out.print(prefix());
                input = readInput();
                // verarbeitet den input
                if (userAdministration) {
                    processUserAdministrationInput(input);
                } else if (articleAdministration) {
                    processsArticleAdministartionInput(input);
                } else {
                    processUserInput(input);
                }
            } catch (IOException | ArticleNotFoundException e) {
                e.printStackTrace();
            }
            //Exit
        } while (!input.equals("q"));
        try {
            shop.saveUser();
            shop.saveArticle();
            shop.saveMassArticle();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Bye see you next time! :) ");
    }

    public static void main(String[] args) {
        CUI cui;
        try {
            //cui wird erzeugt
            cui = new CUI("DATA");
            //Loop
            cui.run();
        } catch (IOException | UserAlreadyExistsException e) {
            e.printStackTrace();
        }
    }
}
