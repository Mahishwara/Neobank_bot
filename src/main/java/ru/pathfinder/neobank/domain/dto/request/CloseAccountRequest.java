package ru.pathfinder.neobank.domain.dto.request;

public record CloseAccountRequest(
        String accountId,
        int currencyNumber
) {}
