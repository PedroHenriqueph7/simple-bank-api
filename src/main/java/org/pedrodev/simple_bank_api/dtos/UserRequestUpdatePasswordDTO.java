package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestUpdatePasswordDTO(

        @NotBlank
        String currentPassword,
        @NotBlank
        String newPassword,
        @NotBlank
        String confirmNewPassword
) { }
