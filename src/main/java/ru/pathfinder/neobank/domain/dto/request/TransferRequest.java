package ru.pathfinder.neobank.domain.dto.request;

public record TransferRequest(
        String fromAccountId,
        String toAccountId,
        long amount,
        String message) {
}
