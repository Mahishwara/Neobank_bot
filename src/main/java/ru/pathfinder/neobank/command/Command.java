package ru.pathfinder.neobank.command;

import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;

import java.util.List;

public interface Command {

    String getCommandPath();

    MessageData execute(String message, Session session) throws CommandHandleException;

    List<Command> getNextCommands();

    boolean isRoot();

    String getDescription();

    boolean requiresAuthorization();

}
