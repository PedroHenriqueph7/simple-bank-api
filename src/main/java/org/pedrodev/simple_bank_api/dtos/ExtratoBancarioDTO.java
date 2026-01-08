package org.pedrodev.simple_bank_api.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ExtratoBancarioDTO {

    BigDecimal getValor();
    String getDescricao();
    LocalDateTime getDataMovimentacao();
    String getTipo();

}
