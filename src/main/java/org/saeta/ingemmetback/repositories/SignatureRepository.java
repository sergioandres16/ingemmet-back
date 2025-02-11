package org.saeta.ingemmetback.repositories;

import org.saeta.ingemmetback.entities.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Integer> {
}