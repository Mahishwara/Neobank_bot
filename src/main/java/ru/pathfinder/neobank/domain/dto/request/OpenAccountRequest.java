package ru.pathfinder.neobank.domain.dto.request;

public record OpenAccountRequest(
        int currencyNumber,
        long amount
) {}
