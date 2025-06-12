package ru.pathfinder.neobank.command.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.domain.CommandPath;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.service.SessionService;

import java.util.Collections;
import java.util.List;

@Component
public class LogoutCommand implements Command {

    private final SessionService sessionService;

    @Autowired
    public LogoutCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String getCommandPath() {
        return CommandPath.LOGOUT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException {
        if (session.getCurrentCommand() == null) {
            return MessageData.of("Вы уверены, что хотите завершить сеанс?", "Да", "Нет");
        }
        return handleMessage(message, session);
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
        return "Выйти из текущей сессии";
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleMessage(String message, Session session) throws CommandHandleException {
        if (message.equals("Да")) {
            sessionService.clearSession(session);
            return MessageData.of("Сеанс завершен. Для повторного ввода используйте /start");
        } else if (message.equals("Нет")) {
            return MessageData.of("Вы отменили операцию завершения сеанса");
        }
        throw new CommandHandleException("Нет такого варианта ответа");
    }

}
