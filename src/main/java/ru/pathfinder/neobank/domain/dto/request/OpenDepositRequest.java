package ru.pathfinder.neobank.domain.dto.request;

public record OpenDepositRequest(
        String accountId,
        int startAmount,
        double depositRate,
        String depositProductId,
        int currencyNumber,
        int period,
        boolean autoProlongation) {
}
