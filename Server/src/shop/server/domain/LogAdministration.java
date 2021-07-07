package shop.server.domain;
import shop.server.persistence.FilePersistenceManager;
import shop.server.persistence.PersistenceManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class LogAdministration {
    private final PersistenceManager pm = new FilePersistenceManager();
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    // 10 = Article, 20 = MassArticle, 30 = Staff, 40 = Customer | 1 = New, 2 = Edit, 3 = Delete
    public final int NEW_ARTICLE     = 11;  //[staffNr, staffName,  articleNr, stock, price]
    public final int EDIT_ARTICLE    = 12;  //[staffNr, staffName,  articleNr, attribute:newValue...]
    public final int DELETE_ARTICLE  = 13;  //[staffNr, staffName,  articleNr]
    public final int ARTICLE_STOCK   = 15;  //[staffNr, staffName,  articleNr, amount]

    public final int NEW_MASS_A      = 21;  //[staffNr, staffName,  articleNr, stock, price, mass]
    public final int EDIT_MASS_A     = 22;  //[staffNr, staffName,  articleNr, attribute:newValue...]

    public final int NEW_STAFF       = 31;  //[staffNr, staffName,  userNr, userName]
    public final int EDIT_STAFF      = 32;  //[staffNr, staffName,  userNr, attribute:newValue...]
    public final int DELETE_STAFF    = 33;  //[staffNr, staffName,  articleNr]

    public final int NEW_CUSTOMER    = 41;  //[staffNr, staffName,  userNr, userName]
    public final int EDIT_CUSTOMER   = 42;  //[staffNr, staffName,  userNr, attribute:newValue...]
    public final int DELETE_CUSTOMER = 43;  //[staffNr, staffName,  userNr]

    public final int TRANSACTION     = 50;  //[userNr,  userName,   price, articleNr:amount...]

    /**
     * Methode, um jede speicherwürdige Aktion in die LOG-Datei einzutragen
     *
     * @param type Art der ausgeführten Aktion
     * @param data Deteils der ausgeführten Aktion
     */
    public void log(int type, String[] data) {
        LocalDateTime now = LocalDateTime.now();
        String text = format.format(now);
        switch (type) {
            case NEW_ARTICLE ->  text += " | add article  | article " + data[2] + " price: " + data[4] + " stock: " + data[3] + " created by: " + data[1] + " (" + data[0] + ")";
            case NEW_MASS_A ->   text += " | add mass a   | article " + data[2] + " price: " + data[4] + " stock: " + data[3] + " mass: " + data[5] + " created by: " + data[1] + " (" + data[0] + ")";
            case NEW_STAFF ->    text += " | add staff    | ID: " + data[2] + " Name: " + data[3] + " | created by: " + data[1] + " (" + data[0] + ")";
            case NEW_CUSTOMER -> text += " | add customer | ID: " + data[2] + " Name: " + data[3];
            case EDIT_ARTICLE -> {
                text += " | mod article  | article " +  data[2] + " edited by " + data[1] + " (" + data[0] + ") [";
                text = editArticle(text, data);
            }
            case EDIT_MASS_A -> {
                text += " | mod mass a   | article " +  data[2] + " edited by " + data[1] + " (" + data[0] + ") [";
                text = editArticle(text, data);
            }
            case EDIT_STAFF -> {
                text += " | mod article  | staff " +    data[2] + " edited by " + data[1] + " (" + data[0] + ") [";
                text = editUser(text, data);
            }
            case EDIT_CUSTOMER -> {
                text += " | mod customer | customer " + data[2] + " edited by " + data[1] + " (" + data[0] + ") [";
                text = editUser(text, data);
            }
            case DELETE_ARTICLE ->  text += " | del article  | article " + data[2] + " deleted by: " + data[1] + " (" + data[0] + ")";
            case DELETE_STAFF ->    text += " | del staff    | staff " +   data[2] + " deleted by: " + data[1] + " (" + data[0] + ")";
            case DELETE_CUSTOMER -> text += " | del customer | user " +    data[2] + " deleted by: " + data[1] + " (" + data[0] + ")";
            case ARTICLE_STOCK ->   text += " | mod stock    | stock of article " + data[2] + " changed amount: " + data[3] + " | changed by: " + data[1] + " (" + data[0] + ")";
            case TRANSACTION -> {
                text += " | purchase     | user " + data[1] + " (" + data[0] + ") bought " + (((data.length - 3) / 2) + 1) + " articles for " + data[2] + " [";
                for (int i = 3; i < data.length; i++) {
                    if (i > 3) {
                        text += ", ";
                    }
                    text += data[i];
                }
                text += "]";
            }
        }
        try {
            pm.openForAppending("LOG.txt");
            pm.saveLog(text);
            pm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String editArticle (String text, String[] data) {
        for (int i = 3; i < data.length; i++) {
            if (i > 3) {
                text += ", ";
            }
            text += data[i];
        }
        text += "]";
        return text;
    }

    private String editUser (String text, String[] data) {
        for (int i = 3; i < data.length; i++) {
            if (i > 3) {
                text += ", ";
            }
            text += data[i];
        }
        text += "]";
        return text;
    }
}
