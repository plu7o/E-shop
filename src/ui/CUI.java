package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import domain.ArticleAdministration;
import domain.Shop;
import valueObject.*;

public class CUI {

    private Shop shop;
    private BufferedReader in;
    private User logged_in_user;

    public CUI(String file) throws IOException {
        //Shop Verwaltung wird initialisiert
        shop = new Shop(file);

        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void showMenu() {
        if (logged_in_user == null) {
            System.out.println("WELCOME! \n");
            System.out.println("Commands:   \n  Login:           [1]");
            System.out.println("            \n  Sign Up:         [2]");
            System.out.println("            \n  Show Articles:   [3]");
            System.out.println("            \n  Search Articles: [4]");
            System.out.println("            \n  Exit:            [q]");
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
                    System.out.println("Username: ");
                    //username wird eingelesen
                    username = readInput();
                    System.out.println("Password: ");
                    //password wird eingelesen
                    password = readInput();
                    try {
                        //Do login
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case "2":
                    System.out.println("Name: ");
                    name = readInput();
                    System.out.println("Username: ");
                    username = readInput();
                    System.out.println("Password: ");
                    password = readInput();
                    try {
                        //do signup
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case "3":
                    list = shop.getAllAvailableArticles();
                    showArticleList(list);
                    break;
                case "4":
                    System.out.println("Article-name > ");
                    articleName = readInput();
                    // search for articles
                    //showArticleList(list);
                    break;
            }
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

    public void run() {
        String input = "";
        do {
            //Gibt menu aus
            showMenu();
            try {
                // liest den input ein
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




