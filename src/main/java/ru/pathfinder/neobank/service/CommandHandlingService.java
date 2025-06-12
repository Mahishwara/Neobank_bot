package ru.pathfinder.neobank.service;

import ru.pathfinder.neobank.domain.CommandData;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;

/**
 * Сервис обработки команд
 */
public interface CommandHandlingService {

    /**
     * Обработать команду
     *
     * @param data      данные с сообщением и пользователем
     * @param session   текущая сессия пользователя
     *
     * @return данные о сообщении, возвращаемому пользователю
     */
    MessageData handleCommand(CommandData data, Session session);

}
