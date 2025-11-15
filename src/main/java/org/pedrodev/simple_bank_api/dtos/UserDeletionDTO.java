package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDeletionDTO(
        @NotBlank(message = "A password is required")
        @Size(min = 8, message = "The password must contain at least 8 characters.")
        String password
) { }
