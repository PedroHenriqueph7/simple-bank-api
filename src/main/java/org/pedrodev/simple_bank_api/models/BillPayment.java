package org.pedrodev.simple_bank_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.pedrodev.simple_bank_api.models.enums.PaymentStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // <--- O "Ouvinte" que vigia a entidade
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_bill_payment")
public class BillPayment {

    @Id
    @SequenceGenerator(name = "bill_payment_seq", sequenceName = "bill_payment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_payment_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User payer;

    @Column(nullable = false, length = 49)
    private String identificationField;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private String description;

    private BigDecimal discount;
    private BigDecimal interest;
    private BigDecimal fine;
    private BigDecimal fee; //TAXA QUE O ASAAS COBRARA DA INSTITUICAO QUE PAGARA A CONTA= SIMPLE BANK

    @Column(nullable = false, unique = true)
    private String externalReference;

    @Column(nullable = false)
    private LocalDate originalDueDate;

    private String beneficiary;

    private String receiptUrl;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt; // horario que usuario pagou a conta

    @LastModifiedDate
    private LocalDateTime updatedAt; // Momento eu que o pagamento alterou de status

    public BillPayment(User payer, String identificationField, BigDecimal value, String description, BigDecimal discount, BigDecimal interest, BigDecimal fine, BigDecimal fee, String externalReference, String beneficiary, String receiptUrl) {
        this.payer = payer;
        this.identificationField = identificationField;
        this.value = value;
        this.description = description;
        this.discount = discount;
        this.interest = interest;
        this.fine = fine;
        this.fee = fee;
        this.externalReference = externalReference;
        this.beneficiary = beneficiary;
        this.receiptUrl = receiptUrl;

    }
}
