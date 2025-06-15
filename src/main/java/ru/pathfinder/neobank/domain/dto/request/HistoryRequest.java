package ru.pathfinder.neobank.domain.dto.request;

public record HistoryRequest(
   String accountId,
   String operation,
   String fromDate,
   String toDate
) {}
