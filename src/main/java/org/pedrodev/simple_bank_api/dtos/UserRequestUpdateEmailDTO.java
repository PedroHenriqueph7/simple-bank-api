package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestUpdateEmailDTO(
        @NotBlank(message = "The email is required.")
        @Email
        String email,
        @NotBlank(message = "A password is required.")
        @Size(min = 8, message = "The password must contain at least 8 characters.")
        String password
) { }
