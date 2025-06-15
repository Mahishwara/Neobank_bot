package ru.pathfinder.neobank;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.service.CommandRegistryService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationStarter implements ApplicationRunner {

    private final TelegramBot telegramBot;

    private final CommandRegistryService commandRegistryService;

    private final List<Command> commands;

    @Override
    public void run(ApplicationArguments args) {
        try {
            commandRegistryService.registerCommands(commands);
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Error on startup application", e);
        }
    }

}
