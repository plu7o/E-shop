package shop.server.domain;

import shop.common.exceptions.LoginFailedException;
import shop.common.exceptions.UserAlreadyExistsException;
import shop.common.exceptions.UserNotFoundException;
import shop.common.valueObject.Article;
import shop.common.valueObject.Invoice;
import shop.common.valueObject.User;
import shop.server.persistence.*;

import java.io.IOException;
import java.util.*;

public class UserAdministration {
        private List<User> customers = new Vector<User>();
        private List<User> staff = new Vector<User>();
        private List<User> users = new Vector<User>();

        private PersistenceManager pm = new FilePersistenceManager();

        public void add(User user) throws UserAlreadyExistsException {
                if (user.isCustomer()) {
                        if (customers.contains(user)) {
                                throw new UserAlreadyExistsException("");
                        }
                        customers.add(user);
                }
                if (user.isStaff()) {
                        if (staff.contains(user)) {
                                throw new UserAlreadyExistsException("");
                        }
                        staff.add(user);
                }
        }

        public void delete(User user) {
                if (user.isCustomer()) {
                        customers.remove(user);
                }
                if (user.isStaff()) {
                        staff.remove(user);
                }
        }

        public List<User> getAllUsers() {
                users.addAll(customers);
                users.addAll(staff);
                return users;
        }

        public User getUser(int userNr) {
                for (User user : getAllUsers()) {
                        if (user.getUserNr() == userNr) {
                                return user;
                        }
                }
                return null;
        }

        public List<User> searchCustomer(int userNr) {
                List<User> search = new Vector<>();

                Iterator<User> iter = customers.iterator();
                while (iter.hasNext()) {
                        User user = iter.next();
                        if (user.getUserNr() == userNr)
                                search.add(user);
                }

                Collections.sort(search, Comparator.comparingInt(shop.common.valueObject.User::getUserNr));
                return search;
        }

        public List<User> searchUsers(int userNr) {
                List<User> search = new Vector<>();

                Iterator<User> iter = getAllUsers().iterator();
                while (iter.hasNext()) {
                        User user = iter.next();
                        if (user.getUserNr() == userNr)
                                search.add(user);
                }

                Collections.sort(search, Comparator.comparingInt(shop.common.valueObject.User::getUserNr));
                return search;
        }

        public List<User> searchStaff(int userNr) {
                List<User> search = new Vector<>();

                Iterator<User> iter = staff.iterator();
                while (iter.hasNext()) {
                        User user = iter.next();
                        if (user.getUserNr() == userNr)
                                search.add(user);
                }

                Collections.sort(search, Comparator.comparing(shop.common.valueObject.User::getName));
                return search;
        }

        public List<User> getCustomerList() {
                Collections.sort(customers, Comparator.comparing(shop.common.valueObject.User::getName));
                return new Vector<User>(customers);
        }

        public List<User> getStaff() {
                Collections.sort(staff, Comparator.comparing(shop.common.valueObject.User::getName));
                return new Vector<User>(staff);
        }

        public User login(String username, String password) throws LoginFailedException {
                User user = findUsername(username);

                if (user.getPassword().equals(password)) {
                        return user;
                }
                throw new LoginFailedException("Username or Password is incorrect");
        }

        public int register(String name, String username, String password) throws UserAlreadyExistsException {
                try {
                        User user = findUsername(username);
                        throw new UserAlreadyExistsException("");
                } catch (LoginFailedException e) {
                        User user = new User(name, userIDGen(), username, password, false, true);
                        customers.add(user);
                        return user.getUserNr();
                }
        }

        private User findUsername(String username) throws LoginFailedException {
                for (User user : customers) {
                        if (user.getUsername().equals(username)) {
                                return user;
                        }
                }
                for (User user : staff) {
                        if (user.getUsername().equals(username)) {
                                return user;
                        }
                }
                throw new LoginFailedException("404 User Not Found");
        }

        public int userIDGen() {
                int userID = 100;
                for (User user : customers) {
                        if (userID <= user.getUserNr()) {
                                userID = user.getUserNr() + 1;
                        }
                }
                return userID;
        }

        public int staffIDGen() {
                double Id = Math.floor((Math.random() * 100));
                int staffID = (int) Id;
                for (User user : staff) {
                        while (staffID == user.getUserNr()) {
                                Id = Math.floor((Math.random() * 100));
                                staffID = (int) Id;
                        }
                }
                return staffID;
        }

        public void changeUserData(User user, String name, String username, String password, String address) {
                if (!name.equals("")) {
                        user.setName(name);
                }
                if (!username.equals("")) {
                        user.setUsername(username);
                }
                if (!password.equals("")) {
                        user.setPassword(password);
                }
                if (!address.equals("")) {
                        user.setAddress(address);
                }
        }

        public void readUserData(String data) throws IOException {
                pm.openForReading(data);
                User user;
                do {
                        user = pm.loadUser();
                        if (user != null) {
                                try {
                                        add(user);
                                } catch (UserAlreadyExistsException e) {

                                }
                        }
                } while (user != null);
                pm.close();
        }

        public void saveUser(String data) throws IOException {
                pm.openForWriting(data);
                for (User user : getAllUsers()) {
                        pm.saveUser(user);
                }
                pm.close();
        }

        public void addToCart(User user, Article article, int amount) {
                int updatedStock = article.getStock() - amount;
                if (!(updatedStock <= 0)) {
                        user.getShoppingCart().addToCart(article, amount);
                        article.setStock(updatedStock);
                } else if (updatedStock == 0) {
                        user.getShoppingCart().addToCart(article, amount);
                        article.setStock(updatedStock);
                        article.setAvailable(false);
                } else {
                        //throw new ShoppingCartException
                }
        }

        public void removeFromCart(User user, Article article, int amount) {
                if (user.getShoppingCart().removeFromCart(article, amount)) {
                        article.addStock(amount);
                        article.setAvailable(true);
                } else {
                        // throw new Shopping cart Exception
                }
        }

        public void emptyCart(User user) {
                user.getShoppingCart().emptyCart();
        }

        public Invoice buy(User user) {
                Invoice invoice = new Invoice(user);
                return invoice;
        }
}
