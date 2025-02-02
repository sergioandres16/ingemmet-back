package org.saeta.ingemmetback.repositories;

import org.saeta.ingemmetback.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar por username o email
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Para login, se puede buscar por username o email indistintamente
}