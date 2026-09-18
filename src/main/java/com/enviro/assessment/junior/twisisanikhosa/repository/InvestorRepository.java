package com.enviro.assessment.junior.twisisanikhosa.repository;

import com.enviro.assessment.junior.twisisanikhosa.entity.Investor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface InvestorRepository extends JpaRepository<Investor, Long> {
    @Query("SELECT i FROM Investor i LEFT JOIN FETCH i.products WHERE i.id = :id")
    Optional<Investor> findByIdWithProducts(@Param("id") Long id);
}