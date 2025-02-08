package jsbh.Jusangbokhap.api.accommodation.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AccommodationErrorCode {

    NOT_FOUND_ACCOMMODATION(HttpStatus.NOT_FOUND, "존재하지 않은 숙소 입니다."),
    INVALID_ACCOMMODATION_PRICE(HttpStatus.BAD_REQUEST, "숙박 가격은 1박당 가격 범위 내에 있어야 합니다."),
    INVALID_GUEST_COUNT(HttpStatus.BAD_REQUEST, "1~100명 사이의 인원만 가능합니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "잘못된 숙소 카테고리입니다.");

    private final HttpStatus errorCode;
    private final String errorMessage;
}
