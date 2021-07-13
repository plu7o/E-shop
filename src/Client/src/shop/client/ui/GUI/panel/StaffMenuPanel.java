package shop.client.ui.GUI.panel;

import shop.common.exceptions.ArticleAlreadyExistsException;
import shop.common.exceptions.UserAlreadyExistsException;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class StaffMenuPanel extends JPanel {

    private ShopInterface shop;
    private StaffMenuListener staffMenuListener;
    private ArticleTablePanel articleTablePanel;
    public User loggedInUser;

    private JButton addArticleButton = new JButton("Add Article");
    private JButton deleteArticleButton = new JButton("Delete Article");
    private JButton saveArticleButton = new JButton("Save Article");
    private JButton showArticleButton = new JButton("Show Article");

    private JButton addUserButton = new JButton("Add User");
    private JButton deleteUserButton = new JButton("Delete User");
    private JButton saveUserButton = new JButton("Save User");
    private JButton showUserButton = new JButton("Show User");

    private JPanel subPanelArticle = new JPanel();
    private JPanel subPanelUser = new JPanel();

    public interface StaffMenuListener {
        public void onArticleAdded();
        public void onArticleDeleted();
        public void onUserAdded();
        public void onUserDeleted();
        public void onShowArticle();
        public void onShowUser();
    }

    public StaffMenuPanel (ShopInterface shopInterface, StaffMenuListener listener, ArticleTablePanel tablePanel, User user) {
        shop = shopInterface;
        articleTablePanel = tablePanel;
        staffMenuListener = listener;
        loggedInUser = user;

        setup();
        setupEvents();
    }

    private void setup() {
        setLayout(new GridLayout(2, 1, 5, 5));

        subPanelArticle.setLayout(new GridLayout(4,1,5,5));
        subPanelArticle.add(addArticleButton);
        subPanelArticle.add(deleteArticleButton);
        subPanelArticle.add(saveArticleButton);
        subPanelArticle.add(showArticleButton);
        subPanelArticle.setBorder(BorderFactory.createTitledBorder("Article Administration"));

        subPanelUser.setLayout(new GridLayout(4,1,5,5));
        subPanelUser.add(addUserButton);
        subPanelUser.add(deleteUserButton);
        subPanelUser.add(saveUserButton);
        subPanelUser.add(showUserButton);
        subPanelUser.setBorder(BorderFactory.createTitledBorder("User Administration"));

        add(subPanelArticle, BorderLayout.NORTH);
        add(subPanelUser, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void setupEvents() {
        addArticleButton.addActionListener(new ArticleListener());
        deleteArticleButton.addActionListener(new ArticleListener());
        saveUserButton.addActionListener(new ArticleListener());
        showArticleButton.addActionListener(new ArticleListener());

        addUserButton.addActionListener(new UserListener());
        deleteUserButton.addActionListener(new UserListener());
        saveUserButton.addActionListener(new UserListener());
        showUserButton.addActionListener(new UserListener());
    }

    public class ArticleListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(addArticleButton)) {

                JCheckBox isMassArticleBox = new JCheckBox("Mass Article:");
                JTextField packageSizeField = new JTextField();
                JPanel ui = new JPanel(new BorderLayout());
                ui.add(isMassArticleBox, BorderLayout.NORTH);

                CardLayout c1 = new CardLayout();
                JPanel cards = new JPanel(c1);
                ui.add(cards, BorderLayout.SOUTH);
                JPanel mass = new JPanel(new BorderLayout());
                mass.add(new JLabel("Package Size:"), BorderLayout.NORTH);
                mass.add(packageSizeField, BorderLayout.SOUTH);
                cards.add(new JPanel(), "notext");
                cards.add(mass, "text");

                ItemListener al = e1 -> {
                    if(isMassArticleBox.isSelected()) {
                        c1.show(cards, "text");
                    } else {
                        c1.show(cards, "notext");
                    }
                };
                isMassArticleBox.addItemListener(al);
                JTextField priceTextField = new JTextField();
                JTextField nameTextField = new JTextField();
                JTextField stockTextField = new JTextField();
                JCheckBox availableBox = new JCheckBox();

                Object[] message = {
                        "Article name:", nameTextField,
                        "Price:", priceTextField,
                        "Stock:", stockTextField,
                        null, ui,
                        "Available:", availableBox
                };
                int option = JOptionPane.showConfirmDialog(getRootPane(), message, "Add Article", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String name = nameTextField.getText();
                    String price_string = priceTextField.getText();
                    String stock_string = stockTextField.getText();
                    String packageSize_string = packageSizeField.getText();

                    if (!(price_string.matches("^[A-Za-zäüöÄÜÖ]$+") ||
                            price_string.equals("") ||
                            stock_string.matches("^[A-Za-zäüöÄÜÖ]$+") ||
                            stock_string.equals("") ||
                            name.equals("") ||
                            packageSize_string.matches("^[A-Za-zäüöÄÜÖ]$+"))) {

                        double price = Double.parseDouble(price_string);
                        int stock = Integer.parseInt(stock_string);
                        boolean available = availableBox.isSelected();

                        if (isMassArticleBox.isSelected()) {
                            int packageSize = Integer.parseInt(packageSize_string);
                            try {
                                User user = shop.getUser(loggedInUser.getUserNr());
                                shop.addMassArticle(user, name, price, stock, available, packageSize);
                                staffMenuListener.onArticleAdded();
                            } catch (ArticleAlreadyExistsException exception) {
                                JOptionPane.showMessageDialog(getRootPane(),
                                        "Article Already Exists",
                                        "Formular",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else if (availableBox.isSelected()) {
                            try {
                                User user = shop.getUser(loggedInUser.getUserNr());
                                shop.addArticle(user, name, price, stock, available);
                                staffMenuListener.onArticleAdded();
                            } catch (ArticleAlreadyExistsException exception) {
                                JOptionPane.showMessageDialog(getRootPane(),
                                        "Article Already Exists",
                                        "Formular",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(getRootPane(),
                                    "Select Article type",
                                    "Formular",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(getRootPane(),
                                "Formular falsch ausgefüllt",
                                "Formular",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else if(e.getSource().equals(deleteArticleButton)) {
                String article_id = JOptionPane.showInputDialog(getRootPane(), "Article ID: ",
                        "Article Administration", JOptionPane.PLAIN_MESSAGE);
                if  (!(article_id.matches("^[A-Za-zäüöÄÜÖ]$+") || article_id.equals(""))) {
                    int articleID = Integer.parseInt(article_id);
                    User user = shop.getUser(loggedInUser.getUserNr());
                    shop.deleteArticle(user, articleID);
                    staffMenuListener.onArticleDeleted();
                } else {
                    JOptionPane.showMessageDialog(getRootPane(), "Form is incomplete and/or incorrectly filled out!",
                            "Form", JOptionPane.PLAIN_MESSAGE);
                }
            } else if(e.getSource().equals(saveArticleButton)) {
                try {
                    shop.saveArticle();
                    JOptionPane.showMessageDialog(getRootPane(),
                            "Artikelsaved",
                            "Article Administration",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException exception) {
                    exception.getMessage();
                }
            } else if (e.getSource().equals(showArticleButton)) {
                staffMenuListener.onShowArticle();
            }
        }
    }

    public class UserListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(addUserButton)) {
                JTextField nameField = new JTextField();
                JTextField usernameField = new JTextField();
                JPasswordField formPasswordField = new JPasswordField();
                JCheckBox isCustomer = new JCheckBox();
                JCheckBox isStaff = new JCheckBox();
                Object[] Message = {
                        "Name:", nameField,
                        "Username:", usernameField,
                        "Password", formPasswordField,
                        "Customer", isCustomer,
                        "Staff", isStaff
                };
                int option = JOptionPane.showConfirmDialog(getRootPane(), Message, "Add User", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String name = nameField.getText();
                    String username = usernameField.getText();
                    String password = new String(formPasswordField.getPassword());
                    if (!(name.matches("^[0-9]$+") ||
                            name.equals("") ||
                            username.equals("") ||
                            password.equals("")) ||
                            isCustomer.isSelected() && isStaff.isSelected()) {
                        try {
                            if (isCustomer.isSelected()) {
                                shop.addCustomer(name, username, password);
                                staffMenuListener.onUserAdded();
                                JOptionPane.showMessageDialog(getRootPane(),
                                        "User Added",
                                        "User Administration",
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else if (isStaff.isSelected()) {
                                User user = shop.getUser(loggedInUser.getUserNr());
                                shop.addStaff(user, name, username, password);
                                staffMenuListener.onUserAdded();
                                JOptionPane.showMessageDialog(getRootPane(),
                                        "User Added",
                                        "User Administration",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (UserAlreadyExistsException exception2) {
                            JOptionPane.showMessageDialog(getRootPane(),
                                    exception2.getMessage(),
                                    "User Administration",
                                    JOptionPane.ERROR_MESSAGE);
                            nameField.setText("");
                            usernameField.setText("");
                            formPasswordField.setText("");
                        }
                    } else {
                        JOptionPane.showMessageDialog(getRootPane(), "Form is incomplete and/or incorrectly filled out!",
                                "Form", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            } else if (e.getSource().equals(deleteUserButton)) {
                String user_id = JOptionPane.showInputDialog(getRootPane(), "User ID: ",
                        "User Administration", JOptionPane.PLAIN_MESSAGE);

                if  (!(user_id.matches("^[A-Za-zäüöÄÜÖ]$+") || user_id.equals(""))) {
                    int userID = Integer.parseInt(user_id);
                    User user = shop.getUser(userID);
                    User admin = shop.getUser(loggedInUser.getUserNr());
                    shop.deleteUser(admin, user);
                    staffMenuListener.onUserDeleted();
                } else {
                    JOptionPane.showMessageDialog(getRootPane(), "Form is incomplete and/or incorrectly filled out!",
                            "Form", JOptionPane.PLAIN_MESSAGE);
                }
            } else if (e.getSource().equals(saveUserButton)) {
                try {
                    shop.saveUser();
                    JOptionPane.showMessageDialog(getRootPane(),
                            "User saved",
                            "User Administration",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException exception) {
                    exception.getMessage();
                }
            } else if (e.getSource().equals(showUserButton)) {
                staffMenuListener.onShowUser();
            }
        }
    }
}
