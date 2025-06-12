package ru.pathfinder.neobank.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriBuilder;
import ru.pathfinder.neobank.domain.Currency;
import ru.pathfinder.neobank.domain.dto.CloseAccountRequest;
import ru.pathfinder.neobank.domain.dto.OpenAccountRequest;
import ru.pathfinder.neobank.domain.dto.AccountResponse;
import ru.pathfinder.neobank.provider.RemoteNeobankProvider;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.NeobankService;

import java.util.List;
import java.util.Map;

@Service
public class RemoteNeobankService implements NeobankService {

    private final RemoteNeobankProvider remoteNeobankProvider;

    @Autowired
    public RemoteNeobankService(RemoteNeobankProvider remoteNeobankProvider) {
        this.remoteNeobankProvider = remoteNeobankProvider;
    }

    @Override
    public String getToken(Map<String, Object> params) {
        try {
            return remoteNeobankProvider.getWebClient()
                    .get()
                    .uri(
                            uriBuilder -> {
                                UriBuilder builder = uriBuilder.path("/token");
                                fillRequestParams(builder, params);
                                return builder.build();
                            }
                    )
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientException e) {
            return null;
        }
    }

    public List<Currency> getCurrencies(Authentication authentication) {
        return remoteNeobankProvider.getWebClient()
                .get()
                .uri("/currencies")
                .header("Authorization", "Bearer " + authentication.token())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Currency>>() {})
                .block();
    }

    @Override
    public List<AccountResponse> getAccounts(Authentication authentication) {
        return remoteNeobankProvider.getWebClient()
                .get()
                .uri("/accounts")
                .header("Authorization", "Bearer " + authentication.token())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountResponse>>() {})
                .block();
    }

    public AccountResponse openAccount(OpenAccountRequest request, Authentication authentication) {
        return remoteNeobankProvider.getWebClient()
                .post()
                .uri("/accounts/account")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .block();
    }

    public AccountResponse closeAccount(CloseAccountRequest request, Authentication authentication) {
        return remoteNeobankProvider.getWebClient()
                .put()
                .uri("/accounts/close-account")
                .header("Authorization", "Bearer " + authentication.token())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AccountResponse.class)
                .block();
    }

    private void fillRequestParams(UriBuilder uriBuilder, Map<String, Object> params) {
        params.forEach((key, value) -> uriBuilder.queryParam(key, String.valueOf(value)));
    }

}
