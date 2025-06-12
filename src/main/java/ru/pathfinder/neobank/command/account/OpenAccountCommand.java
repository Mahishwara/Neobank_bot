package ru.pathfinder.neobank.command.account;

import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.domain.CommandPath;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;

import java.util.List;

@Component
public class OpenAccountCommand implements Command {

    @Override
    public String getCommandPath() {
        return CommandPath.OPEN_ACCOUNT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException {
        if (session.getCurrentCommand() == null) {
            return MessageData.of(getInfoMessage());
        }
        return handleMessage(message);
    }

    @Override
    public List<Command> getNextCommands() {
        return List.of();
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Открыть новый дебетовый счет (рублевый/валютный)";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }

    private MessageData handleMessage(String message) {
        return switch (message) {
            case "1":

        }
    }

    private String getInfoMessage() {
        return """
                Открытие счета:
                Выберите валюту:
                \t1. RUB (Рубли) – без комиссии
                \t2. USD (Доллары) – комиссия 1%
                \t3. EUR (Евро) – комиссия 1.5%
                Введите номер варианта (1-3):
                """;
    }

}
