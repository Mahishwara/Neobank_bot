package ru.pathfinder.neobank.domain.dto;

public record CloseAccountRequest(
        String accountId,
        int currencyNumber
) {}
