package domain;
import valueObject.User;

public class UserAdministration {
        User user = new User("name", 0, "pw");

        public void login() {
                System.out.println("Login: Benutzernamen eingeben");
                User getName;
                System.out.println("Passwort eingeben");
                User getPassword;
        }

        public void register() {
                System.out.println("Registrierung: Benutzernamen und Adresse eingeben");
                User setName;
                User setAddress;
                System.out.println("Bitte Passwort eingeben");
                User setPassword;
        }

        public void changeData() {
                System.out.println("Neuen Benutzernamen und/oder neue Adresse eingeben");
                User setName;
                User setAddress;
                System.out.println("Bitte Passwort eingeben");
                User getPassword;
        }

        public void buy() { //TODO
                /* und beim Kauf wird geleert
                zeitgleich Artikel aus Bestand nehmen
                 */
                User getShoppingCart;
                //gekauft
                //User shoppingCart = 0;
                //System.out.println(name + date + article + "x" + number + "Einzelpreis:" + price + "Gesamtpreis:" + totalPrice);
        }

        public void takeArticleShoppingCart() {

        }

        public void changeArticle() {

        }

        public void adminRights() {
                /*if(!staff) {

                }*/
        }
}
