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
        String[] args = message.split(" ");
        if (args.length != 4) {
            throw new CommandHandleException(EX_MESSAGE);
        }
        AccountResponse account = neobankService.findAccount(args[3], session.getAuthentication());
        if (account == null) {
            throw new CommandHandleException("Счет не найден");
        }
        TransferRequest request = new TransferRequest(account.id().toString(), args[0], Long.parseLong(args[1]), args[2]);
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
