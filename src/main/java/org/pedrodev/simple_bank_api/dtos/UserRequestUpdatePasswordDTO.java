package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestUpdatePasswordDTO(

        @NotBlank(message = "Enter your current password.!")
        @Size(min = 8, message = "The password must contain at least 8 characters.")
        String currentPassword,
        @NotBlank(message = "Enter your new password.!")
        @Size(min = 8, message = "The password must contain at least 8 characters.")
        String newPassword,
        @NotBlank(message = "Confirm your new password!")
        @Size(min = 8, message = "The password must contain at least 8 characters.")
        String confirmNewPassword
) { }
