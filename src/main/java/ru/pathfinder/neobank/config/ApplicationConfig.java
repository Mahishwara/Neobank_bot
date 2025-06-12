package ru.pathfinder.neobank.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "app")
public class ApplicationConfig {

    private TelegramConfig telegram;

    private BackendConfig backend;

    @Getter
    @Setter
    public static class TelegramConfig {
        private String id;
        private String chatId;
        private String chatType;
        private String username;
        private String token;
    }

    @Getter
    @Setter
    public static class BackendConfig {
        private String url;
        private String tokenHash;
    }

}
