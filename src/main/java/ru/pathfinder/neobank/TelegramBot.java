package ru.pathfinder.neobank;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pathfinder.neobank.config.ApplicationConfig;
import ru.pathfinder.neobank.converter.MessageConverter;
import ru.pathfinder.neobank.converter.TelegramMessageConverter;
import ru.pathfinder.neobank.domain.CommandData;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.service.CommandHandlingService;
import ru.pathfinder.neobank.service.CommandRegistryService;
import ru.pathfinder.neobank.service.SessionService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final SessionService sessionService;

    private final CommandHandlingService commandHandlingService;

    private final CommandRegistryService commandRegistryService;

    private final MessageConverter<Update> telegramMessageConverter;

    private final ApplicationConfig applicationConfig;

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }
        CommandData data = telegramMessageConverter.convert(update);
        Session session = sessionService.getSession(data.getUserId());
        MessageData responseMessageData = commandHandlingService.handleCommand(data, session);
        sendMessage(data.getChatId(), responseMessageData);
    }

    @Override
    public void onRegister() {
        List<BotCommand> commands = commandRegistryService.getAllCommands().stream()
                .map(c -> new BotCommand(c.getCommandPath(), c.getDescription()))
                .collect(Collectors.toList());
        try {
            execute(SetMyCommands.builder().commands(commands).build());
        } catch (TelegramApiException e) {
            log.error("Ошибка при регистрации команд", e);
        }
    }

    @Override
    public String getBotUsername() {
        return applicationConfig.getTelegram().getUsername();
    }

    @Override
    public String getBotToken() {
        return applicationConfig.getTelegram().getToken();
    }

    private void sendMessage(Long chatId, @NonNull MessageData messageData) {
        SendMessage msg = SendMessage.builder().chatId(chatId)
                .text(messageData.getText())
                .build();
        createButtonsIfNeeded(msg, messageData);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения", e);
        }
    }

    private void createButtonsIfNeeded(SendMessage msg, MessageData messageData) {
        if (!CollectionUtils.isEmpty(messageData.getActionsForSelection())) {
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
            markup.setResizeKeyboard(true);
            markup.setOneTimeKeyboard(true);
            markup.setKeyboard(createButtons(messageData.getActionsForSelection()));
            msg.setReplyMarkup(markup);
        } else {
            if (!messageData.isHasException()) {
                ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
                removeKeyboard.setRemoveKeyboard(true);
                msg.setReplyMarkup(removeKeyboard);
            }
        }
    }

    private List<KeyboardRow> createButtons(List<String> actions) {
        KeyboardRow row = new KeyboardRow();
        actions.forEach(row::add);
        return Collections.singletonList(row);
    }

}
