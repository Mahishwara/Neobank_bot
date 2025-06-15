package ru.pathfinder.neobank.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.config.ApplicationConfig;
import ru.pathfinder.neobank.exception.NeobankException;
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
    public Authentication getAuthentication(Long chatId, User user) throws NeobankException {
        Map<String, Object> params = buildAuthParams(chatId, user);
        String bearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsMERJdG0wV19FVXpEcEpIWFBaWnFObnNST2lPUkpLcmdGYnIxZ3JYdUFrIn0.eyJleHAiOjE3NTAwMTM3NzIsImlhdCI6MTc0OTk5OTM3MiwiYXV0aF90aW1lIjoxNzQ5OTk5MzcyLCJqdGkiOiI1NGEzMjg5Ni01ZGI5LTQ2NGMtOTUxNS1kMzc0NDc4NDA1M2YiLCJpc3MiOiJodHRwczovL2lkLW5lb2JhbmsubmVvZmxleC5ydS9hdXRoL3JlYWxtcy9tc2FiYW5rIiwiYXVkIjoibXNhYmFuayIsInN1YiI6ImM4NDQxYmI1LTc5ZjgtNDZlMy05ZGI1LWQ2YWY4Yzk2MThhOCIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1zYWJhbmstc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiJkNTM2OTJiZi1iY2MwLTQ5MmItODI5Mi01NThmZTJjNDIwY2QiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVzb3VyY2VfYWNjZXNzIjp7Im1zYWJhbmsiOnsicm9sZXMiOlsiY2xpZW50Il19fSwic2NvcGUiOiJvcGVuaWQgQlJBTkNIIHByb2ZpbGUgZW1haWwiLCJzaWQiOiJkNTM2OTJiZi1iY2MwLTQ5MmItODI5Mi01NThmZTJjNDIwY2QiLCJCUkFOQ0giOiIxIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJOaWtpdGEgTW9yb3pvdiIsInByZWZlcnJlZF91c2VybmFtZSI6Im1vcm96b3ZubSIsImdpdmVuX25hbWUiOiJOaWtpdGEiLCJmYW1pbHlfbmFtZSI6Ik1vcm96b3YiLCJlbWFpbCI6Im1vcm96b3ZubUBuZW9iYW5rLmNvbSJ9.AubD16XRFbT-kHdwKp8OgE_iIMajw3VMBHR0tHWIpMjzuXuyIuUrXyFxKTaTkigE0I3jqt_A8_kNZw-EwVgbIdpULfLwGFqUKN-2rKhrTUOZOsv1NmeZpMv9c2CHYB42Tf6MjMf-dh5JdUGbcCeW4haK1DuyNtoaAB1Wt1wV9ZEcLx4kI94YaqjM3lgUKFyjVp1V2768PFO8fzFRu_B3pQKqZ9khnpX9p45UYmflrw5G7HTZfHqj1brmFAj7yr-CG3R8ACi_EPxdNYikxOQtDr9mYZyRNhka71Br7UdeW2YJVyIz_MRCeuSA-UdkNETPZvFbzbh9gBm-q26NsF6sJw";
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
