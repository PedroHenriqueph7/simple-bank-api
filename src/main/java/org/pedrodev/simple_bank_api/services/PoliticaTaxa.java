package org.pedrodev.simple_bank_api.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface PoliticaTaxa {

    BigDecimal calcularValorLiquido(BigDecimal valor);
}
