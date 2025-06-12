package ru.pathfinder.neobank.converter;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.pathfinder.neobank.domain.CommandData;

@Component
public class TelegramMessageConverter implements MessageConverter<Update> {

    public CommandData convert(Update update) {
        Message message = update.getMessage();
        User user = message.getFrom();
        return CommandData.builder()
                .chatId(message.getChatId())
                .message(message.getText())
                .user(
                        ru.pathfinder.neobank.domain.User.builder()
                                .id(user.getId())
                                .username(user.getUserName())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .build()
                )
                .build();
    }

}
