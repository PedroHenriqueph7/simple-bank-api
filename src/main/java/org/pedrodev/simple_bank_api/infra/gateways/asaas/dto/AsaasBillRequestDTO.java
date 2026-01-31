package org.pedrodev.simple_bank_api.infra.gateways.asaas.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record AsaasBillRequestDTO(
        @NotBlank(message = "A linha digitavel do Boleto é obrigatório para pagamento")
        String identificationField,

        BigDecimal value,

        String description,

        String scheduleDate
) {
}
