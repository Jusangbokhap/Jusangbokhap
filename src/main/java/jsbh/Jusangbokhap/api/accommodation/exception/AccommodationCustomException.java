package jsbh.Jusangbokhap.api.accommodation.exception;

import lombok.Getter;

@Getter
public class AccommodationCustomException extends RuntimeException {
    private AccommodationErrorCode errorCode;
    private String description;

    public AccommodationCustomException(AccommodationErrorCode accommodationErrorCode) {
        super(accommodationErrorCode.getErrorMessage());
        this.errorCode = accommodationErrorCode;
        this.description = accommodationErrorCode.getErrorMessage();
    }

    public AccommodationCustomException(AccommodationErrorCode accommodationErrorCode, Throwable cause) {
        super(accommodationErrorCode.getErrorMessage(), cause);
        this.errorCode = accommodationErrorCode;
        this.description = accommodationErrorCode.getErrorMessage();
    }


    public AccommodationCustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccommodationCustomException(Throwable cause) {
        super(cause);
    }

    protected AccommodationCustomException(String message, Throwable cause, boolean enableSuppression,
                                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
