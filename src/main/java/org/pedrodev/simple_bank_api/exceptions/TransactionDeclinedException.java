package org.pedrodev.simple_bank_api.exceptions;

public class TransactionDeclinedException extends RuntimeException {
    public TransactionDeclinedException(String message) {
        super(message);
    }
}
