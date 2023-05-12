package ru.yandex.practicum.filorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> catchValidationException(ValidationException e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(e.getObject(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> catchResourceNotFoundException(DataNotFound e) {
        log.info(e.getMessage());
        return new ResponseEntity<>(e.getObject(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
