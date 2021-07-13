package shop.client.ui.GUI.panel;

import shop.common.interfaces.ShopInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import shop.common.valueObject.Invoice;
import shop.common.valueObject.User;

public class TotalBarPanel extends JPanel {

    private ShopInterface shop;
    private TotalBarListener totalBarListener;
    public User loggedInUser;

    private JLabel totalLabel = new JLabel("Total:");
    private JButton buyButton = new JButton("Buy");
    private JButton backButton = new JButton("Back");
    private JPanel subPanel = new JPanel();
    private JPanel subPanel2 = new JPanel();

    public interface TotalBarListener {
        public void onBuy(Invoice invoice);
        public void onBack();
    }

    public TotalBarPanel(ShopInterface shopInterface, TotalBarListener listener, User user) {
        shop = shopInterface;
        totalBarListener = listener;
        loggedInUser = user;

        setup();
        setupEvents();
    }

    private void setup() {
        setLayout(new BorderLayout());
        subPanel.setLayout(new GridLayout(1,2,5,5));

        totalLabel.setText("Total:" + getTotal(loggedInUser));
        subPanel2.add(totalLabel);

        subPanel2.setLayout(new GridLayout(1,2,5,5));
        subPanel2.add(backButton);
        subPanel2.add(buyButton);

        add(subPanel, BorderLayout.WEST);
        add(subPanel2, BorderLayout.EAST);
        setBorder(BorderFactory.createBevelBorder(5));
        setVisible(true);
    }

    private String getTotal(User loggedInUser) {
        User user = shop.getUser(loggedInUser.getUserNr());
        return Double.toString(user.getShoppingCart().getTotal());
    }

    private void setupEvents() {
        buyButton.addActionListener(new BarListener());
        backButton.addActionListener(new BarListener());
    }

    public class BarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(buyButton)) {
                User user = shop.getUser(loggedInUser.getUserNr());
                Map shoppingCart = user.getShoppingCart().getCart();
                if (!(shoppingCart.isEmpty())) {
                    Invoice invoice = shop.buy(user);
                    totalBarListener.onBuy(invoice);
                } else {
                    JOptionPane.showMessageDialog(getRootPane(),
                            "Shopping Cart is empty",
                            "Shopping Cart",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (e.getSource().equals(backButton)) {
                totalBarListener.onBack();
            }
        }
    }
}
