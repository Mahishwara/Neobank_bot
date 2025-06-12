package ru.pathfinder.neobank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.config.ApplicationConfig;
import ru.pathfinder.neobank.domain.CommandData;
import ru.pathfinder.neobank.domain.Message;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.security.Authentication;

import java.util.function.Function;

@Slf4j
@Service
public class CommandHandlingService {

    private static final String MESSAGE_SEPARATOR = " ";

    private final AuthenticationService authenticationService;

    private final CommandRegistryService commandRegistryService;

    private final ApplicationConfig applicationConfig;

    @Autowired
    public CommandHandlingService(CommandRegistryService commandRegistryService,
                                  AuthenticationService authenticationService,
                                  ApplicationConfig applicationConfig) {
        this.commandRegistryService = commandRegistryService;
        this.authenticationService = authenticationService;
        this.applicationConfig = applicationConfig;
    }

    public Message handleCommand(CommandData data, Session session) {
        Command command = extractCommand(data, session);
        return session.hasCurrentCommand()
                ? handleNextCommand(command, data, session)
                : handleRootCommand(command, data, session);
    }

    private Message handleRootCommand(Command command, CommandData data, Session session) {
        return handleCommand(command, data, session, c -> c);
    }

    private Message handleNextCommand(Command command, CommandData data, Session session) {
        return handleCommand(command, data, session, Command::getNextCommand);
    }

    private Message handleCommand(Command command, CommandData data, Session session, Function<Command, Command> nextCommandExtractor) {
        if (command == null) {
            return Message.of("Неизвестная команда");
        }
        try {
            Message result = doHandleCommandWithAuthorization(command, data, session);
            session.setCurrentCommand(nextCommandExtractor.apply(command));
            return result;
        } catch (CommandHandleException e) {
            return Message.ofException(e.getMessage());
        }
    }

    private Message doHandleCommandWithAuthorization(Command command, CommandData data, Session session) throws CommandHandleException {
        String authResponse = authorizeIfNeeded(command, data, session);
        if (authResponse != null) {
            return Message.of(authResponse);
        }
        return command.execute(data.getMessage(), session);
    }

    private String authorizeIfNeeded(Command command, CommandData data, Session session) {
        if (command.requiresAuthorization() && session.isUnauthorized()) {
            Long chatId = Long.parseLong(applicationConfig.getTelegram().getChatId());
            Authentication authentication = authenticationService.getAuthentication(chatId, data.getUser());
            if (authentication == null) {
                return authenticationService.getAuthenticationLink(chatId, data.getUser());
            }
            session.setAuthentication(authentication);
        }
        return null;
    }

    private Command extractCommand(CommandData data, Session session) {
        String commandPath = data.getMessage().split(MESSAGE_SEPARATOR)[0];
        Command command = commandRegistryService.getCommand(commandPath);
        if (command != null) {
            session.setCurrentCommand(null);
            return command;
        }
        return session.getCurrentCommand();
    }

}
