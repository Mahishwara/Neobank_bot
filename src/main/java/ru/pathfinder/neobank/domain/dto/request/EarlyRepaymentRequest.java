package ru.pathfinder.neobank.domain.dto.request;

public record EarlyRepaymentRequest(
   String creditId,
   int actionAmount,
   int currencyNumber
) {}
