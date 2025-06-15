package ru.pathfinder.neobank.exception;

import ru.pathfinder.neobank.constant.Messages;

/**
 * Ошибка обработки команды
 */
public class CommandHandleException extends Exception {

    public CommandHandleException() {
        super(Messages.DEFAULT_MESSAGE_COMMAND_HANDLE_EXCEPTION);
    }

    public CommandHandleException(String message) {
        super(message);
    }

}
