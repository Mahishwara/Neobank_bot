package ru.pathfinder.neobank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.config.ApplicationConfig;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.security.HashGenerator;
import ru.pathfinder.neobank.domain.User;
import ru.pathfinder.neobank.service.AuthenticationService;
import ru.pathfinder.neobank.service.NeobankService;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final NeobankService neobankService;

    private final ApplicationConfig applicationConfig;

    private final HashGenerator hashGenerator;

    @Autowired
    public AuthenticationServiceImpl(NeobankService neobankService,
                                     ApplicationConfig applicationConfig,
                                     HashGenerator hashGenerator) {
        this.neobankService = neobankService;
        this.applicationConfig = applicationConfig;
        this.hashGenerator = hashGenerator;
    }

    @Override
    public Authentication getAuthentication(Long chatId, User user) {
        Map<String, Object> params = buildAuthParams(chatId, user);
        String bearerToken = neobankService.getToken(params);
        return bearerToken == null ? null : new Authentication(bearerToken);
    }

    @Override
    public String getAuthenticationLink(Long chatId, User user) {
        Map<String, Object> params = buildAuthParams(chatId, user);
        String backendUrl = applicationConfig.getBackend().getUrl();
        return String.format("%s/auth?%s", backendUrl, formatParams(params));
    }

    private String formatParams(Map<String, Object> params) {
        return params.entrySet().stream()
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private Map<String, Object> buildAuthParams(Long chatId, User user) {
        Map<String, Object> params = new HashMap<>();
        ApplicationConfig.TelegramConfig telegramConfig = applicationConfig.getTelegram();
        params.put("bot_id", telegramConfig.getId());
        params.put("bot_username", telegramConfig.getUsername());
        params.put("chat_id", chatId);
        params.put("chat_type", telegramConfig.getChatType());
        params.put("first_name", user.getFirstName());
        params.put("last_name", user.getLastName());
        params.put("user_id", Long.toString(user.getId()));
        params.put("username", user.getUsername());
        params.put("hash", hashGenerator.generate(chatId, user));
        params.put("auth_date", Long.toString(System.currentTimeMillis()));
        return params;
    }

}
