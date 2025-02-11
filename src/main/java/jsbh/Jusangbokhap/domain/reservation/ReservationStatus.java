package jsbh.Jusangbokhap.domain.reservation;

import lombok.Getter;

@Getter
public enum ReservationStatus {
	PENDING("예약 확인 중"),
	CONFIRMED("예약 확정"),
	CANCELED("예약 취소");

	private final String description;

	ReservationStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}