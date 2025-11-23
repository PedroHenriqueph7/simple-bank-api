package org.pedrodev.simple_bank_api.exceptions;

public class TransferLimitExceededException extends RuntimeException {
    public TransferLimitExceededException(String message) {
        super(message);
    }
}
