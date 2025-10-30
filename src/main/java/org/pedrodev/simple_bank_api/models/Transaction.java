package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_transaction")
public class Transaction {

    @Id
    @SequenceGenerator(name = "transaction_seq", sequenceName = "transaction_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_seq")
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;


    @ManyToOne
    @JoinColumn(name = "pagador_id", nullable = false)
    private User pagador;

    @ManyToOne
    @JoinColumn(name = "recebedor_id", nullable = false)
    private User recebedor;

    @Column(nullable = false)
    private LocalDateTime dataeHora;
}
