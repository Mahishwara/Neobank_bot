package ru.pathfinder.neobank.command.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.service.SessionService;

@Component
@RequiredArgsConstructor
public class LogoutCommand implements Command {

    private final SessionService sessionService;

    @Override
    public String getCommandPath() {
        return CommandPath.LOGOUT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException {
        if (session.getCurrentCommand() == null) {
            return MessageData.of(Messages.COMMAND_LOGOUT_CONFIRM_QUESTION, Messages.QUIT, Messages.CANCEL);
        }
        return handleMessage(message, session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_LOGOUT;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleMessage(String message, Session session) throws CommandHandleException {
        if (message.equals(Messages.QUIT)) {
            session.notifyToClear();
            sessionService.removeSession(session);
            return MessageData.of(Messages.COMMAND_LOGOUT_CONFIRM_YES);
        } else if (message.equals(Messages.CANCEL)) {
            return MessageData.of(Messages.COMMAND_LOGOUT_CONFIRM_NO);
        }
        throw new CommandHandleException(Messages.NO_SUCH_ANSWER_EXCEPTION);
    }

}
