package ru.pathfinder.neobank.service.impl;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.service.CommandRegistryService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommandRegistryServiceImpl implements CommandRegistryService {

    private final Map<String, Command> commandRegistry = new HashMap<>();

    @Override
    public Collection<Command> getAllRootCommands() {
        return commandRegistry.values().stream().filter(Command::isRoot).collect(Collectors.toList());
    }

    @Override
    public Command getCommand(String commandPath) {
        return commandRegistry.get(commandPath);
    }

    @Override
    public void registerCommands(List<Command> commands) {
        this.commandRegistry.putAll(commands.stream().collect(Collectors.toMap(Command::getCommandPath, c -> c)));
    }

}
