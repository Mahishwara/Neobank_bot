package ru.pathfinder.neobank.service;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import ru.pathfinder.neobank.command.Command;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommandRegistryService {

    private final Map<String, Command> commandRegistry = new HashMap<>();

    public Collection<Command> getAllCommands() {
        return commandRegistry.values();
    }

    @Nullable
    public Command getCommand(String commandPath) {
        return commandRegistry.get(commandPath);
    }

    public void registerCommands(List<Command> commands) {
        this.commandRegistry.putAll(commands.stream().collect(Collectors.toMap(Command::getCommandPath, c -> c)));
    }

}
