package ru.pathfinder.neobank.domain.dto.response;

import java.util.List;

public record ProductsResponse(
   List<ProductCreditResponse> creditProducts,
   List<ProductDepositResponse> depositProducts
) {}
