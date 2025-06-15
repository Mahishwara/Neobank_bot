package ru.pathfinder.neobank.domain.dto.request;

public record OpenCreditRequest(
   long amount,
   long rate,
   int period,
   int currencyNumber,
   long creditProductId,
   String accountId
) {}
