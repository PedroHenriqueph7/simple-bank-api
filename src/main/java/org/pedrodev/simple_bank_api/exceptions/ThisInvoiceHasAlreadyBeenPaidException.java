package org.pedrodev.simple_bank_api.exceptions;

public class ThisInvoiceHasAlreadyBeenPaidException extends RuntimeException {
    public ThisInvoiceHasAlreadyBeenPaidException(String message) {
        super(message);
    }

    public ThisInvoiceHasAlreadyBeenPaidException(){ super("This invoice has already been paid!");}
}
