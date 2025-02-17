package jsbh.Jusangbokhap.domain.reservation;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("예약 확인중"),
    CONFIRMED("예약 확정됨"),
    CANCELED("예약 취소됨");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
