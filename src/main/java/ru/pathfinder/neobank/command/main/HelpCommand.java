package ru.pathfinder.neobank.command.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.domain.CommandPath;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.service.CommandRegistryService;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
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
        return CommandPath.HELP;
    }

    @Override
    public MessageData execute(String message, Session session) {
        return MessageData.of(
                commandRegistryService.getAllRootCommands().stream()
                        .filter(Command::isRoot)
                        .map(this::formatCommand)
                        .collect(Collectors.joining("\n"))
        );
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
