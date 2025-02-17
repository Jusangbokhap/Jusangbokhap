package jsbh.Jusangbokhap.api.reservation.exception;

import lombok.Getter;

@Getter
public class ReservationCustomException extends RuntimeException {
    private ReservationErrorCode errorCode;
    private String description;

    public ReservationCustomException(
            ReservationErrorCode reservationErrorCode) {
        super(reservationErrorCode.getErrorMessage());
        this.errorCode = reservationErrorCode;
        this.description = reservationErrorCode.getErrorMessage();
    }

    public ReservationCustomException(ReservationErrorCode reservationErrorCode, Throwable cause) {
        super(reservationErrorCode.getErrorMessage(), cause);
        this.errorCode = reservationErrorCode;
        this.description = reservationErrorCode.getErrorMessage();
    }


    public ReservationCustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReservationCustomException(Throwable cause) {
        super(cause);
    }

    protected ReservationCustomException(String message, Throwable cause, boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
