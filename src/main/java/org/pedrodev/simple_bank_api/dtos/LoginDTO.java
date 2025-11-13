package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record LoginDTO(
        @CPF
        @NotBlank
        String cpf,
        @NotBlank
        String password) {
}
