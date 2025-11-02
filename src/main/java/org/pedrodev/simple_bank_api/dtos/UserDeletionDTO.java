package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.NotBlank;

public record UserDeletionDTO(
        @NotBlank
        String password
) { }
