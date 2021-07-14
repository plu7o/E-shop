package shop.server.net;

import shop.common.exceptions.*;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientRequestProcessor implements Runnable {

    private ShopInterface shop;
    private User loggedInUser;
    private Socket clientSocket;
    private BufferedReader in;
    private PrintStream out;

    public ClientRequestProcessor(Socket socket, ShopInterface Shop) {

        this.shop = Shop;
        clientSocket = socket;

        // I/O-Streams initialisieren und ClientRequestProcessor-Objekt als Thread starten:
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(inputStream));
            out = new PrintStream(outputStream, true);

        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException e2) {}
            System.out.println("Exception when providing the stream: " + e);
            return;
        }

        System.out.println("Connected to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
    }

    /**
     * Methode zur Abwicklung der Kommunikation mit dem Client gemäß dem
     * vorgebenen Kommunikationsprotokoll.
     */
    public void run() {
        // Implement shop functions with socket communication
        String input = "";

        // Begrüßung an den Client senden
        out.println("Server to Client: I'M READY FOR YOUR REQUESTS!");

        // Hauptschleife zur wiederhotlen Abwicklung der Kommunikation
        do {
            // Beginn der Benutzerinteraktion:
            // Aktion vom Client einlesen [dann ggf. weitere Daten einlesen ...]
            try {
                input = in.readLine();
            } catch (Exception e) {
                System.out.println("---> ERROR reading Client (Action)");
                System.out.println(e.getMessage());
                input = null;
            }
            // Eingabe bearbeiten:
            if (input == null) {
                // input wird von readLine() auf null gesetzt, wenn Client Verbindung abbricht
                // Einfach behandeln wie ein "quit"
                input = "q";
            } else {
                switch (input) {
                    case "getAllAvailableArticles" -> listAvailableArticles();
                    case "getAllArticles" -> listAllArticles();
                    case "searchArticle" -> searchArticle();
                    case "getArticle" -> getArticle();
                    case "addArticle" -> addArticle();
                    case "addMassArticle" -> addMassArticle();
                    case "deleteArticle" -> deleteArticle();
                    case "updateArticleData" -> updateArticleData();
                    case "updateMassArticleData" -> updateMassArticleData();
                    case "addStock" -> addStock();
                    case "reduceStock" -> reduceStock();
                    case "getCustomers" -> getCustomers();
                    case "getStaff" -> getStaff();
                    case "getAllUsers" -> getAllUsers();
                    case "searchCustomer" -> searchCustomer();
                    case "searchUsers" -> searchUsers();
                    case "searchStaff" -> searchStaff();
                    case "addCustomer" -> addCustomer();
                    case "addStaff" -> addStaff();
                    case "deleteUser" -> deleteUser();
                    case "getUser" -> getUser();
                    case "updateUserData" -> updateUserData();
                    case "login" -> login();
                    case "signup" -> signup();
                    case "saveUser" -> saveUser();
                    case "saveArticle" -> saveArticle();
                    case "saveMassArticle" -> saveMassArticle();
                    case "addToCart" -> addToCart();
                    case "removeFromCart" -> removeFromCart();
                    case "emptyCart" -> emptyCart();
                    case "buy" -> buy();
                    case "getSorted" -> getSorted(); 
                }
            }
        } while (!(input.equals("q")));
        disconnect();
    }
    
    /**
     * Methode um verbindung zum Client abbzubrechen
     */
    private void disconnect() {
        try {
            out.println("BYE!");
            clientSocket.close();

            System.out.println("Connection to " + clientSocket.getInetAddress()
                    + ":" + clientSocket.getPort() + " disconnect from Client");
        } catch (Exception e) {
            System.out.println("---> ERROR trying to disconnect: ");
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
     /**
     * Methode gibt alle erhlätlichen Article zurück und sendet sie an den Client gemäß den Server Protokol
     */
    private void listAvailableArticles() {
        List<Article> articles;
        articles = shop.getAllAvailableArticles();
        sendArticleListToClient(articles);
    }
    
     /**
     * Methode gibt alle Article zurück und sendet sie an den Client gemäß den Server Protokol
     */
    private void listAllArticles() {
        List<Article> articles;
        articles = shop.getAllArticles();
        sendArticleListToClient(articles);
    }
    
    /**
    * Methode um Artikel Liste an den CLient zu schicken gemäß den Server Protokol
    */
    private void sendArticleListToClient(List<Article> articles) {
        out.println(articles.size());
        for (Article article : articles) {
            sendArticleToClient(article);
        }
    }

    /**
    * Methode um Liste mit Usern an den CLient zu schicken gemäß den Server Protokol
    */
    private void sendUserListToClient(List<User> users) {
        out.println(users.size());
        for (User user : users) {
            sendUserToClient(user);
        }
    }
    
     /**
    * Methode um einzelnes Artikel objekt an den CLient zu schicken gemäß den Server Protokol
    */
    private void sendArticleToClient(Article article) {
        out.println(article.getArticleNr());
        out.println(article.getName());
        out.println(article.getPrice());
        out.println(article.getStock());
        out.println(article.isAvailable());
    }
    
     /**
    * Methode um einzelnes User objekt an den CLient zu schicken gemäß den Server Protokol
    */
    private void sendUserToClient(User user) {
        Map<Article, Integer> shoppingCart = user.getShoppingCart().getCart();
        out.println(user.getName());
        out.println(user.getUserNr());
        out.println(user.getUsername());
        out.println(user.getPassword());
        out.println(user.isStaff());
        out.println(user.isCustomer());
        out.println(user.getAddress());
        out.println(shoppingCart.size());
        for (Article article : shoppingCart.keySet()) {
            sendArticleToClient(article);
            out.println(shoppingCart.get(article));
        }
    }
    
     /**
    * Methode um Invoice an den CLient zu schicken
    */
    private void sendInvoiceToClient(Invoice invoice) {
        Map<Article, Integer> shoppingCart = invoice.getShoppingCart();
        out.println(invoice.getName());
        out.println(invoice.getUserNr());
        out.println(shoppingCart.size());
        for (Article article : shoppingCart.keySet()) {
            sendArticleToClient(article);
            out.println(shoppingCart.get(article));
        }
    }
    
     /**
    * Methode git ein liste mit Artikeln zurück die mit den suchbegriff übereinstimmen
    * Liest den such begriff vom Client
    * Und sende die listen an den Client
    */
    private void searchArticle() {
        String input = null;
        //Antwort vom Client wird gelesen
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action): " + input);
            System.out.println(e.getMessage());
        }
        String name = new String(input);
        List<Article> articles;
        if (name.equals("")) {
            articles = shop.getAllAvailableArticles();
        } else {
            articles = shop.searchArticle(name);
        }
        // suchergebnis wird an Client zurück geschitk
        sendArticleListToClient(articles);
    }
    
    /**
    * Methode gibt einen einzelnen Artikel der mit der Artikel nummer übereinstimmt zurück
    * und sende den Artikel an den Client zurück
    */
    private void getArticle() {
        String input = null;
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action)");
            System.out.println(e.getMessage());
        }
        int articleNr = Integer.parseInt(input);

        try {
            Article article = shop.getArticle(articleNr);
            out.println("SUCCESS");
            sendArticleToClient(article);
        } catch (ArticleNotFoundException e2) {
            out.println("FAILURE");
            System.out.println(e2.getMessage());
        }
    }
    
    /**
    * Methode um Artikel hinzuzufügen
    * Liest die Parameter vom Client ein
    * und erstellt dann den Artikel
    */
    private void addArticle() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (USERID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article Name)");
            System.out.println(e.getMessage());
        }

        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article price)");
            System.out.println(e.getMessage());
        }
        double price = Double.parseDouble(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        int stock = Integer.parseInt(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article Availabilty)");
            System.out.println(e.getMessage());
        }
        boolean available = Boolean.parseBoolean(input);

        // die eigentliche Arbeit soll der SHOP machen:
        try {
            loggedInUser = shop.getUser(userID);
            shop.addArticle(loggedInUser, name, price, stock, available);
            shop.saveArticle();
            // Rückmeldung an den Client: Aktion erfolgreich!
            out.println("SUCCESS");
        } catch (ArticleAlreadyExistsException | IOException e) {
            // Rückmeldung an den Client: Fehler!
            out.println("ERROR");
            out.println(e.getMessage());
        }
    }
    
     /**
    * Methode um Massen Artikel hinzuzufügen
    * Liest die Parameter vom Client ein
    * und erstellt dann den Massen Artikel
    */
    public void addMassArticle() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (USERID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article Name)");
            System.out.println(e.getMessage());
        }

        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article price)");
            System.out.println(e.getMessage());
        }
        double price = Double.parseDouble(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        int stock = Integer.parseInt(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article Availabilty)");
            System.out.println(e.getMessage());
        }
        boolean available = Boolean.parseBoolean(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article Package Size)");
            System.out.println(e.getMessage());
        }
        int packageSize = Integer.parseInt(input);

        // die eigentliche Arbeit soll der SHOP machen:
        try {
            loggedInUser = shop.getUser(userID);
            shop.addMassArticle(loggedInUser, name, price, stock, available, packageSize);
            shop.saveMassArticle();
            // Rückmeldung an den Client: Aktion erfolgreich!
            out.println("SUCCESS");
        } catch (ArticleAlreadyExistsException | IOException e) {
            // Rückmeldung an den Client: Fehler!
            out.println("ERROR");
            out.println(e.getMessage());
        }
    }
    
     /**
    * Methode um Artikel zu Löschen
    * Liest die Parameter vom Client ein
    * und Löscht dann den Artikel
    */
    public void deleteArticle() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (USERID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article ID)");
            System.out.println(e.getMessage());
        }
        String articleID = new String(input);
        int articleNr = Integer.parseInt(articleID);

        loggedInUser = shop.getUser(userID);
        shop.deleteArticle(loggedInUser, articleNr);
        try {
            shop.saveArticle();
            shop.saveMassArticle();
        } catch (IOException e) {
            e.getMessage();
        }
        out.println("SUCCESS");
    }
    
    /**
    * Methode zum ändern der Artikel Parameter
    * Liste neue werte vom Client und updated den Artikel mit den neuen informationen
    */
    public void updateArticleData() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article ID)");
            System.out.println(e.getMessage());
        }
        String articleID = new String(input);
        int articleNr = Integer.parseInt(articleID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article Name)");
            System.out.println(e.getMessage());
        }
        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article price)");
            System.out.println(e.getMessage());
        }
        double price = Double.parseDouble(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        int stock = Integer.parseInt(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article availability)");
            System.out.println(e.getMessage());
        }
        boolean available = Boolean.parseBoolean(input);

        try {
            loggedInUser = shop.getUser(userID);
            Article article = shop.getArticle(articleNr);
            shop.updateArticleData(loggedInUser, article, name, price, stock, available);
            shop.saveArticle();
            out.println("SUCCESS");
        } catch (ArticleNotFoundException | IOException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
      /**
    * Methode zum ändern der Massen Artikel Parameter
    * Liste neue werte vom Client und updated den Massen Artikel mit den neuen informationen
    */
    public void updateMassArticleData() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article ID)");
            System.out.println(e.getMessage());
        }
        String articleID = new String(input);
        int articleNr = Integer.parseInt(articleID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article Name)");
            System.out.println(e.getMessage());
        }
        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article price)");
            System.out.println(e.getMessage());
        }
        double price = Double.parseDouble(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        int stock = Integer.parseInt(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article availability)");
            System.out.println(e.getMessage());
        }
        boolean available = Boolean.parseBoolean(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article packageSize)");
            System.out.println(e.getMessage());
        }
        int packageSize = Integer.parseInt(input);

        try {
            loggedInUser = shop.getUser(userID);
            MassArticle article = (MassArticle)shop.getArticle(articleNr);
            shop.updateMassArticleData(loggedInUser, article, name, price, stock, available, packageSize);
            shop.saveMassArticle();
            out.println("SUCCESS");
        } catch (ArticleNotFoundException | IOException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
    /**
    * Methode um den bestand eines Artikels zu erhöhen
    *
    */
    public void addStock() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article ID)");
            System.out.println(e.getMessage());
        }
        String articleID = new String(input);
        int articleNr = Integer.parseInt(articleID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        String amount_ = new String(input);
        int amount = Integer.parseInt(amount_);

        try {
            loggedInUser = shop.getUser(userID);
            Article article = shop.getArticle(articleNr);
            shop.addStock(loggedInUser, article, amount);
            shop.saveArticle();
            shop.saveMassArticle();
            out.println("SUCCESS");
        } catch (ArticleNotFoundException | IOException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
      /**
    * Methode um den bestand eines Atikel zu verringern
    *
    */
    public void reduceStock() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article ID)");
            System.out.println(e.getMessage());
        }
        String articleID = new String(input);
        int articleNr = Integer.parseInt(articleID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        String amount_ = new String(input);
        int amount = Integer.parseInt(amount_);

        try {
            loggedInUser = shop.getUser(userID);
            Article article = shop.getArticle(articleNr);
            shop.reduceStock(loggedInUser, article, amount);
            shop.saveArticle();
            shop.saveMassArticle();
            out.println("SUCCESS");
        } catch (ArticleNotFoundException | IOException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
     /**
     * Methode gibt alle Kunden zurück und sendet sie an den Client gemäß den Server Protokol
     */
    public void getCustomers() {
        List<User> customers;
        customers = shop.getCustomers();
        sendUserListToClient(customers);
    }
    
     /**
     * Methode gibt alle Mitarbeiter zurück und sendet sie an den Client gemäß den Server Protokol
     */
    public void getStaff() {
        List<User> staff;
        staff = shop.getStaff();
        sendUserListToClient(staff);
    }
    
     /**
     * Methode gibt alle User zurück und sendet sie an den Client gemäß den Server Protokol
     */
    public void getAllUsers() {
        List<User> staff;
        staff = shop.getAllUsers();
        sendUserListToClient(staff);
    }
    
    /**
    * Methode git ein liste mit Kunden zurück die mit den suchbegriff übereinstimmen
    * Liest den such begriff vom Client
    * Und sende die listen an den Client zurück
    */
    public void searchCustomer() {
        String input = null;
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action): " + input);
            System.out.println(e.getMessage());
        }
        String userId = new String(input);
        int ID = Integer.parseInt(userId);

        List<User> customers;
        if (userId.equals("")) {
            customers = shop.getCustomers();
            sendUserListToClient(customers);
        } else {
            customers = shop.searchCustomer(ID);
            sendUserListToClient(customers);
        }
    }
    
     /**
    * Methode git ein liste mit Mitarbeitern zurück die mit den suchbegriff übereinstimmen
    * Liest den such begriff vom Client
    * Und sende die listen an den Client zurück
    */
    public void searchStaff() {
        String input = null;
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action): " + input);
            System.out.println(e.getMessage());
        }
        String userId = new String(input);
        int ID = Integer.parseInt(userId);

        List<User> staff;
        if (userId.equals("")) {
            staff = shop.getStaff();
            sendUserListToClient(staff);
        } else {
            staff = shop.searchStaff(ID);
            sendUserListToClient(staff);
        }
    }
    
     /**
    * Methode git ein liste mit Usern zurück die mit den suchbegriff übereinstimmen
    * Liest den such begriff vom Client
    * Und sende die listen an den Client zurück
    */
    public void searchUsers() {
        String input = null;
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action)");
            System.out.println(e.getMessage());
        }
        String userId = new String(input);
        int ID = Integer.parseInt(userId);

        List<User> users;
        if (userId.equals("")) {
            users = shop.getAllUsers();
            sendUserListToClient(users);
        } else {
            users = shop.searchUsers(ID);
            sendUserListToClient(users);
        }
    }
    
     /**
    * Methode um Kunden hinzuzufügen
    * Liest die Parameter vom Client ein
    * und erstellt dann den Kunden (user)
    */
    public void addCustomer() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (name)");
            System.out.println(e.getMessage());
        }
        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (username)");
            System.out.println(e.getMessage());
        }
        // Achtung: Objekte sind Referenzdatentypen:
        // Buch-Titel in neues String-Objekt kopieren,
        // damit Titel nicht bei nächste Eingabe in input überschrieben wird
        String username = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out
                    .println("---> ERROR reading Client (password)");
            System.out.println(e.getMessage());
        }
        String password = new String(input);

        try {
            User user = shop.addCustomer(name, username, password);
            // Rückmeldung an den Client: Aktion erfolgreich!
            out.println("SUCCESS");
            sendUserToClient(user);
        } catch (UserAlreadyExistsException e) {
            // Rückmeldung an den Client: Fehler!
            out.println("ERROR");
            out.println(e.getMessage());
        }
    }
    
     /**
    * Methode um Mitarbeiter hinzuzufügen
    * Liest die Parameter vom Client ein
    * und erstellt dann den Mitarbeiter (user)
    */
    public void addStaff() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action): " + input);
            System.out.println(e.getMessage());
        }
        String userId = new String(input);
        int ID = Integer.parseInt(userId);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (name)");
            System.out.println(e.getMessage());
        }
        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (username)");
            System.out.println(e.getMessage());
        }
        // Achtung: Objekte sind Referenzdatentypen:
        // Buch-Titel in neues String-Objekt kopieren,
        // damit Titel nicht bei nächste Eingabe in input überschrieben wird
        String username = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out
                    .println("---> ERROR reading Client (password)");
            System.out.println(e.getMessage());
        }
        String password = new String(input);

        try {
            loggedInUser = shop.getUser(ID);
            User user = shop.addStaff(loggedInUser, name, username, password);
            // Rückmeldung an den Client: Aktion erfolgreich!
            out.println("SUCCESS");
            sendUserToClient(user);
        } catch (UserAlreadyExistsException  e) {
            // Rückmeldung an den Client: Fehler!
            out.println("ERROR");
            out.println(e.getMessage());
        }
    }
    
    /**
    * Methode um User zu Löschen
    * Liest die Parameter vom Client ein
    * und Löscht dann den User
    */
    public void deleteUser() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action)");
            System.out.println(e.getMessage());
        }
        String loggedUser_Id = new String(input);
        int loggedUserID = Integer.parseInt(loggedUser_Id);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action)");
            System.out.println(e.getMessage());
        }
        String userId = new String(input);
        int ID = Integer.parseInt(userId);

        loggedInUser = shop.getUser(loggedUserID);
        User user = shop.getUser(ID);
        shop.deleteUser(loggedInUser, user);
        out.println("SUCCESS");
    }

     
    /**
    * Methode um User zu finden der mit der Userid übereinstimmt
    * Liest die Parameter vom Client ein
    * und gibt den passenden user zurück an den Client
    */
    public void getUser() {
        String input = null;       
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (userID)");
            System.out.println(e.getMessage());
        }
        int userId = Integer.parseInt(input);

        User user = shop.getUser(userId);
        out.println("SUCCESS");
        sendUserToClient(user);
    }

     /**
    * Methode zum ändern der User Parameter
    * Liste neue werte vom Client und updated den User mit den neuen informationen
    */
    public void updateUserData() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int loggedInUserID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (userID)");
            System.out.println(e.getMessage());
        }
        String userID = new String(input);
        int userNr = Integer.parseInt(userID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (user Name)");
            System.out.println(e.getMessage());
        }

        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (username)");
            System.out.println(e.getMessage());
        }
        String username = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (password)");
            System.out.println(e.getMessage());
        }
        String password = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (address)");
            System.out.println(e.getMessage());
        }
        String address = new String(input);

        try {
            loggedInUser = shop.getUser(loggedInUserID);
            User user = shop.getUser(userNr);
            shop.updateUserData(loggedInUser, user, name, username, password, address);
            out.println("SUCCESS");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
    /**
    * Methode zum einloggen der User
    * Liest username und password name von Client und schickt passenden User zurück an den CLient 
    */
    public void login() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (username)");
            System.out.println(e.getMessage());
        }
        String username = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (password)");
            System.out.println(e.getMessage());
        }
        String password = new String(input);

        try {
            loggedInUser = shop.login(username, password);
            out.println("SUCCESS");
            sendUserToClient(loggedInUser);
        } catch (LoginFailedException e) {
            out.println("ERROR");
            out.println(e.getMessage());
        }
    }
    
    /**
    * Methode zum Registrieren
    * liest username password und name von Client und erstellt User objekt
    */
    public void signup() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (user Name)");
            System.out.println(e.getMessage());
        }

        String name = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (username)");
            System.out.println(e.getMessage());
        }
        String username = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (password)");
            System.out.println(e.getMessage());
        }
        String password = new String(input);

        try {
            shop.signup(name, username, password);
            shop.saveUser();
            out.println("SUCCESS");
        } catch (UserAlreadyExistsException | IOException e) {
            out.println("ERROR");
            out.println(e.getMessage());
        }
    }
    
     /**
     * Methode zum Speichern des Userbestands.
     *
     */
    public void saveUser() {
        try {
            shop.saveUser();
            out.println("SUCCESS");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
     /**
     * Methode zum Speichern des Artikelbestands.
     *
     */
    public void saveArticle() {
        try {
            shop.saveArticle();
            out.println("SUCCESS");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
     /**
     * Methode zum Speichern des Massen Artikelbestands.
     *
     */
    public void saveMassArticle() {
        try {
            shop.saveMassArticle();
            out.println("SUCCESS");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
    /**
    * Methode um Artikel den Warenkorb hinzufügen
    * Liest User ID, Article ID und anzahl von Client
    * und fügt den passenden Article den passenden warenkorb hinzu
    */
    public void addToCart() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article ID)");
            System.out.println(e.getMessage());
        }
        String articleID = new String(input);
        int articleNr = Integer.parseInt(articleID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        String amount_ = new String(input);
        int amount = Integer.parseInt(amount_);

        try {
            loggedInUser = shop.getUser(userID);
            Article article = shop.getArticle(articleNr);
            shop.addToCart(loggedInUser, article, amount);
            out.println("SUCCESS");
        } catch (ArticleNotFoundException | ShoppingCartException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
     /**
    * Methode um Artikel den Warenkorb zu entnehmen
    * Liest User ID, Article ID und anzahl von Client
    * und entfernt den passenden Article vom passenden warenkorb 
    */
    public void removeFromCart() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article ID)");
            System.out.println(e.getMessage());
        }
        String articleID = new String(input);
        int articleNr = Integer.parseInt(articleID);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Article stock)");
            System.out.println(e.getMessage());
        }
        String amount_ = new String(input);
        int amount = Integer.parseInt(amount_);

        try {
            loggedInUser = shop.getUser(userID);
            Article article = shop.getArticle(articleNr);

            shop.removeFromCart(loggedInUser, article, amount);
            out.println("SUCCESS");
        } catch (ArticleNotFoundException | ShoppingCartException e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
    /**
    * Methode um den Warenkorb eines Users zu Leeren
    */
    public void emptyCart() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            loggedInUser = shop.getUser(userID);
            shop.emptyCart(loggedInUser);
            out.println("SUCCESS");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
    
    
    /**
    * Methode um Artikel im Warenkorb zu Kaufen
    * erstellt Invoice objekt und gibt das den Client zurück
    */
    public void buy() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (UserID)");
            System.out.println(e.getMessage());
        }
        String ID = new String(input);
        int userID = Integer.parseInt(ID);

        try {
            loggedInUser = shop.getUser(userID);
            Invoice invoice = shop.buy(loggedInUser);
            out.println("SUCCESS");
            sendInvoiceToClient(invoice);
            loggedInUser.getShoppingCart().emptyCart();
            shop.saveArticle();
            shop.saveMassArticle();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            out.println("ERROR");
        }
    }
       
    /**
    * Methode gibt Sortierte Liste von Artikeln zurück
    * list von Client nach was sortiert werden soll
    * schickt sortierte Liste zurück an den Client
    */
    public void getSorted() {
        String input = null;

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (USERID)");
            System.out.println(e.getMessage());
        }
        String by = new String(input);

        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (USERID)");
            System.out.println(e.getMessage());
        }
        String onlyAvailable_string = new String(input);
        boolean onlyAvailable = Boolean.parseBoolean(onlyAvailable_string);

        List<Article> sortedList = shop.getSorted(by, onlyAvailable);
        sendArticleListToClient(sortedList);
    }
}
