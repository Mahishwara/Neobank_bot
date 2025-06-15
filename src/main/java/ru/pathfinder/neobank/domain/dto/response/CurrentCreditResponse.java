package ru.pathfinder.neobank.domain.dto.response;

import java.util.List;

public record CurrentCreditResponse(
   List<CreditResponse> credit
) {}
