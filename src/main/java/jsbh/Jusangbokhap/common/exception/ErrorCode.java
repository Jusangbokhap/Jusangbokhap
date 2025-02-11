package jsbh.Jusangbokhap.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // 사용자 관련 오류
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // 결제 관련 오류
    PAYMENT_REQUEST_FAILED(HttpStatus.BAD_REQUEST, "카카오페이 결제 요청 중 오류가 발생했습니다."),
    KAKAO_PAY_INVALID_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "카카오페이 API 응답이 올바르지 않습니다."),
    REDIS_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "카카오페이 결제 정보를 찾을 수 없습니다."),

    // 추가된 오류 코드
    PAYMENT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "이미 결제가 완료된 주문입니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
