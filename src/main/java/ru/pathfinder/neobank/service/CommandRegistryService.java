package ru.pathfinder.neobank.service;

import jakarta.annotation.Nullable;
import ru.pathfinder.neobank.command.Command;

import java.util.Collection;
import java.util.List;

/**
 * Сервис обнаружения команд
 */
public interface CommandRegistryService {

    /**
     * @return список всех корневых команд
     */
    Collection<Command> getAllRootCommands();

    /**
     * Получить команду
     *
     * @param commandPath путь команды
     */
    @Nullable
    Command getCommand(String commandPath);

    /**
     * Зарегистрировать команды
     *
     * @param commands список команд
     */
    void registerCommands(List<Command> commands);

}
