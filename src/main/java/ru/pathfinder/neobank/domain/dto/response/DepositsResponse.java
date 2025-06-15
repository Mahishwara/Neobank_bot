package ru.pathfinder.neobank.domain.dto.response;

import java.util.List;

public record DepositsResponse(
   List<DepositResponse> deposit
) {}
