package org.pedrodev.simple_bank_api.repositories;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.pedrodev.simple_bank_api.models.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(Long userId);

    // Bloqueio a linha pois tenho intensao de alter-lo algum registro no bd da wallet então ninguem pode ler/editar ou alterar ate eu terminar a transacao
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Defina o comportamento caso esta linha esteja bloqueada, e "value = 0" significa não espere(caso esteja Bloqueada), lance um erro imediato
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0"))
    Optional<Wallet> findWalletForUpdateByUserId(Long userId);
}
