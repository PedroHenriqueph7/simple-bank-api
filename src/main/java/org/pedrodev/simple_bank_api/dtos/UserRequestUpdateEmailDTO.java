package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestUpdateEmailDTO(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) { }
