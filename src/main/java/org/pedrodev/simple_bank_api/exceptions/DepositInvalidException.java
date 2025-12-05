package org.pedrodev.simple_bank_api.exceptions;

public class DepositInvalidException extends RuntimeException {
    public DepositInvalidException(String message) {
        super(message);
    }
}
