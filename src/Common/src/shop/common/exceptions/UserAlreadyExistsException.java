package shop.common.exceptions;

import shop.common.valueObject.User;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
