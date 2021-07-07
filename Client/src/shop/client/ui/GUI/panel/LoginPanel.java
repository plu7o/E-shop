package shop.client.ui.GUI.panel;

import shop.common.exceptions.LoginFailedException;
import shop.common.interfaces.ShopInterface;
import shop.common.valueObject.User;
import shop.common.exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private ShopInterface shop;
    public User loggedInUser;

    private LoginListener loginListener;
    private JButton loginButton = new JButton("login");
    private JButton logoutButton = new JButton("Logout");
    private JButton signUpButton = new JButton("Sign Up");

    public interface LoginListener {
        public void onLogin(User user);
    }

    public LoginPanel(ShopInterface shopInterface, LoginListener listener) {
        shop = shopInterface;
        loginListener = listener;

        setup();
        setupEvents();
    }

    public void setup() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        Dimension borderMinSize = new Dimension(10, 5);
        Dimension borderPrefSize = new Dimension(35, 10);
        Dimension borderMaxSize = new Dimension(40, 10);
        add(new Box.Filler(borderMinSize, borderPrefSize, borderMaxSize));

        add(signUpButton);
        add(new Box.Filler(borderMinSize, borderPrefSize, borderMaxSize));
        add(loginButton);

        Dimension fillerMinSize = new Dimension(5, 20);
        Dimension fillerPrefSize = new Dimension(5, Short.MAX_VALUE);
        Dimension fillerMaxSize = new Dimension(5, Short.MAX_VALUE);

        setBorder(BorderFactory.createTitledBorder("User"));
    }

    public void setupEvents() {
        loginButton.addActionListener(new LoginActionListener());
        signUpButton.addActionListener(new LoginActionListener());
        logoutButton.addActionListener(new LoginActionListener());
    }

    public class LoginActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(loginButton)) {
                JTextField usernameTextField = new JTextField();
                JPasswordField passwordField = new JPasswordField();
                Object[] loginMessage = {
                        "Username:", usernameTextField,
                        "Password:", passwordField
                };
                int loginOption = JOptionPane.showConfirmDialog(getRootPane(), loginMessage, "Login", JOptionPane.OK_CANCEL_OPTION);
                if (loginOption == JOptionPane.OK_OPTION) {
                    String username = usernameTextField.getText();
                    String password = new String(passwordField.getPassword());
                    try {
                        loggedInUser = shop.login(username, password);
                        JOptionPane.showMessageDialog(getRootPane(),
                                "Welcome " + loggedInUser.getUsername() + "You have successfully logged in!",
                                "Login",
                                JOptionPane.INFORMATION_MESSAGE);
                        loginButton.setVisible(false);
                        signUpButton.setVisible(false);
                        logoutButton.setVisible(true);
                        add(logoutButton, BorderLayout.PAGE_END);

                    } catch (LoginFailedException exception) {
                        JOptionPane.showMessageDialog(getRootPane(),
                                exception.getMessage(),
                                "Login",
                                JOptionPane.ERROR_MESSAGE);
                        usernameTextField.setText("");
                        passwordField.setText("");
                    }
                    loginListener.onLogin(loggedInUser);
                }
            } else if (e.getSource().equals(logoutButton)) {
                loggedInUser = null;

                loginButton.setVisible(true);
                signUpButton.setVisible(true);
                logoutButton.setVisible(false);

                remove(logoutButton);
                loginListener.onLogin(loggedInUser);
            } else if (e.getSource().equals(signUpButton)) {
                JTextField nameField = new JTextField();
                JTextField usernameField = new JTextField();
                JPasswordField formPasswordField = new JPasswordField();
                Object[] SignUpMessage = {
                        "Name:", nameField,
                        "Username:", usernameField,
                        "Password", formPasswordField
                };
                int SignUpOption = JOptionPane.showConfirmDialog(getRootPane(), SignUpMessage, "Sign Up", JOptionPane.OK_CANCEL_OPTION);
                if (SignUpOption == JOptionPane.OK_OPTION) {
                    String name = nameField.getText();
                    String username = usernameField.getText();
                    String password = new String(formPasswordField.getPassword());
                    if (!(name.matches("^[0-9]$+"))) {
                        try {
                            shop.signup(name, username, password);
                            JOptionPane.showMessageDialog(getRootPane(),
                                    "You successfully registered your account!",
                                    "Sign Up",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (UserAlreadyExistsException exception2) {
                            JOptionPane.showMessageDialog(getRootPane(),
                                    exception2.getMessage(),
                                    "Sign Up",
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
            }
        }
    }
}
