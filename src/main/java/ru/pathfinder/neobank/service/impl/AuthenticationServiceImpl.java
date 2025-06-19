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
        String bearerToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJsMERJdG0wV19FVXpEcEpIWFBaWnFObnNST2lPUkpLcmdGYnIxZ3JYdUFrIn0.eyJleHAiOjE3NTAyNjkyMDEsImlhdCI6MTc1MDI1NDgwMSwiYXV0aF90aW1lIjoxNzUwMjU0Nzk5LCJqdGkiOiIxZjBiN2M0My1hYTM3LTRhMDMtOWJhOC04ZjRhNTM4MmE4MmYiLCJpc3MiOiJodHRwczovL2lkLW5lb2JhbmsubmVvZmxleC5ydS9hdXRoL3JlYWxtcy9tc2FiYW5rIiwiYXVkIjoibXNhYmFuayIsInN1YiI6ImM4NDQxYmI1LTc5ZjgtNDZlMy05ZGI1LWQ2YWY4Yzk2MThhOCIsInR5cCI6IkJlYXJlciIsImF6cCI6Im1zYWJhbmstc2VydmljZSIsInNlc3Npb25fc3RhdGUiOiJjNWEwOWQ0ZS0wMmVkLTQyMTItYWZiMS04YTJmYWQxM2Y2ZWEiLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbIioiXSwicmVzb3VyY2VfYWNjZXNzIjp7Im1zYWJhbmsiOnsicm9sZXMiOlsiY2xpZW50Il19fSwic2NvcGUiOiJvcGVuaWQgQlJBTkNIIHByb2ZpbGUgZW1haWwiLCJzaWQiOiJjNWEwOWQ0ZS0wMmVkLTQyMTItYWZiMS04YTJmYWQxM2Y2ZWEiLCJCUkFOQ0giOiIxIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJOaWtpdGEgTW9yb3pvdiIsInByZWZlcnJlZF91c2VybmFtZSI6Im1vcm96b3ZubSIsImdpdmVuX25hbWUiOiJOaWtpdGEiLCJmYW1pbHlfbmFtZSI6Ik1vcm96b3YiLCJlbWFpbCI6Im1vcm96b3ZubUBuZW9iYW5rLmNvbSJ9.su7vy8uUKJtXGE5lA3tgPI-5e49XtTqV12PdrIdVDe7I3vwIiyeY09kgVxVmBOEgqj01O53pj3IGvG7jpblsb-2zE37ce_DBLrljI7Wan7gYTAdkOflG2nzRX-OJHGCArH01wCPSLSsFldhCO5QI3JJvSWUDu5zlMTNXSjYi3z8QWlZzR2h5IvYkk7s7MaBPU_1f-x4XfBBKAQOC5jhCi2i2gEXJWWK0ZayAFB-1qtoDUmRfe-oWnTHxURhPz8RxZwkayGNnwp7s8e5tX1GZJK_e2RVnbdj0_heHKU3OldH724A2RB_QZLkQDWbfXiKrrjYDD3kR2YJvfJK5L93HKw";
//        String bearerToken;
//        try {
//            bearerToken = neobankService.getToken(params);
//        } catch (Exception ex) {
//            bearerToken = null;
//        }
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
        if (user.getFirstName() != null) {
            params.put("first_name", user.getFirstName());
        }
        if (user.getLastName() != null) {
            params.put("last_name", user.getLastName());
        }
        params.put("user_id", Long.toString(user.getId()));
        params.put("username", user.getUsername());
        params.put("hash", hashGenerator.generate(chatId, user));
        params.put("auth_date", Long.toString(System.currentTimeMillis()));
        return params;
    }

}
