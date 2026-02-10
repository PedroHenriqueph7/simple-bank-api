package org.pedrodev.simple_bank_api.repositories;

import org.pedrodev.simple_bank_api.dtos.ExtratoBancarioDTO;
import org.pedrodev.simple_bank_api.models.Transaction;
import org.pedrodev.simple_bank_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    @Query(value = "SELECT * FROM tb_transaction\n" +
            "ORDER BY id DESC\n" +
            "LIMIT 1; ", nativeQuery = true)
    Transaction findLastTransaction();

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transaction t WHERE t.pagador.id = :pagadorId AND t.dataeHora >= :datalimite")
    BigDecimal findValueTransactionsByPagador_id24Hours(@Param("pagadorId") Long pagadorId, @Param("datalimite") ZonedDateTime data);

    @Query(value = """ 
        SELECT * FROM (
            SELECT\s
                d.valor as valor,\s
                'Depósito' as descricao,\s
                d.data_hora as data_movimentacao, 
                'DEPOSIT' as tipo
            FROM tb_deposit d
            WHERE d.user_id = :userId
        
            UNION ALL
        
            SELECT\s
                -t.valor,\s
                'Transferência',\s
                t.datae_hora,
                'TRANSACTION'
            FROM tb_transaction t
            WHERE t.pagador_id = :userId
        
            UNION ALL
        
            SELECT\s
                -bp.value,\s
                bp.description,\s
                bp.created_at,\s
                'BILL_PAYMENT'
            FROM tb_bill_payment bp
            WHERE bp.user_id = :userId AND bp.payment_status = 'PAID'
        ) as extrato
        ORDER BY data_movimentacao DESC;
    """, nativeQuery = true)
    List<ExtratoBancarioDTO> buscarResumoExtrato(@Param("userId") Long userId);
}
