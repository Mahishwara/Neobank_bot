package ru.pathfinder.neobank.domain.dto.response;

public record ProductDepositResponse(
        String id,
        String name,
        String depositProductStatus,
        int branchId,
        int currencyNumber
) {}
