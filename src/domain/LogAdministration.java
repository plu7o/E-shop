package domain;
import persistence.FilePersistenceManager;
import persistence.PersistenceManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class LogAdministration {
    private final PersistenceManager pm = new FilePersistenceManager();
    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");

    // 10 = Article, 20 = Staff, 30 = Customer | 1 = New, 2 = Edit, 3 = Delete
    public final int NEW_ARTICLE = 11;      //[staffNr, staffName, articleNr, ...?]
    public final int EDIT_ARTICLE = 12;     //[staffNr, staffName, articleNr, attribute, newValue...]
    public final int DELETE_ARTICLE = 13;   //[staffNr, staffName, articleNr]
    public final int ARTICLE_STOCK = 15;    //[staffNr, staffName, articleNr, amount]

    public final int NEW_STAFF = 21;        //[staffNr, staffName, staffNr, ...?]
    public final int EDIT_STAFF = 22;       //[staffNr, staffName, userNr, attribute, newValue...]
    public final int DELETE_STAFF = 23;     //[staffNr, staffName, articleNr]

    public final int NEW_CUSTOMER = 31;     //[userNr, userName, ...?]
    public final int EDIT_CUSTOMER = 32;    //[staffNr, staffName, userNr, attribute, newValue...]
    public final int DELETE_CUSTOMER = 33;  //[staffNr, staffName, userNr]

    public final int TRANSACTION = 40;      //[userNr, userName, price, articleNr, amount...]

    public void log(int type, String[] data) {
        LocalDateTime now = LocalDateTime.now();
        String text = format.format(now);
        switch (type) {
            case NEW_ARTICLE ->  text += " | add article | article " + data[1] + " created by: " + data[1] + " (" + data[0] + ")";
            case NEW_STAFF ->    text += " | add article | ID: " + data[1] + " | created by: " + data[1] + " (" + data[0] + ")";
            case NEW_CUSTOMER -> text += " | add customer | ID: " + data[0] + " Name: " + data[1];
            case EDIT_ARTICLE -> {
                text += " | mod article | article " + data[1] + " edited by " + data[1] + " (" + data[0] + ") [";
                for (int i = 2; i < data.length; i++) {
                    if (i > 2) {
                        text += ", ";
                    }
                    text += data[i];
                }
                text += "]";
            }
            case EDIT_STAFF -> {
                text += " | mod article | staff " + data[1] + " edited by " + data[1] + " (" + data[0] + ") [";
                for (int i = 2; i < data.length; i++) {
                    if (i > 2) {
                        text += ", ";
                    }
                    text += data[i];
                }
                text += "]";
            }
            case EDIT_CUSTOMER -> {
                text += " | mod customer | customer " + data[1] + " edited by " + data[1] + " (" + data[0] + ") [";
                for (int i = 2; i < data.length; i++) {
                    if (i > 2) {
                        text += ", ";
                    }
                    text += data[i];
                }
                text += "]";
            }
            case DELETE_ARTICLE ->  text += " | del article | article " + data[1] + " deleted by: " + data[1] + " (" + data[0] + ")";
            case DELETE_STAFF ->    text += " | del staff | staff " + data[1] + " deleted by: " + data[1] + " (" + data[0] + ")";
            case DELETE_CUSTOMER -> text += " | del customer | user " + data[1] + " deleted by: " + data[1] + " (" + data[0] + ")";
            case ARTICLE_STOCK ->   text += " | mod stock | stock of article " + data[1] + " changed amount: " + data[2] + " | changed by: " + data[1] + " (" + data[0] + ")";
            case TRANSACTION -> {
                text += " | purchase | user " + data[2] + " bought " + ((data.length - 2) / 2) + " articles for " + data[1] + " [";
                for (int i = 2; i < data.length; i += 2) {
                    if (i > 2) {
                        text += ", ";
                    }
                    text += data[i] + ": " + data[i + 1];
                }
                text += "]";
            }
        }
        try {
            pm.openForWriting("LOG.txt");
            pm.saveLog(text);
            pm.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
