package ru.pathfinder.neobank.exception;

import lombok.Getter;
import ru.pathfinder.neobank.domain.dto.response.ErrorResponse;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public ServiceException(ErrorResponse errorResponse) {
        super(errorResponse.errorTitle());
        this.errorResponse = errorResponse;
    }

}
