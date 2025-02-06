package jsbh.Jusangbokhap.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // User
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다.");

    private final HttpStatus status;
    private final String message;

}
