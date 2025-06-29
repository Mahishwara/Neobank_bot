package ru.pathfinder.neobank.command.main;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.service.CommandRegistryService;

import java.text.MessageFormat;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final CommandRegistryService commandRegistryService;

    @Override
    public String getCommandPath() {
        return CommandPath.HELP;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException {
        if (message == null) {
            return handleMessage();
        }
        return handleCommandReference(message);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_HELP;
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }

    private String formatCommand(Command c) {
        return MessageFormat.format("{0} - {1}", c.getCommandPath(), c.getDescription());
    }

    private MessageData handleMessage() {
        return MessageData.of(
                commandRegistryService.getAllCommands().stream()
                        .map(this::formatCommand)
                        .collect(Collectors.joining("\n"))
        );
    }

    private MessageData handleCommandReference(String message) throws CommandHandleException {
        Command command = commandRegistryService.getCommand(message);
        if (command == null) {
            throw new CommandHandleException("Команда не найдена. Введите команду в формате");
        }
        return MessageData.of(command.getDescription());
    }

}
