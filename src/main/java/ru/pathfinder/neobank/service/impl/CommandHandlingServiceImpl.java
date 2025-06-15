package ru.pathfinder.neobank.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.config.ApplicationConfig;
import ru.pathfinder.neobank.domain.CommandData;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.AuthenticationService;
import ru.pathfinder.neobank.service.CommandHandlingService;
import ru.pathfinder.neobank.service.CommandRegistryService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandHandlingServiceImpl implements CommandHandlingService {

    private final AuthenticationService authenticationService;

    private final CommandRegistryService commandRegistryService;

    private final ApplicationConfig applicationConfig;

    private final MessageExtractor messageExtractor;

    @Override
    public MessageData handleCommand(CommandData data, Session session) {
        Command command = extractCommand(data, session);
        return handleCommand(command, data, session);
    }

    private MessageData handleCommand(Command command, CommandData data, Session session) {
        if (command == null) {
            return MessageData.of("Неизвестная команда");
        }
        try {
            MessageData result = doHandleCommandWithAuthorization(command, data, session);
            session.update(command);
            return result;
        } catch (CommandHandleException e) {
            return MessageData.ofException(e.getMessage());
        } catch (NeobankException e) {
            return MessageData.ofException(e.getErrorResponse().errorDetail());
        } catch (RuntimeException e) {
            return MessageData.ofException(String.format("Ошибка сервера: \"%s\"", e.getMessage()));
        }
    }

    private MessageData doHandleCommandWithAuthorization(Command command, CommandData data, Session session) throws CommandHandleException, NeobankException {
        String authResponse = authorizeIfNeeded(command, data, session);
        if (authResponse != null) {
            return MessageData.of(authResponse);
        }
        return command.execute(messageExtractor.extractMessage(data), session);
    }

    private String authorizeIfNeeded(Command command, CommandData data, Session session) throws NeobankException {
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
    @RequiredArgsConstructor
    public static class MessageExtractor {

        private static final String MESSAGE_SEPARATOR = " ";

        private final CommandRegistryService commandRegistryService;

        public String getCommandPath(CommandData data) {
            return data.getMessage().split(MESSAGE_SEPARATOR)[0];
        }

        public String extractMessage(CommandData data) {
            String text = data.getMessage();
            String[] textBlocks = text.split(MESSAGE_SEPARATOR, 2);
            if (textBlocks.length == 1) {
                if (commandRegistryService.hasCommand(textBlocks[0])) {
                    return null;
                }
                return text;
            }
            if (commandRegistryService.hasCommand(textBlocks[0])) {
                return textBlocks[1];
            }
            return text;
        }

    }

}
