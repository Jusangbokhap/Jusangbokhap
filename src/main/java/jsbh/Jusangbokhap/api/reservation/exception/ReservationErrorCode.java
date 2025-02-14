package jsbh.Jusangbokhap.api.reservation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode {

    NOT_FOUND_RESERVATION_DATE(HttpStatus.NOT_FOUND, "예약할 수 있는 날짜가 없습니다."),
    ALREADY_BOOKED_DATE(HttpStatus.NOT_FOUND, "이미 예약된 숙소 입니다.");

    private final HttpStatus errorCode;
    private final String errorMessage;
}
