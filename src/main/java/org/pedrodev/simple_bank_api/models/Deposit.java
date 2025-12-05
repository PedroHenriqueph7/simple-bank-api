package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.*;
import org.pedrodev.simple_bank_api.models.enums.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_deposit")
public class Deposit {

    @EqualsAndHashCode.Include
    @Id
    @SequenceGenerator(name = "deposit_seq", sequenceName = "deposit_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "deposit_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status statusAtual;

    @Column(nullable = false, unique = true)
    private String pixId;

    @Column(nullable = false)
    private ZonedDateTime dataExpiracao;

    public Deposit(User user, BigDecimal valor, Status statusAtual, String pixId, ZonedDateTime dataExpiracao) {
        this.user = user;
        this.valor = valor;
        this.statusAtual = statusAtual;
        this.pixId = pixId;
        this.dataExpiracao = dataExpiracao;
    }
}
