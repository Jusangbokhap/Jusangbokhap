package jsbh.Jusangbokhap.domain.reservation;

import jakarta.persistence.*;
import java.time.LocalDate;

import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import jsbh.Jusangbokhap.domain.BaseEntity;
import jsbh.Jusangbokhap.domain.accommodation.Accommodation;
import jsbh.Jusangbokhap.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private Integer guestCount;

    @Column(nullable = true, length = 255)
    private String cancelReason;

    public void confirmReservation() {
        this.reservationStatus = ReservationStatus.CONFIRMED;
    }

    public void cancelReservation(String reason) {
        if (this.reservationStatus == ReservationStatus.CANCELED) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_CANCELED);
        }
        this.reservationStatus = ReservationStatus.CANCELED;
        this.cancelReason = reason;
    }
}
