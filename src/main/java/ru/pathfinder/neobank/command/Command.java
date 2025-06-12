package ru.pathfinder.neobank.command;

import ru.pathfinder.neobank.domain.Message;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.exception.CommandHandleException;

public interface Command {

    String getCommandPath();

    Message execute(String message, Session session) throws CommandHandleException;

    Command getNextCommand();

    boolean isRoot();

    String getDescription();

    boolean requiresAuthorization();

}
