package ru.pathfinder.neobank.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.security.Authentication;

@Data
@NoArgsConstructor
public class Session {
    private Long userId;
    private Command currentCommand;
    private Authentication authentication;

    public Session(Long userId) {
        this.userId = userId;
    }

    public boolean hasCurrentCommand() {
        return currentCommand != null;
    }

    public boolean isUnauthorized() {
        return authentication == null;
    }

    public void clear() {
        currentCommand = null;
        authentication = null;
    }

}
