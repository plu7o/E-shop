package shop.server.net;

import shop.common.exceptions.ArticleNotFoundException;
import shop.common.interfaces.ShopInterface;

import java.io.*;
import java.util.List;
import java.net.Socket;
import shop.common.valueObject.*;

public class ClientRequestProcessor implements Runnable {

    private ShopInterface shop;

    private Socket clientSocket;
    private BufferedReader in;
    private PrintStream out;

    public  ClientRequestProcessor(Socket socket, ShopInterface Shop) {

        this.shop = Shop;
        clientSocket = socket;

        // I/O-Streams initialisieren und ClientRequestProcessor-Objekt als Thread starten:
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(inputStream));
            out = new PrintStream(outputStream);

        } catch (IOException e) {
            try {
                clientSocket.close();
            } catch (IOException e2) {

            }
            System.out.println("Exception when providing the stream: " + e);
            return;
        }

        System.out.println("Connected to " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
    }

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
                System.out.println("---> ERROR reading Client (Action): " + input);
                System.out.println(e.getMessage());
                continue;
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
                }
            }
        } while (!(input.equals("q")));

        disconnect();
    }

    private void listAvailableArticles() {
        List<Article> articles;
        articles = shop.getAllAvailableArticles();
        sendArticleListToClient(articles);
    }

    private void listAllArticles() {
        List<Article> articles;
        articles = shop.getAllArticles();
        sendArticleListToClient(articles);
    }

    private void sendArticleListToClient(List<Article> articles) {
        out.println(articles.size());
        for (Article article : articles) {
            sendArticleToClient(article);
        }
    }

    private void sendArticleToClient(Article article) {
        out.println(article.getName());
        out.println(article.getArticleNr());
        out.println(article.getPrice());
        out.println(article.getStock());
        out.println(article.isAvailable());
    }

    private void searchArticle() {
        String input = null;
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action): " + input);
            System.out.println(e.getMessage());
        }
        String name = new String(input);
        List<Article> articles;
        if (name.equals(""))
            articles = shop.getAllAvailableArticles();
        else {
            articles = shop.searchArticle(name);
            sendArticleListToClient(articles);
        }
    }

    private void getArticle() {
        String input = null;
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("---> ERROR reading Client (Action): " + input);
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
}
