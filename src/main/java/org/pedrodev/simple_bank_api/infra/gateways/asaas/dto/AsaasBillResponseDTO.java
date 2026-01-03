package org.pedrodev.simple_bank_api.infra.gateways.asaas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AsaasBillResponseDTO(

        String id,
        String status,

        BigDecimal value,
        BigDecimal fee,
        BigDecimal fine,
        BigDecimal interest,
        BigDecimal discount,

        String transactionReceiptUrl,
        String dueDate,

        String companyName,
        String identificationField
) { }
