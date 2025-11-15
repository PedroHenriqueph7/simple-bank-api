package org.pedrodev.simple_bank_api.exceptions;

public class TheUserAlreadyHasAnAccountException extends RuntimeException {
    public TheUserAlreadyHasAnAccountException(String message) {
        super(message);
    }
}
