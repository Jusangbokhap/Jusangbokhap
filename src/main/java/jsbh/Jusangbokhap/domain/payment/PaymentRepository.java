package jsbh.Jusangbokhap.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservation_ReservationId(Long reservationId);
    boolean existsByTid(String tid);
}
