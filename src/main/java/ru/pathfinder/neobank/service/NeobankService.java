package ru.pathfinder.neobank.service;

import ru.pathfinder.neobank.domain.dto.AccountResponse;
import ru.pathfinder.neobank.security.Authentication;

import java.util.List;
import java.util.Map;

/**
 * Сервис банка "Neobank"
 */
public interface NeobankService {

    /**
     * Получить список открытых счетов
     *
     * @param authentication авторизация пользователя
     */
    List<AccountResponse> getAccounts(Authentication authentication);

    /**
     * Получить токен авторизации пользователя
     *
     * @param params параметры для получения авторизации
     * @return bearer токен, или {@code null}
     */
    String getToken(Map<String, Object> params);

}
