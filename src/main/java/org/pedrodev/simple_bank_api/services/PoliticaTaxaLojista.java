package org.pedrodev.simple_bank_api.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PoliticaTaxaLojista implements PoliticaTaxa {
    @Override
    public BigDecimal calcularValorLiquido(BigDecimal valor) {

        BigDecimal valorLiquido = valor.multiply(new BigDecimal("0.98"));

        return valorLiquido;
    }
}
