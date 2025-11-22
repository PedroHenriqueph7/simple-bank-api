package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequestDTO(
        @NotNull(message = "O id do pagador é obrigatório")
        @Positive(message = "O id deve ser positivo!")
        Long recebedor_id,

        @NotNull(message = "O valor da transação é obrigatório")
        @Positive(message = "O valor da transação precisa ser positivo!")
        BigDecimal valor) {
}
