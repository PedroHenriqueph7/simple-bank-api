package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_transaction")
public class Transaction {

    @EqualsAndHashCode.Include
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
    private ZonedDateTime dataeHora;

    public Transaction(BigDecimal valor, User pagador, User recebedor, ZonedDateTime dataeHora) {
        this.valor = valor;
        this.pagador = pagador;
        this.recebedor = recebedor;
        this.dataeHora = dataeHora;
    }
}
