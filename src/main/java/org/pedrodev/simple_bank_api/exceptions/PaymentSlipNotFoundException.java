package org.pedrodev.simple_bank_api.exceptions;

public class PaymentSlipNotFoundException extends RuntimeException {
    public PaymentSlipNotFoundException(String message) {
        super(message);
    }

    public PaymentSlipNotFoundException() { super("payment slip not found"); }
}
