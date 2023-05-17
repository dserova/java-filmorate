package ru.yandex.practicum.filorate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RuntimeException {
    Object object;
    HttpStatus httpStatus;

    public ValidationException(String message, Object object, HttpStatus httpStatus) {
        super(message);
        this.object = object;
        this.httpStatus = httpStatus;
    }

}
