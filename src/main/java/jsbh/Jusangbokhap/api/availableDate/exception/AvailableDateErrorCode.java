package jsbh.Jusangbokhap.api.availableDate.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AvailableDateErrorCode {
    NOT_FOUND_AVAILABLE(HttpStatus.NOT_FOUND, "해당 날짜를 찾을 수 없습니다."),
    DATE_OVERLAP(HttpStatus.BAD_REQUEST, "이미 예약된 날짜 범위가 존재 합니다"),
    MISSING_DATE(HttpStatus.BAD_REQUEST, "날짜 값은 필수입니다."),
    SAME_START_END(HttpStatus.BAD_REQUEST, "시작일과 종료일은 같을 수 없습니다."),
    START_AFTER_END(HttpStatus.BAD_REQUEST, "시작 날짜는 종료 날짜보다 이후일 수 없습니다.");

    private final HttpStatus errorCode;
    private final String errorMessage;
}
