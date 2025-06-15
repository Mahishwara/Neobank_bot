package ru.pathfinder.neobank.domain.dto.response;

public record ErrorResponse2(
        String errorTitle,
        String[] errorDetails
) {}
