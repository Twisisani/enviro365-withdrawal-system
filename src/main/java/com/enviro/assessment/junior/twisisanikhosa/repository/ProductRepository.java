package com.enviro.assessment.junior.twisisanikhosa.repository;

import com.enviro.assessment.junior.twisisanikhosa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p JOIN FETCH p.investor WHERE p.id = :id")
    Optional<Product> findByIdWithInvestor(@Param("id") Long id);
}