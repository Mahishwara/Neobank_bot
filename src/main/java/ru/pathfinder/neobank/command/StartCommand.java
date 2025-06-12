package ru.pathfinder.neobank.command;

import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.domain.Message;
import ru.pathfinder.neobank.domain.Session;

@Component
public class StartCommand implements Command {

    @Override
    public String getCommandPath() {
        return "/start";
    }

    @Override
    public Message execute(String message, Session session) {
        return Message.of("Добро пожаловать в NeoBank!");
    }

    @Override
    public Command getNextCommand() {
        return null;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Начало работы с ботом, запуск процесса авторизации";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }

}
