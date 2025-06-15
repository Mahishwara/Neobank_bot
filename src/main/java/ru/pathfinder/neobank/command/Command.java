package ru.pathfinder.neobank.command;

import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;

/**
 * Команда
 */
public interface Command {

    /**
     * @return путь команды
     */
    String getCommandPath();

    /**
     * Выполнить команду
     *
     * @param message   текст сообщения
     * @param session   сессия пользователя
     *
     * @return данные о сообщении, возвращаемому пользователю
     *
     * @throws CommandHandleException   ошибка обработки команды
     * @throws NeobankException         внутрення ошибка Neobank сервиса
     */
    MessageData execute(String message, Session session) throws CommandHandleException, NeobankException;

    /**
     * @return описание команды
     */
    String getDescription();

    /**
     * @return нужна ли авторизация для выполнения команды
     */
    boolean requiresAuthorization();

}
