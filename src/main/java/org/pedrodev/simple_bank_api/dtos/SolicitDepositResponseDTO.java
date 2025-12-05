package org.pedrodev.simple_bank_api.dtos;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record SolicitDepositResponseDTO(String pixCode, BigDecimal valor, ZonedDateTime dataExpiracao) {
}
