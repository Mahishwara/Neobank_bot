package ru.pathfinder.neobank.command;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.domain.Message;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.service.CommandRegistryService;

import java.text.MessageFormat;
import java.util.stream.Collectors;

@Component
public class HelpCommand implements Command {

    private final CommandRegistryService commandRegistryService;

    @Autowired
    public HelpCommand(CommandRegistryService commandRegistryService) {
        this.commandRegistryService = commandRegistryService;
    }

    @Override
    public String getCommandPath() {
        return "/help";
    }

    @Override
    public Message execute(String message, Session session) {
        return Message.of(
                commandRegistryService.getAllCommands().stream()
                        .filter(Command::isRoot)
                        .map(this::formatCommand)
                        .collect(Collectors.joining("\n"))
        );
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
        return "Получить список доступных команд и справку";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }

    private String formatCommand(Command c) {
        return MessageFormat.format("{0} - {1}", c.getCommandPath(), c.getDescription());
    }

}
