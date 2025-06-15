package ru.pathfinder.neobank.domain.dto;

import java.util.List;

public record HistoryResponse(
        int pageCount,
        int currentPage,
        int pageSize,
        List<Transfer> transfers
) {
    public record Transfer(
            String type,
            TransferDetails fromTransfer,
            TransferDetails toTransfer,
            String message,
            String datetime,
            String status,
            String declineReason
    ) {}

    public record TransferDetails(
            String accountNumber,
            int currencyNumber,
            long amount
    ) {}
}
