package ru.pathfinder.neobank.provider;

import org.springframework.web.reactive.function.client.WebClient;

public interface WebClientProvider {

    WebClient getWebClient();

}
