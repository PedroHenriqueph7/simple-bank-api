package org.pedrodev.simple_bank_api.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransactionRequestDTO(

        @Schema(description = "Id do Recebedor", example = "5")
        @NotNull(message = "O id do recebedor é obrigatório")
        @Positive(message = "O id deve ser positivo!")
        Long recebedor_id,

        @Schema(description = "Valor da Transação", example = "100.00")
        @NotNull(message = "O valor da transação é obrigatório")
        @Positive(message = "O valor da transação precisa ser positivo!")
        BigDecimal valor) {
}
