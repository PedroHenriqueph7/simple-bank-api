package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SolicitDepositDTO(@NotNull(message = "Enter the deposit amount; this field must be filled in.") BigDecimal valor) {
}
