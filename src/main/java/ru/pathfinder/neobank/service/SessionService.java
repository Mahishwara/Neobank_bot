package ru.pathfinder.neobank.service;

import ru.pathfinder.neobank.domain.Session;

/**
 * Сервис сессий
 */
public interface SessionService {

    /**
     * Получить сессию
     *
     * @param userId идентификатор пользователя
     */
    Session getSession(Long userId);

    /**
     * Удалить сессию
     *
     * @param session сессия
     */
    void removeSession(Session session);

}
