package ru.pathfinder.neobank.domain.dto.request;

public record TransferRequest(
        String from,
        String to,
        long amount,
        String message) {
}
