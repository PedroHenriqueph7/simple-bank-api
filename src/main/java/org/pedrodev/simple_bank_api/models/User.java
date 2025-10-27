package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.validator.constraints.br.CPF;
import org.pedrodev.simple_bank_api.models.enums.UserRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    @CPF
    private String cpf;
    @Column(nullable = false, unique = true)

    private String email;
    @Column(nullable = false)

    private String senha;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    public User(String nomeCompleto, String cpf, String email, String senha, UserRole role) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }
}
