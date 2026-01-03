package org.pedrodev.simple_bank_api.repositories;

import org.pedrodev.simple_bank_api.models.BillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {

    Optional<BillPayment> findByExternalReference(String externalReference);

}
