package ru.pathfinder.neobank.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommandData {
    Long chatId;
    String message;
    User user;

    public Long getUserId() {
        return user.getId();
    }
}
