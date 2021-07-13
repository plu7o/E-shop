package shop.client.ui.GUI.panel;

import shop.common.exceptions.ArticleNotFoundException;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.Article;
import shop.common.valueObject.Invoice;
import shop.common.valueObject.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class CustomerMenuPanel extends JPanel {

    public User loggedInUser;

    private ShopInterface shop;
    private CustomerMenuListener customerMenuListener;
    private ArticleTablePanel articleTablePanel;

    private JButton addButton;
    private JButton removeButton;
    private JButton cartButton;
    private JButton emptyButton;
    private JButton buyButton;

    public interface CustomerMenuListener {
        public void onAddedToCart();
        public void onRemovedFromCart();
        public void onShowCart(Map<Article, Integer> shoppingCart);
        public void onBuy(Invoice invoice);
    }

    public CustomerMenuPanel(ShopInterface shopInterface, CustomerMenuListener listener, ArticleTablePanel tablePanel, User user) {
        shop = shopInterface;
        customerMenuListener = listener;
        articleTablePanel = tablePanel;
        loggedInUser = user;

        setup();

        setupEvents();
    }

    private void setup() {
        setLayout(new GridLayout(5, 1, 5, 5));

        addButton = new JButton("ADD");
        removeButton = new JButton("REMOVE");
        cartButton = new JButton("CART");
        emptyButton = new JButton("EMPTY");
        buyButton = new JButton("BUY");

        add(addButton);
        add(removeButton);
        add(cartButton);
        add(emptyButton);
        add(buyButton);

        setBorder(BorderFactory.createTitledBorder("Menu"));
        setVisible(true);
    }

    private void setupEvents() {
        addButton.addActionListener(new CartActionListener());
        removeButton.addActionListener(new CartActionListener());
        cartButton.addActionListener(new CartActionListener());
        emptyButton.addActionListener(new CartActionListener());
        buyButton.addActionListener(new CartActionListener());
    }

   public class CartActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Article article = null;
            int articleID;

            if (e.getSource().equals(addButton)) {
                try {
                    try {
                        articleID = (int) (articleTablePanel.getValueAt(articleTablePanel.getSelectedRow(), 0));
                        article = shop.getArticle(articleID);
                    } catch (ArticleNotFoundException exception) {
                        JOptionPane.showMessageDialog(getRootPane(),
                                exception.getMessage(),
                                "ShoppingCart",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    String amount_string = JOptionPane.showInputDialog(getRootPane(), "Amount: ",
                            "Add Article to Cart", JOptionPane.PLAIN_MESSAGE);
                    if  (!(amount_string.matches("^[A-Za-zäüöÄÜÖ]$+") || amount_string.equals(""))) {
                        int amount = Integer.parseInt(amount_string);
                        User user = shop.getUser(loggedInUser.getUserNr());
                        shop.addToCart(user, article, amount);
                        customerMenuListener.onAddedToCart();
                    } else {
                        JOptionPane.showMessageDialog(getRootPane(), "Form is incomplete and/or incorrectly filled out!",
                                "Form", JOptionPane.PLAIN_MESSAGE);
                    }
                } catch(ArrayIndexOutOfBoundsException exception2 ){
                        JOptionPane.showMessageDialog(getRootPane(),
                                "No Article Selected",
                                "ShoppingCart",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            else if (e.getSource().equals(removeButton)) {
                try {
                    try {
                        articleID = (int) (articleTablePanel.getValueAt(articleTablePanel.getSelectedRow(), 0));
                        article = shop.getArticle(articleID);
                    } catch (ArticleNotFoundException exception ){
                        JOptionPane.showMessageDialog(getRootPane(),
                                exception.getMessage(),
                                "ShoppingCart",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    String amount_string = JOptionPane.showInputDialog(getRootPane(), "Amount: ",
                            "remove Article to Cart", JOptionPane.PLAIN_MESSAGE);
                    if (!(amount_string.matches("^[A-Za-zäüöÄÜÖ]$+") || amount_string.equals(""))) {
                        int amount = Integer.parseInt(amount_string);
                        User user = shop.getUser(loggedInUser.getUserNr());
                        shop.removeFromCart(user, article, amount);
                        customerMenuListener.onRemovedFromCart();
                    } else {
                        JOptionPane.showMessageDialog(getRootPane(), "Form is incomplete and/or incorrectly filled out!",
                                "Form", JOptionPane.PLAIN_MESSAGE);
                    }
                } catch(ArrayIndexOutOfBoundsException exception2 ){
                    JOptionPane.showMessageDialog(getRootPane(),
                            "No Article Selected",
                            "ShoppingCart",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else if (e.getSource().equals(cartButton)) {
                User user = shop.getUser(loggedInUser.getUserNr());
                Map<Article, Integer> shoppingCart = user.getShoppingCart().getCart();
                if (!(shoppingCart.isEmpty())) {
                    customerMenuListener.onShowCart(shoppingCart);
                } else {
                    JOptionPane.showMessageDialog(getRootPane(),
                            "Shopping Cart is empty",
                            "Shopping Cart",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else if (e.getSource().equals(emptyButton)) {
                User user = shop.getUser(loggedInUser.getUserNr());
                shop.emptyCart(user);
                JOptionPane.showMessageDialog(getRootPane(),
                        "Shopping Cart was emptied",
                        "Shopping Cart",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            else if (e.getSource().equals(buyButton)) {
                User user = shop.getUser(loggedInUser.getUserNr());
                Map shoppingCart = user.getShoppingCart().getCart();
                if (!(shoppingCart.isEmpty())) {
                    Invoice invoice = shop.buy(user);
                    customerMenuListener.onBuy(invoice);
                } else {
                    JOptionPane.showMessageDialog(getRootPane(),
                            "Shopping Cart is empty",
                            "Shopping Cart",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}
