package org.pedrodev.simple_bank_api.infra.gateways.asaas.dto;

import java.math.BigDecimal;

public record AsaasBillRequestDTO(
        String identificationField,

        BigDecimal value,

        String description,

        String scheduleDate
) {
}
