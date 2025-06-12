package ru.pathfinder.neobank.domain;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String text;
    private List<String> actionsForSelection;
    private boolean hasException;

    private Message(String text) {
        this.text = text;
    }

    public static Message of(String text) {
        Message msg = new Message(text);
        msg.actionsForSelection = Collections.emptyList();
        return msg;
    }

    public static Message ofException(String text) {
        Message message = new Message(text);
        message.hasException = true;
        return message;
    }

}
