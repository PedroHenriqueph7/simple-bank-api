package org.pedrodev.simple_bank_api.exceptions;

public class DeactivatedUserException extends RuntimeException {
    public DeactivatedUserException(String message) {
        super(message);
    }
}
