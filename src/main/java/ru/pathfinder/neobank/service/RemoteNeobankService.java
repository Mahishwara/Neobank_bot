package ru.pathfinder.neobank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriBuilder;
import ru.pathfinder.neobank.provider.RemoteNeobankProvider;

import java.util.Map;

@Service
public class RemoteNeobankService {

    private final RemoteNeobankProvider remoteNeobankProvider;

    @Autowired
    public RemoteNeobankService(RemoteNeobankProvider remoteNeobankProvider) {
        this.remoteNeobankProvider = remoteNeobankProvider;
    }

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

    private void fillRequestParams(UriBuilder uriBuilder, Map<String, Object> params) {
        params.forEach((key, value) -> uriBuilder.queryParam(key, String.valueOf(value)));
    }

}
