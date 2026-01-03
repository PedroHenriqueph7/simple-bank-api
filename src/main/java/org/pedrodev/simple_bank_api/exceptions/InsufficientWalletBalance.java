package org.pedrodev.simple_bank_api.exceptions;

public class InsufficientWalletBalance extends RuntimeException {
    public InsufficientWalletBalance(String message) {
        super(message);
    }

    public InsufficientWalletBalance() { super("insufficient user wallet balance"); }
}
