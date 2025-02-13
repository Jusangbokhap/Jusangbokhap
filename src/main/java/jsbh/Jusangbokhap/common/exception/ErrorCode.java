package jsbh.Jusangbokhap.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 사용자 관련 오류
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // 예약 관련 오류
    NOT_FOUND_RESERVATION(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),
    RESERVATION_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 예약입니다."),

    // 결제 관련 오류
    PAYMENT_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "카카오페이 결제 요청 중 오류가 발생했습니다. 다시 시도해 주세요."),
    KAKAO_PAY_INVALID_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "카카오페이 API 응답이 올바르지 않습니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "해당 예약의 결제 내역이 없습니다."),
    KAKAO_PAY_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오페이 API 호출 중 오류가 발생했습니다."),
    PAYMENT_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 금액입니다."),
    TID_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다. TID 또는 주문번호를 확인하세요.");


    private final HttpStatus status;
    private final String message;
}
