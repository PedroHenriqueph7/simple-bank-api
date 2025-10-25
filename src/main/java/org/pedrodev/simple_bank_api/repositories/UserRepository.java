package org.pedrodev.simple_bank_api.repositories;

import org.pedrodev.simple_bank_api.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
