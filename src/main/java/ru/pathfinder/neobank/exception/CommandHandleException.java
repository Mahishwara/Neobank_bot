package ru.pathfinder.neobank.exception;

public class CommandHandleException extends Exception {

    public CommandHandleException() {
        super("Ошибка обработки команды");
    }

    public CommandHandleException(String message) {
        super(message);
    }

}
