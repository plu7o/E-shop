package domain;
import domain.exceptions.LoginFailedException;
import domain.exceptions.UserAlreadyExistsException;
import valueObject.User;

import java.util.*;

public class UserAdministration {

        private List<User> customers =  new Vector<User>();
        private List<User> staff  = new Vector<User>();

        public void add(User user) throws UserAlreadyExistsException {
                if (user.isCustomer()) {
                        if (customers.contains(user)) {
                               throw new UserAlreadyExistsException(user, "");
                        }
                        User customer = user;
                        customers.add(customer);
                }
                if (user.isStaff()) {
                        if (staff.contains(user)) {
                                throw new UserAlreadyExistsException(user, "");
                        }
                        User Employee = user;
                        staff.add(Employee);
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

        public List<User> searchCustomer(int userNr) {
                List<User> search = new Vector<>();

                Iterator<User> iter = customers.iterator();
                while (iter.hasNext()) {
                        User user = iter.next();
                        if (user.getUserNr() == userNr)
                                search.add(user);
                }

                Collections.sort(search, Comparator.comparingInt(valueObject.User::getUserNr));
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

                Collections.sort(search, Comparator.comparingInt(valueObject.User::getUserNr));
                return search;
        }

        public List<User> getCustomerList() {
                Collections.sort(customers, Comparator.comparing(valueObject.User::getName));
                return new Vector<User>(customers);
        }

        public List<User> getStaff() {
                Collections.sort(staff, Comparator.comparing(valueObject.User::getName));
                return new Vector<User>(staff);
        }

        public User login(String username, String password) throws LoginFailedException {
                User user = findUsername(username);

                if (user.getPassword().equals(password)) {
                        return user;
                }
                throw new LoginFailedException("Username or Password is incorrect");
        }

        public void register(String name, String username, String password) throws UserAlreadyExistsException {
                try {
                        User user = findUsername(username);
                        throw new UserAlreadyExistsException(user, "");
                }
                catch (LoginFailedException e) {
                        User user = new User(name, userIDGen(), username, password, false, true);
                        customers.add(user);
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
                double Id = Math.floor((Math.random()*100));
                int staffID = (int)Id;
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

        public void buy() { //TODO
                /* und beim Kauf wird geleert
                zeitgleich Artikel aus Bestand nehmen
                 */
                //User getShoppingCart;
                //gekauft
                //User shoppingCart = 0;
                //System.out.println(name + date + article + "x" + number + "Einzelpreis:" + price + "Gesamtpreis:" + totalPrice);
        }
}




