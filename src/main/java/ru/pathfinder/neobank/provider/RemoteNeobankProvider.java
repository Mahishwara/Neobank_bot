package ru.pathfinder.neobank.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.pathfinder.neobank.config.ApplicationConfig;

@Component
@RequiredArgsConstructor
public class RemoteNeobankProvider implements WebClientProvider {

    private final ApplicationConfig applicationConfig;

    @Override
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(applicationConfig.getBackend().getUrl())
                .build();
    }

}
