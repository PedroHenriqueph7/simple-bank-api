package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;
import org.pedrodev.simple_bank_api.models.enums.UserRole;

public record RegisterDTO(
        @NotBlank(message = "The name is required!")
        String nomeCompleto,

        @NotBlank(message = "The cpf is required!")
        @CPF
        String cpf,

        @NotBlank(message = "The email is required!")
        @Email
        String email,

        @NotBlank(message = "A password is required!")
        @Size(min = 8, message = "The password must contain at least 8 characters.")
        String password,

        @NotNull
        UserRole role
) { }
