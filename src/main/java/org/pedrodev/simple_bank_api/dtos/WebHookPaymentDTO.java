package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.NotBlank;

public record WebHookPaymentDTO(@NotBlank(message = "Enter the Pix code (copy and paste), this field is required!") String pixCode) {
}
