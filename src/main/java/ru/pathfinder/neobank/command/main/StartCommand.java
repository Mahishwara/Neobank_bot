package ru.pathfinder.neobank.command.main;

import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.domain.CommandPath;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;

import java.util.Collections;
import java.util.List;

@Component
public class StartCommand implements Command {

    @Override
    public String getCommandPath() {
        return CommandPath.START;
    }

    @Override
    public MessageData execute(String message, Session session) {
        return MessageData.of("Добро пожаловать в NeoBank!");
    }

    @Override
    public List<Command> getNextCommands() {
        return Collections.emptyList();
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
