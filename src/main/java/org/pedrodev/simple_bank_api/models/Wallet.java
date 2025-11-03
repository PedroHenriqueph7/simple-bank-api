package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_wallet")
public class Wallet {

    @Id
    @SequenceGenerator(name = "wallet_seq", sequenceName = "wallet_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_seq")
    private Long id;

    private BigDecimal saldo;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Wallet(BigDecimal saldo, User user){
        this.saldo = saldo;
        this.user = user;
    }


}
