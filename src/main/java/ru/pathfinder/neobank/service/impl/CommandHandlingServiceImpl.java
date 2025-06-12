package ru.pathfinder.neobank.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.config.ApplicationConfig;
import ru.pathfinder.neobank.domain.CommandData;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.AuthenticationService;
import ru.pathfinder.neobank.service.CommandHandlingService;
import ru.pathfinder.neobank.service.CommandRegistryService;

import java.util.function.Function;

@Slf4j
@Service
public class CommandHandlingServiceImpl implements CommandHandlingService {

    private final AuthenticationService authenticationService;

    private final CommandRegistryService commandRegistryService;

    private final ApplicationConfig applicationConfig;

    private final MessageExtractor messageExtractor;

    @Autowired
    public CommandHandlingServiceImpl(CommandRegistryService commandRegistryService,
                                      AuthenticationService authenticationService,
                                      MessageExtractor messageExtractor,
                                      ApplicationConfig applicationConfig) {
        this.commandRegistryService = commandRegistryService;
        this.authenticationService = authenticationService;
        this.messageExtractor = messageExtractor;
        this.applicationConfig = applicationConfig;
    }

    @Override
    public MessageData handleCommand(CommandData data, Session session) {
        Command command = extractCommand(data, session);
        return session.hasCurrentCommand()
                ? handleNextCommand(command, data, session)
                : handleRootCommand(command, data, session);
    }

    private MessageData handleRootCommand(Command command, CommandData data, Session session) {
        return handleCommand(command, data, session, c -> c);
    }

    private MessageData handleNextCommand(Command command, CommandData data, Session session) {
        return handleCommand(command, data, session, c -> c);
    }

    private MessageData handleCommand(Command command, CommandData data, Session session, Function<Command, Command> nextCommandExtractor) {
        if (command == null) {
            return MessageData.of("Неизвестная команда");
        }
        try {
            MessageData result = doHandleCommandWithAuthorization(command, data, session);
            session.setCurrentCommand(nextCommandExtractor.apply(command));
            return result;
        } catch (CommandHandleException e) {
            return MessageData.ofException(e.getMessage());
        }
    }

    private MessageData doHandleCommandWithAuthorization(Command command, CommandData data, Session session) throws CommandHandleException {
        String authResponse = authorizeIfNeeded(command, data, session);
        if (authResponse != null) {
            return MessageData.of(authResponse);
        }
        return command.execute(messageExtractor.extractMessage(data), session);
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
        Command command = commandRegistryService.getCommand(messageExtractor.getCommandPath(data));
        if (command != null) {
            session.setCurrentCommand(null);
            return command;
        }
        return session.getCurrentCommand();
    }

    @Component
    public static class MessageExtractor {

        private static final String MESSAGE_SEPARATOR = " ";

        public String getCommandPath(CommandData data) {
            return data.getMessage().split(MESSAGE_SEPARATOR)[0];
        }

        public String extractMessage(CommandData data) {
            String[] textBlocks = data.getMessage().split(MESSAGE_SEPARATOR);
            return textBlocks.length > 1 ? textBlocks[1] : null;
        }

    }

}
