package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.*;
import org.pedrodev.simple_bank_api.models.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_user")
public class User implements UserDetails {

    private boolean ativo = true;
    // Nome lógico para o JPA
    // Nome da sequência no Banco de Dados// A escolha mais segura e compatível// Diz para usar sequência// Aponta para o gerador definido acima
    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true)
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       if(this.role == UserRole.COMUM) return List.of(new SimpleGrantedAuthority("ROLE_COMUM"), new SimpleGrantedAuthority("ROLE_LOJISTA"));
       else return List.of(new SimpleGrantedAuthority("ROLE_LOJISTA"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return cpf;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
