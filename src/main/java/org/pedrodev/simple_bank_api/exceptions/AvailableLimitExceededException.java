package org.pedrodev.simple_bank_api.exceptions;

import java.math.BigDecimal;

public class AvailableLimitExceededException extends RuntimeException {

    private final BigDecimal valorDisponivel;

    public AvailableLimitExceededException(BigDecimal valorDisponivel) {
        super("Available limit exceeded");
        this.valorDisponivel = valorDisponivel;
    }

    public BigDecimal getValorDisponivel() {
        return valorDisponivel;
    }
}
