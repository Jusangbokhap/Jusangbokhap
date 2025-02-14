package jsbh.Jusangbokhap.api.accommodation.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "jsbh.Jusangbokhap.api.accommodation")
public class AccommodationExceptionHandler {

    @ExceptionHandler(AccommodationCustomException.class)
    public ResponseEntity<String> handleAccommodationCustomException(AccommodationCustomException ex) {
        return new ResponseEntity<>(ex.getDescription(), ex.getErrorCode().getErrorCode());
    }
}
