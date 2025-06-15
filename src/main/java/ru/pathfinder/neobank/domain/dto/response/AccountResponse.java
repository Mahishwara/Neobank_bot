package ru.pathfinder.neobank.domain.dto.response;

import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        UUID clientId,
        long amount,
        long availableAmount,
        String startDate,
        String accountType,
        String accountStatus,
        int currencyNumber,
        int openBranchId,
        int book
) {}
