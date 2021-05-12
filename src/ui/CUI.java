package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

import domain.LogAdministration;
import domain.Shop;
import domain.exceptions.UserAlreadyExistsException;
import valueObject.*;

public class CUI {

    private Shop shop;
    private BufferedReader in;
    private User logged_in_user;

    public CUI(String file) throws IOException, UserAlreadyExistsException {
        //Shop Verwaltung wird initialisiert
        shop = new Shop(file);

        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void showMenu() {
        if (logged_in_user == null) {
            String menu = "+----------------------+\n" +
                          "| Login            [1] |\n" +
                          "| Sign Up          [2] |\n" +
                          "| Show Products    [3] |\n" +
                          "| Search           [4] |\n" +
                          "| Exit             [q] |\n" +
                          "+----------------------+\n";

            System.out.println(menu);
        }
        else if (logged_in_user.isCustomer()) {
            System.out.println("WELCOME! " + logged_in_user.getUsername());
        }
        else if (logged_in_user.isStaff()) {
            System.out.println("WELCOME! " + logged_in_user.getUsername());
        }
    }

    private String readInput() throws IOException {
        return in.readLine();
    }

    private void processUserInput(String input) throws IOException {
        String username;
        String password;
        String name;
        String articleName;

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
                    } catch (Exception e) {
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
            }
        } else if (logged_in_user.isCustomer()) {

        } else if (logged_in_user.isStaff()){

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
                processUserInput(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Exit
        } while (!input.equals("q"));
    }

    public static void main(String args[]) {
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
