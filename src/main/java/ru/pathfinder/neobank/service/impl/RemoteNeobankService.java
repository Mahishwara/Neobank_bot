package ru.pathfinder.neobank.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import ru.pathfinder.neobank.domain.dto.HistoryResponse;
import ru.pathfinder.neobank.domain.dto.request.TransferRequest;
import ru.pathfinder.neobank.domain.dto.request.OpenDepositRequest;
import ru.pathfinder.neobank.domain.Currency;
import ru.pathfinder.neobank.domain.dto.request.*;
import ru.pathfinder.neobank.domain.dto.response.*;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.exception.ServiceException;
import ru.pathfinder.neobank.provider.RemoteNeobankProvider;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemoteNeobankService implements NeobankService {

    private final RemoteNeobankProvider remoteNeobankProvider;

    @Override
    public String getToken(Map<String, Object> params) throws NeobankException {
        return doCall(() -> remoteNeobankProvider.getWebClient()
                .get()
                .uri(
                        uriBuilder -> {
                            UriBuilder builder = uriBuilder.path("/token");
                            fillRequestParams(builder, params);
                            return builder.build();
                        }
                )
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(String.class)
                .block());
    }

    @Override
    public Map<String, Currency> getCurrencies(Authentication authentication) {
        List<Currency> currencies = remoteNeobankProvider.getWebClient()
                .get()
                .uri("/currencies")
                .header("Authorization", "Bearer " + authentication.token())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response
                                .bodyToMono(ErrorResponse.class)
                                .flatMap(error -> Mono.error(new ServiceException(error))))
                .bodyToMono(new ParameterizedTypeReference<List<Currency>>() {})
                .block();
        return currencies == null
                ? Collections.emptyMap()
                : currencies.stream().collect(Collectors.toMap(Currency::currencyNumber, e -> e));
    }

    @Override
    public void transfer(TransferRequest request, Authentication authentication) throws NeobankException {
        doCall(() -> remoteNeobankProvider.getWebClient()
                .put()
                .uri("/transfers")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(Object.class)
                .block());
    }

    @Override
    public HistoryResponse getHistory(HistoryRequest request, Authentication authentication) throws NeobankException {
        return doCall(() -> remoteNeobankProvider.getWebClient()
                .post()
                .uri("/transfers/history")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(HistoryResponse.class)
                .block());
    }

    @Override
    public List<AccountResponse> getAccounts(Authentication authentication) throws NeobankException {
        return doCall(() -> remoteNeobankProvider.getWebClient()
                .get()
                .uri("/accounts")
                .header("Authorization", "Bearer " + authentication.token())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<List<AccountResponse>>() {})
                .block());
    }

    @Override
    public AccountResponse findAccount(String lastNumbers, Authentication authentication) throws NeobankException {
        int lastNumbersLength = lastNumbers.length();
        return getAccounts(authentication).stream()
                .filter(a -> a.accountNumber().substring(a.accountNumber().length() - lastNumbersLength).equals(lastNumbers))
                .findFirst().orElse(null);
    }

    @Override
    public AccountResponse openAccount(OpenAccountRequest request, Authentication authentication) throws NeobankException {
        return doCall(() -> remoteNeobankProvider.getWebClient()
                .post()
                .uri("/accounts/account")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(AccountResponse.class)
                .block());
    }

    @Override
    public AccountResponse closeAccount(CloseAccountRequest request, Authentication authentication) throws NeobankException {
        return doCall(() -> remoteNeobankProvider.getWebClient()
                .put()
                .uri("/accounts/close-account")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(AccountResponse.class)
                .block());
    }

    @Override
    public List<ProductCreditResponse> getCreditProducts(Authentication authentication) throws NeobankException {
        ProductInfoResponse productsResponse = doCall(() -> remoteNeobankProvider.getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("productType", "credit")
                        .build()
                )
                .header("Authorization", "Bearer " + authentication.token())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(ProductInfoResponse.class)
                .block());
        if (productsResponse == null || productsResponse.products() == null) {
            return Collections.emptyList();
        }
        return productsResponse.products().creditProducts();
    }

    @Override
    public CreditResponse openCredit(OpenCreditRequest request, Authentication authentication) throws NeobankException {
        return doCall(() -> remoteNeobankProvider.getWebClient()
                .post()
                .uri("/credit")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(CreditResponse.class)
                .block());
    }

    @Override
    public CreditResponse getCurrentCredit(Authentication authentication) throws NeobankException {
        ActiveCreditRequest request = new ActiveCreditRequest("ACTIVE");
        CurrentCreditResponse resp = doCall(() -> remoteNeobankProvider.getWebClient()
                .post()
                .uri("/credits")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(CurrentCreditResponse.class)
                .block());
        return resp == null || resp.credit() == null ? null : resp.credit().getFirst();
    }

    @Override
    public PaymentPlanResponse getPaymentPlan(String creditId, Authentication authentication) throws NeobankException {
        return doCall(() -> remoteNeobankProvider.getWebClient()
                .get()
                .uri(MessageFormat.format("/credit/{0}/paymentPlan", creditId))
                .header("Authorization", "Bearer " + authentication.token())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(PaymentPlanResponse.class)
                .block());
    }

    @Override
    public void repayment(EarlyRepaymentRequest request, Authentication authentication) throws NeobankException {
        doCall(() -> remoteNeobankProvider.getWebClient()
                .put()
                .uri("/credit/repayment")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(Object.class)
                .block());

    }
    @Override
    public List<ProductDepositResponse> getDepositProducts(Authentication authentication) throws NeobankException {
        ProductInfoResponse productsResponse = doCall(() -> remoteNeobankProvider.getWebClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products")
                        .queryParam("productType", "deposit")
                        .build()
                )
                .header("Authorization", "Bearer " + authentication.token())
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(ProductInfoResponse.class)
                .block());
        if (productsResponse == null || productsResponse.products() == null) {
            return Collections.emptyList();
        }
        return productsResponse.products().depositProducts();
    }

    @Override
    public void openDeposit(OpenDepositRequest request, Authentication authentication) throws NeobankException {
        doCall(() -> remoteNeobankProvider.getWebClient()
                .post()
                .uri("/deposit")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(Object.class)
                .block());
    }

    @Override
    public DepositResponse getCurrentDeposit(Authentication authentication) throws NeobankException {
        DepositsResponse res = doCall(() -> remoteNeobankProvider.getWebClient()
                .post()
                .uri("/deposits")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(Map.of("depositStatus", "ACTIVE"))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(DepositsResponse.class)
                .block());
        if (res == null || res.deposit() == null || res.deposit().isEmpty()) {
            return null;
        }
        return res.deposit().getFirst();
    }

    @Override
    public void closeDeposit(CloseDepositRequest request, Authentication authentication) throws NeobankException {
        doCall(() -> remoteNeobankProvider.getWebClient()
                .put()
                .uri("/deposit/close-deposit")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .flatMap(body -> {
                                    try {
                                        ErrorResponse error = new ObjectMapper().readValue(body, ErrorResponse.class);
                                        return Mono.error(new ServiceException(error));
                                    } catch (Exception e1) {
                                        try {
                                            ErrorResponse2 error2 = new ObjectMapper().readValue(body, ErrorResponse2.class);
                                            ErrorResponse error = new ErrorResponse(error2.errorTitle(), String.join("\n", error2.errorDetails()));
                                            return Mono.error(new ServiceException(error));
                                        } catch (JsonProcessingException e) {
                                            return Mono.error(new RuntimeException(e));
                                        }
                                    }
                                })
                )
                .bodyToMono(Object.class)
                .block());
    }

    private <T> T doCall(Supplier<T> remoteAction) throws NeobankException {
        try {
            return remoteAction.get();
        } catch (ServiceException e) {
            throw new NeobankException(e.getErrorResponse());
        } catch (WebClientException e) {
            return null;
        }
    }

    private void fillRequestParams(UriBuilder uriBuilder, Map<String, Object> params) {
        params.forEach((key, value) -> uriBuilder.queryParam(key, String.valueOf(value)));
    }

}
