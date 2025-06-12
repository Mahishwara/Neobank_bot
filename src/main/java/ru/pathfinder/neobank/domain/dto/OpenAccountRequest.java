package ru.pathfinder.neobank.domain.dto;

public record OpenAccountRequest(
        long amount,
        int currencyNumber
) {}
