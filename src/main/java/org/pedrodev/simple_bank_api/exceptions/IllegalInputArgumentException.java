package org.pedrodev.simple_bank_api.exceptions;

public class IllegalInputArgumentException extends RuntimeException {
    public IllegalInputArgumentException(String message) {
        super(message);
    }
}
