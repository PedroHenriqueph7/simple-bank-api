package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record BillPaymentRequestDTO(
        @NotBlank
        String identificationField,

        BigDecimal value,

        String description,

        String scheduleDate
        ) {
}
