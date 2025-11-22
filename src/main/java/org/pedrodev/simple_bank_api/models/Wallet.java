package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_wallet")
public class Wallet {

    @EqualsAndHashCode.Include
    @Id
    @SequenceGenerator(name = "wallet_seq", sequenceName = "wallet_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal saldo;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Wallet(BigDecimal saldo, User user){
        this.saldo = saldo;
        this.user = user;
    }


    public void debitar(BigDecimal valor) {

        if (!this.user.isAtivo()) throw new RuntimeException("Esta conta está desativada!");

        if (valor.compareTo(saldo) > 0) throw new RuntimeException("Saldo Insuficiente!");

        this.saldo = this.saldo.subtract(valor);

    }

    public void creditar(BigDecimal valor) {

        if (!this.user.isAtivo()) throw new RuntimeException("Esta conta está desativada!");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("O valor deve ser positivo!");

        this.saldo = this.saldo.add(valor);
    }

}
