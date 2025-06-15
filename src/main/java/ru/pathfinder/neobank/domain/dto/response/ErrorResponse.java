package ru.pathfinder.neobank.domain.dto.response;

public record ErrorResponse(
        String errorTitle,
        String errorDetail
) {}
