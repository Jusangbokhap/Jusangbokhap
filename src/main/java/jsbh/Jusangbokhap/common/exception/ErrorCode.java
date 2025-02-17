package jsbh.Jusangbokhap.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // User
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // Search
    NOT_EXIST_KEYWORD(HttpStatus.BAD_REQUEST, "상호명, 시도, 시군구, 읍면동 중 한 가지는 반드시 입력해야 합니다.");

    private final HttpStatus status;
    private final String message;

}
