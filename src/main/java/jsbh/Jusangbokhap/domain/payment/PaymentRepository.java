package jsbh.Jusangbokhap.domain.payment;

import jsbh.Jusangbokhap.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByTid(String tid);

    Optional<Payment> findByTid(String tid);  // ✅ findByTid() 추가
    Optional<Payment> findByReservation_ReservationId(Long reservationId);
}
