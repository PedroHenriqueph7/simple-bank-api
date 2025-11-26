package org.pedrodev.simple_bank_api.exceptions;

public class TransactionNotAuthorizedException extends RuntimeException {
    public TransactionNotAuthorizedException(String message) {
        super(message);
    }
}
