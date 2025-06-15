package ru.pathfinder.neobank.domain.dto.response;

public record ProductCreditResponse(
    long id,
    String name,
    long branchId,
    String status,
    int currencyNumber
) {}
