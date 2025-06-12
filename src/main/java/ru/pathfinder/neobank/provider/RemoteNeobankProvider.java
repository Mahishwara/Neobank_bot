package ru.pathfinder.neobank.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.pathfinder.neobank.config.ApplicationConfig;

@Component
public class RemoteNeobankProvider implements WebClientProvider {

    private final ApplicationConfig applicationConfig;

    @Autowired
    public RemoteNeobankProvider(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Override
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl(applicationConfig.getBackend().getUrl())
                .build();
    }

}
