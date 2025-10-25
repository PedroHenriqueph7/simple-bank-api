package org.pedrodev.simple_bank_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.pedrodev.simple_bank_api.models.Deposit;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {
}
