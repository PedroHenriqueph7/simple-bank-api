package org.pedrodev.simple_bank_api.repositories;

import org.pedrodev.simple_bank_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.pedrodev.simple_bank_api.models.Deposit;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {

    Optional<Deposit> findDepositByPixId(String pixCode);

}
