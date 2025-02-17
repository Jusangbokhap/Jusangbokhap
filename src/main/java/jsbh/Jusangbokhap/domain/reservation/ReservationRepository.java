package jsbh.Jusangbokhap.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByGuest_UserIdAndReservationStatus(Long userId, ReservationStatus status);
}
