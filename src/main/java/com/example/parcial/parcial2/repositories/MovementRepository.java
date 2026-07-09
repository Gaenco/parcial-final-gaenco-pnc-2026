package com.example.parcial.parcial2.repositories;

import com.example.parcial.parcial2.domain.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MovementRepository extends JpaRepository<Movement, UUID> {

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Movement m " +
            "WHERE m.book.id = :bookId AND m.lector.id = :lectorId AND m.type = 'BORROWING' " +
            "AND NOT EXISTS (SELECT 1 FROM Movement m2 WHERE m2.book.id = :bookId " +
            "AND m2.lector.id = :lectorId AND m2.type = 'RETURN' AND m2.timestamp > m.timestamp)")
    boolean existsActiveBorrowing(@Param("bookId") UUID bookId, @Param("lectorId") UUID lectorId);
}
