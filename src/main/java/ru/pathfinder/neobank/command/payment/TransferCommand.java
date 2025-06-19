package ru.pathfinder.neobank.command.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.request.TransferRequest;
import ru.pathfinder.neobank.domain.dto.response.AccountResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

@Component
@RequiredArgsConstructor
public class TransferCommand implements Command {

    private static final String EX_MESSAGE = """
            Неверный формат команды /transfer
            Ввод: /transfer 1234567890 1000 (без назначения)
            """;

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.TRANSFER;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null) {
            throw new CommandHandleException(EX_MESSAGE);
        }
        String[] args_in = message.split(" ");
        String[] args = new String[4];
        if (args_in.length < 4) {
            throw new CommandHandleException(EX_MESSAGE);
        }
        args[0] = args_in[0];
        args[1] = args_in[1];
        args[2] = args_in[2];
        StringBuilder remainingString = new StringBuilder();
        for (int i = 2; i < args_in.length; i++) {
            if (i > 2) { // добавляем пробелы между словами начиная с третьего слова
                remainingString.append(' ');
            }
            remainingString.append(args_in[i]);
        }
        args[3] = remainingString.toString();
        AccountResponse account_from = neobankService.findAccount(args[0], session.getAuthentication());
        AccountResponse account_to = neobankService.findAccount(args[1], session.getAuthentication());
        if ((account_from == null) && (account_to == null)) {
            throw new CommandHandleException("Счет получателя или отправителя не найден");
        }
        TransferRequest request = new TransferRequest(account_from.id().toString(), account_to.id().toString(), Long.parseLong(args[2]), args[3]);
        neobankService.transfer(request, session.getAuthentication());
        session.notifyToClear();
        return MessageData.of("Перевод выполнен");
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_TRANSFER;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

}
