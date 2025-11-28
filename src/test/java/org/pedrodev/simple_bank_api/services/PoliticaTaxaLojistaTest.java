package org.pedrodev.simple_bank_api.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PoliticaTaxaLojistaTest {


    @DisplayName("testar o calculo da taxa do lojista retornando com sucesso")
    @ParameterizedTest
    @ValueSource(ints = {20, 100, 230, 1000, 500})
    void politicaTaxaLojistaComSucesso(int valor) {

        // arrange
        BigDecimal valorInput = BigDecimal.valueOf(valor);

        // act
        PoliticaTaxaLojista politicaTaxaLojista = new PoliticaTaxaLojista();
        BigDecimal valorOutput = politicaTaxaLojista.calcularValorLiquido(valorInput);

        //assert
        assertEquals(valorInput.multiply(new BigDecimal("0.98")), valorOutput);
    }

}