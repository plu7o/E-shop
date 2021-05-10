package domain;
import valueObject.User;

public class UserAdministration {
        User user = new User("name", 0, "pw");

        public void login() {
                System.out.println("Login: Benutzernamen eingeben");
                user.getName();
                System.out.println("Passwort eingeben");
                user.getPassword();
        }

        public void register() {
                System.out.println("Registrierung: Benutzernamen und Adresse eingeben");
                user.setName();
                user.setAddress();
                System.out.println("Bitte Passwort eingeben");
                user.setPassword();
        }

        public void changeData() {
                System.out.println("Neuen Benutzernamen und/oder neue Adresse eingeben");
                user.setName();
                user.setAddress();
                System.out.println("Bitte Passwort eingeben");
                user.getPassword();
        }

        public void buy() { //TODO
                /* und beim Kauf wird geleert
                zeitgleich Artikel aus Bestand nehmen
                 */
                user.getShoppingCart();
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
