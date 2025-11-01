package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;
import org.pedrodev.simple_bank_api.models.enums.UserRole;

public record UserRegisterDTO(
        @NotBlank
        String nomeCompleto,

        @NotBlank
        @CPF
        String cpf,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        UserRole role
) { }
