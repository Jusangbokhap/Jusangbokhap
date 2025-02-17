package jsbh.Jusangbokhap.api.reservation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(ReservationCustomException.class)
    public ResponseEntity<String> handleReservationCustomException(ReservationCustomException ex) {
        return new ResponseEntity<>(ex.getDescription(), ex.getErrorCode().getErrorCode());
    }
}
