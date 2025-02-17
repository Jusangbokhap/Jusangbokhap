package jsbh.Jusangbokhap.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final int status;
    private final String message;
    private final String errorDetails;

    public ErrorResponse(ErrorCode errorCode, String errorDetails) {
        this.status = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
        this.errorDetails = errorDetails;
    }
}
