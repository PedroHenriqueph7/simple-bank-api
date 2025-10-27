package org.pedrodev.simple_bank_api.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.br.CPF;
import org.pedrodev.simple_bank_api.models.enums.UserRole;

@Getter
@AllArgsConstructor
public class UserRegisterDTO {

    @NotBlank
    private String nomeCompleto;

    @CPF
    @NotBlank
    private String cpf;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private UserRole role;

}
