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

    // TODO: Reativar locks e hints quando migrar para PostgreSQL (H2 não suporta NOWAIT corretamente)
    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0"))
    Optional<Wallet> findWalletForUpdateByUserId(Long userId);
}
