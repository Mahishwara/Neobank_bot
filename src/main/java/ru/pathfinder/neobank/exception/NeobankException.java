package ru.pathfinder.neobank.exception;

import lombok.Getter;
import ru.pathfinder.neobank.domain.dto.response.ErrorResponse;

/**
 * Внутренняя ошибка Neobank сервиса
 */
@Getter
public class NeobankException extends Exception {

    private final ErrorResponse errorResponse;

    public NeobankException(ErrorResponse errorResponse) {
        super(errorResponse.errorTitle());
        this.errorResponse = errorResponse;
    }

}