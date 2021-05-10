package domain.exceptions;

import valueObject.User;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(User user, String msg) {
        super("User with Username: " + user.getUsername() + "already exists");
    }

}
