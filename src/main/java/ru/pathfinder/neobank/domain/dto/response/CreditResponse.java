package ru.pathfinder.neobank.domain.dto.response;

public record CreditResponse(
        String id,
        String creditNumber,
        double amount,
        int period,
        String status,
        String startDate,
        int creditProductId,
        long rate,
        int currencyNumber,
        String message
) {}
