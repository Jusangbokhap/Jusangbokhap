package jsbh.Jusangbokhap.domain.reservation;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("확인중"),
    CONFIRMED("확정됨"),
    CANCELED("취소됨");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}
