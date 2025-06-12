package ru.pathfinder.neobank.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String accountNumber,
        UUID clientId,
        long amount,
        long availableAmount,
        LocalDate startDate,
        String accountType,
        String accountStatus,
        int currencyNumber,
        int openBranchId,
        int book
) {}
