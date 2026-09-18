package com.enviro.assessment.junior.twisisanikhosa.repository;

import com.enviro.assessment.junior.twisisanikhosa.entity.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {

    @Query("SELECT w FROM WithdrawalNotice w JOIN FETCH w.product p " +
           "WHERE (:productId IS NULL OR p.id = :productId) " +
           "AND (:startDate IS NULL OR w.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR w.createdAt <= :endDate) " +
           "ORDER BY w.createdAt DESC")
    List<WithdrawalNotice> findFilteredNotices(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}