package shop.client.ui.GUI.panel;

import javax.swing.*;
import shop.common.valueObject.Invoice;
import shop.common.valueObject.Article;

import java.awt.*;
import java.util.List;
import java.util.Vector;

public class InvoicePanel extends JPanel {

    private Invoice userInvoice;

    private JLabel date = new JLabel("Date:", SwingConstants.CENTER);
    private JLabel date_value = new JLabel("", SwingConstants.CENTER);

    private JLabel iD = new JLabel("ID:", SwingConstants.CENTER);
    private JLabel id_value = new JLabel("", SwingConstants.CENTER);

    private JLabel name = new JLabel("Name:", SwingConstants.CENTER);
    private JLabel name_value = new JLabel("", SwingConstants.CENTER);

    private JList<String> articles;

    private JButton acceptButton = new JButton("OK");

    public InvoicePanel(Invoice invoice){
        userInvoice = invoice;

        setup();
    }

    private void setup() {
        List<Article> shoppingCartList = new Vector<>();

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        JLabel invoice = new JLabel("INVOICE", SwingConstants.CENTER);
        invoice.setFont(new Font("Arial", Font.BOLD, 25));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        add(date, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        String date = userInvoice.getDate();
        name_value.setText(date);
        add(date_value, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        add(iD, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        int id = userInvoice.getUserNr();
        String id_string = Integer.toString(id);
        name_value.setText(id_string);
        add(date_value, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        add(name, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;
        String name = userInvoice.getName();
        name_value.setText(name);
        add(name_value, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;

        /*
        shoppingCartList = new Vector<>(userInvoice.getShoppingCart().keySet());
        articles = new JList<>(shoppingCartList);

        add()
        */
    }
}
