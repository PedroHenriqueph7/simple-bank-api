package org.pedrodev.simple_bank_api.dtos;

import org.pedrodev.simple_bank_api.models.BillPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillPaymentResponseDTO(
        Long id,
        String status,
        BigDecimal value,
        String recipient,
        String transactionId, // O ID do Asaas
        LocalDateTime createdAt
) {

    public BillPaymentResponseDTO(BillPayment entity) {
        this(
                entity.getId(),
                entity.getPaymentStatus().toString(),
                entity.getValue(),
                entity.getBeneficiary(),
                entity.getExternalReference(),
                entity.getCreatedAt()
        );
    }
}
