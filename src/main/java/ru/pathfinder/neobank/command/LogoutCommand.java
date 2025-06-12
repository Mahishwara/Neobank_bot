package ru.pathfinder.neobank.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.domain.Message;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.service.SessionService;

import java.util.Arrays;

@Component
public class LogoutCommand implements Command {

    private final SessionService sessionService;

    @Autowired
    public LogoutCommand(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public String getCommandPath() {
        return "/logout";
    }

    @Override
    public Message execute(String message, Session session) throws CommandHandleException {
        if (session.getCurrentCommand() == null) {
            return Message.builder()
                    .text("Вы уверены, что хотите завершить сеанс?")
                    .actionsForSelection(Arrays.asList("Да", "Нет"))
                    .build();
        }
        return handleMessage(message, session);
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
        return "Выйти из текущей сессии";
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private Message handleMessage(String message, Session session) throws CommandHandleException {
        if (message.equals("Да")) {
            sessionService.clearSession(session);
            return Message.of("Сеанс завершен. Для повторного ввода используйте /start");
        } else if (message.equals("Нет")) {
            return Message.of("Вы отменили операцию завершения сеанса");
        }
        throw new CommandHandleException("Нет такого варианта ответа");
    }

}
