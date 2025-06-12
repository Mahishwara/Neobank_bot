package ru.pathfinder.neobank.domain;

import lombok.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Данные о сообщении, возвращаемом пользователю
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageData {
    private String text;
    private List<String> actionsForSelection;
    private boolean hasException;

    private MessageData(String text) {
        this.text = text;
    }

    public static MessageData of(String text, String... actions) {
        MessageData msg = new MessageData(text);
        msg.actionsForSelection = actions != null && actions.length > 0 ? Arrays.asList(actions) : Collections.emptyList();
        return msg;
    }

    public static MessageData ofException(String text) {
        MessageData messageData = of(text);
        messageData.hasException = true;
        return messageData;
    }

}
