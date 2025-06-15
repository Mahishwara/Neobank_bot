package ru.pathfinder.neobank.command.main;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;

@Component
@Order(1)
public class StartCommand implements Command {

    @Override
    public String getCommandPath() {
        return CommandPath.START;
    }

    @Override
    public MessageData execute(String message, Session session) {
        return MessageData.of(Messages.COMMAND_START_MESSAGE);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_START;
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }

}
