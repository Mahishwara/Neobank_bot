package ru.pathfinder.neobank.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final NeobankService neobankService;

    private final ApplicationConfig applicationConfig;

    private final HashGenerator hashGenerator;

    @Override
    public Authentication getAuthentication(Long chatId, User user) {
        Map<String, Object> params = buildAuthParams(chatId, user);
        String bearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsMERJdG0wV19FVXpEcEpIWFBaWnFObnNST2lPUkpLcmdGYnIxZ3JYdUFrIn0.eyJleHAiOjE3NDk5OTU2ODUsImlhdCI6MTc0OTk4MTI4NSwiYXV0aF90aW1lIjoxNzQ5OTgxMjg1LCJqdGkiOiIxYjVlZmY3OS1lODc0LTQ4NGQtOWJiZi0yZjQyODExNzQxZWEiLCJpc3MiOiJodHRwczovL2lkLW5lb2JhbmsubmVvZmxleC5ydS9hdXRoL3JlYWxtcy9tc2FiYW5rIiwiYXVkIjoibXNhYmFuayIsInN1YiI6ImM4NDQxYmI1LTc5ZjgtNDZlMy05ZGI1LWQ2YWY4Yzk2MThhOCIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1zYWJhbmstc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiI0NDg3NzQ5YS1hY2EzLTRlNmEtYjI1Yy1mNWE5ZmMwYzY5ODciLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVzb3VyY2VfYWNjZXNzIjp7Im1zYWJhbmsiOnsicm9sZXMiOlsiY2xpZW50Il19fSwic2NvcGUiOiJvcGVuaWQgQlJBTkNIIHByb2ZpbGUgZW1haWwiLCJzaWQiOiI0NDg3NzQ5YS1hY2EzLTRlNmEtYjI1Yy1mNWE5ZmMwYzY5ODciLCJCUkFOQ0giOiIxIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJOaWtpdGEgTW9yb3pvdiIsInByZWZlcnJlZF91c2VybmFtZSI6Im1vcm96b3ZubSIsImdpdmVuX25hbWUiOiJOaWtpdGEiLCJmYW1pbHlfbmFtZSI6Ik1vcm96b3YiLCJlbWFpbCI6Im1vcm96b3ZubUBuZW9iYW5rLmNvbSJ9.nxnl1DQaTTBVjzRSh8D45SGykhfBmFKG-PXnI9YwYfqO9B6MOGX2jDFW5rChOtrpLkEBvQM9NTuQ5TvheRb0ioihTd-6nWZO36P2rJTfnaYo6JL8OFFGXLEnWhM0y9vELnOD0wCWchSiwrbWmo606pfgMVvU-gQ9bY_NGBYuRrU12m6ccfhvmTWMZTliQifHeG6ZcB-R19HVr7QTJoA-jy57WFBV385ryAHyk7gpE0ntACkZnU8OPZufbQmhemilt9TuVf3ohNtZkJxuL9lbMQ4MNXa-Oqz4lW8_-a_Xxj_EQQvJoQHAB0FAtNpG8ODLrJynKNcXfTJKVPfRKHj8Og";
//        String bearerToken = neobankService.getToken(params);
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
