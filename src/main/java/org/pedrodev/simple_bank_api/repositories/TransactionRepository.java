package org.pedrodev.simple_bank_api.repositories;

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

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transaction t WHERE t.pagador.id = :pagadorId AND t.dataeHora >= :datalimite")
    BigDecimal findValueTransactionsByPagador_id24Hours(@Param("pagadorId") Long pagadorId, @Param("datalimite") ZonedDateTime data);

    // Teste A: Remove a data (Verifica se o problema é o ID)
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transaction t WHERE t.pagador.id = :pagadorId")
    BigDecimal somaTotalSemData(@Param("pagadorId") Long pagadorId);

    // Teste B: Remove o usuário (Verifica se o problema é a Data)
// CUIDADO: Isso soma TUDO do banco nas ultimas 24h. Use apenas em ambiente de dev.
    @Query("SELECT COALESCE(SUM(t.valor), 0) FROM Transaction t WHERE t.dataeHora >= :datalimite")
    BigDecimal somaTotalSemUsuario(@Param("datalimite") ZonedDateTime data);
}
