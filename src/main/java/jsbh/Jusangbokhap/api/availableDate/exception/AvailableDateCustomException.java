package jsbh.Jusangbokhap.api.availableDate.exception;

import lombok.Getter;

@Getter
public class AvailableDateCustomException extends RuntimeException{
    private AvailableDateErrorCode errorCode;
    private String description;

    public AvailableDateCustomException(AvailableDateErrorCode availableDateErrorCode) {
        super(availableDateErrorCode.getErrorMessage());
        this.errorCode = availableDateErrorCode;
        this.description = availableDateErrorCode.getErrorMessage();
    }

    public AvailableDateCustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvailableDateCustomException(Throwable cause) {
        super(cause);
    }

    protected AvailableDateCustomException(String message, Throwable cause, boolean enableSuppression,
                                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
