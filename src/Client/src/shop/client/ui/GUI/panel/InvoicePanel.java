package shop.client.ui.GUI.panel;

import javax.swing.*;

import shop.common.valueObject.Invoice;
import shop.common.valueObject.Article;
import shop.common.valueObject.User;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class InvoicePanel extends JPanel {

    private Invoice userInvoice;
    private InvoicePanelListener invoicePanelListener;

    private JLabel date = new JLabel("Date:");
    private JLabel date_value = new JLabel("");

    private JLabel iD = new JLabel("ID:");
    private JLabel id_value = new JLabel("");

    private JLabel name = new JLabel("Name:");
    private JLabel name_value = new JLabel("");

    private JLabel total = new JLabel("Total:");
    private JLabel total_value = new JLabel();

    private JList articles;
    private JList amount_values;

    private JButton acceptButton = new JButton("OK");

    public interface InvoicePanelListener {
        public void onAccept();
    }

    public InvoicePanel(Invoice invoice, InvoicePanelListener listener) {
        userInvoice = invoice;
        invoicePanelListener = listener;

        setup();
        setupEvents();
    }

    private void setup() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.ipady = 10;

        JLabel invoice = new JLabel("INVOICE", SwingConstants.CENTER);
        invoice.setFont(new Font("Arial", Font.BOLD, 25));
        date.setFont(new Font("Arial", Font.BOLD, 15));
        date_value.setFont(new Font("Arial", Font.PLAIN, 15));
        iD.setFont(new Font("Arial", Font.BOLD, 15));
        id_value.setFont(new Font("Arial", Font.PLAIN, 15));
        name.setFont(new Font("Arial", Font.BOLD, 15));
        name_value.setFont(new Font("Arial", Font.PLAIN, 15));
        total.setFont(new Font("Arial", Font.BOLD, 15));
        total_value.setFont(new Font("Arial", Font.PLAIN, 15));

        c.gridx = 0;
        c.gridy = 0;
        add(invoice, c);

        c.gridx = 0;
        c.gridy = 2;
        add(date, c);

        c.gridx = 1;
        c.gridy = 2;
        String date = userInvoice.getDate();
        date_value.setText(date);
        add(date_value, c);

        c.gridx = 0;
        c.gridy = 3;
        add(iD, c);

        c.gridx = 1;
        c.gridy = 3;
        int id = userInvoice.getUserNr();
        String id_string = Integer.toString(id);
        id_value.setText(id_string);
        add(id_value, c);

        c.gridx = 0;
        c.gridy = 4;
        add(name, c);

        c.gridx = 1;
        c.gridy = 4;
        String name = userInvoice.getName();
        name_value.setText(name);
        add(name_value, c);

        c.gridx = 0;
        c.gridy = 5;
        List<String> shoppingCartList = new Vector<>();
        for (Article article : userInvoice.getShoppingCart().keySet()) {
            shoppingCartList.add(article.getName());
        }
        articles = new JList<String>((Vector<? extends String>) shoppingCartList);
        add(articles, c);

        c.gridx = 1;
        c.gridy = 5;
        List<String> amounts = new ArrayList<>();
        for (int value : userInvoice.getShoppingCart().values()) {
            String amount = Integer.toString(value);
            amounts.add(amount);
        }
        String[] amount_list = amounts.toArray(new String[amounts.size()]);

        amount_values = new JList<String>(amount_list);
        add(amount_values, c);

        c.gridx = 0;
        c.gridy = 6;
        add(total, c);

        c.gridx = 1;
        c.gridy = 6;
        c.ipady = 40;
        total_value.setText(userInvoice.getTotalStr());
        add(total_value, c);

        c.gridx = 0;
        c.gridy = 8;
        c.ipady = 10;
        c.gridwidth = 2;
        add(acceptButton, c);

        setBorder(BorderFactory.createBevelBorder(5));
    }

    private void setupEvents() {
        acceptButton.addActionListener(new InvoiceActionListener());
    }

    public class InvoiceActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource().equals(acceptButton)) {
                invoicePanelListener.onAccept();
            }
        }
    }
}
