package ru.pathfinder.neobank.service;

import ru.pathfinder.neobank.domain.User;
import ru.pathfinder.neobank.security.Authentication;

/**
 * Сервис авторизации пользователей
 */
public interface AuthenticationService {

    /**
     * Получить авторизацию пользователя
     *
     * @param chatId    идентификатор чата пользователя
     * @param user      пользователь
     */
    Authentication getAuthentication(Long chatId, User user);

    /**
     * Получить ссылку для авторизации пользователя
     *
     * @param chatId    идентификатор чата пользователя
     * @param user      пользователь
     */
    String getAuthenticationLink(Long chatId, User user);


}
