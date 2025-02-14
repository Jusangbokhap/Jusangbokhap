package jsbh.Jusangbokhap.api.availableDate.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AvailableDateExceptionHandler {

    @ExceptionHandler(AvailableDateCustomException.class)
    public ResponseEntity<String> handleAvailableCustomException(AvailableDateCustomException ex) {
        return new ResponseEntity<>(ex.getDescription(), ex.getErrorCode().getErrorCode());
    }
}
