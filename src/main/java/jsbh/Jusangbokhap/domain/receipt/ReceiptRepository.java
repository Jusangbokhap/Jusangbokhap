package jsbh.Jusangbokhap.domain.receipt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    @Query("SELECT r FROM Receipt r WHERE r.accommodation.accommodationId = :accommodationId " +
            "AND r.givenAt BETWEEN :startDate AND :endDate " +
            "AND r.receiptStatus IN (:statuses)")
    List<Receipt> findAllByAccommodationAndPeriod(
            @Param("accommodationId") Long accommodationId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<ReceiptStatus> statuses);

    @Query("SELECT DISTINCT r.receiptStatus FROM Receipt r WHERE r.accommodation.accommodationId = :accommodationId")
    List<ReceiptStatus> findDistinctReceiptStatusByAccommodation(@Param("accommodationId") Long accommodationId);
}