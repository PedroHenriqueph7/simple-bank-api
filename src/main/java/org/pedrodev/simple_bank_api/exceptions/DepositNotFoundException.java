package org.pedrodev.simple_bank_api.exceptions;

public class DepositNotFoundException extends RuntimeException {
    public DepositNotFoundException(String message) {
        super(message);
    }
}
