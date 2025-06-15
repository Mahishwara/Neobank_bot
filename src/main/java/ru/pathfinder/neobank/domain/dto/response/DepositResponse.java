package ru.pathfinder.neobank.domain.dto.response;

public record DepositResponse(
        String id,
        String depositNumber,
        long startAmount,
        String startDepositDate,
        String endDepositDate,
        String planEndDate,
        String depositName,
        long depositRate,
        int period,
        boolean prolongation,
        String depositStatus,
        int currencyNumber,
        int openBranchId,
        int closeBranchId
) {}
