package ru.yandex.practicum.filorate.exception;

import lombok.Getter;

@Getter
public class DataNotFound extends RuntimeException {

    Object object;

    public DataNotFound(String message, Object object) {
        super(message);
        this.object = object;
    }
}
