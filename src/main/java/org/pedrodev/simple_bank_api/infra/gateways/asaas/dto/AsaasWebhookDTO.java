package org.pedrodev.simple_bank_api.infra.gateways.asaas.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignorar Campos que não serão utilizados
public record AsaasWebhookDTO(
        String event,
        AsaasBillPaymentDTO bill
) {

    public record AsaasBillPaymentDTO(
            String id,       // O ID da transação (ex: bpm_1234)
            String status,   // PAID, FAILED, etc.
            BigDecimal value // O valor pago
    ) {}
}
