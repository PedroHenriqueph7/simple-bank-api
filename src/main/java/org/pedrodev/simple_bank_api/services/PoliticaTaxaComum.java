package org.pedrodev.simple_bank_api.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PoliticaTaxaComum implements PoliticaTaxa {

    @Override
    public BigDecimal calcularValorLiquido(BigDecimal valor) {

        return valor;
    }
}
