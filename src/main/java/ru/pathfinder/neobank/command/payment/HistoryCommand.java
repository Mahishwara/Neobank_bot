package ru.pathfinder.neobank.command.payment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.HistoryResponse;
import ru.pathfinder.neobank.domain.dto.request.HistoryRequest;
import ru.pathfinder.neobank.domain.dto.response.AccountResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.HISTORY;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null) {
            return MessageData.of("Неверный формат");
        }
        return handleFilterMessage(message, session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_HISTORY;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleFilterMessage(String message, Session session) throws CommandHandleException, NeobankException {
        String[] args = message.split(" ");
        if (args.length != 3) {
            throw new CommandHandleException("Неверный формат");
        }
        AccountResponse account = neobankService.findAccount(args[2], session.getAuthentication());
        if (account == null) {
            throw new CommandHandleException("Счет не найден");
        }
        HistoryResponse incomingHistory = history(account.id().toString(), "INCOMING", args[0], args[1], session.getAuthentication());
        HistoryResponse outGoing = history(account.id().toString(), "OUTGOING", args[0], args[1], session.getAuthentication());
        int index = 1;
        List<String> transferInfos = new ArrayList<>();
        if (outGoing != null && outGoing.transfers() != null) {
            for (HistoryResponse.Transfer transfer : outGoing.transfers()) {
                transferInfos.add(MessageFormat.format("{0}. {1} -{2} (Перевод)", index++, transfer.datetime(), transfer.fromTransfer().amount()));
            }
        }
        if (incomingHistory != null && incomingHistory.transfers() != null) {
            for (HistoryResponse.Transfer transfer : incomingHistory.transfers()) {
                transferInfos.add(MessageFormat.format("{0}. {1} +{2} (Пополнение)", index++, transfer.datetime(), transfer.fromTransfer().amount()));
            }
        }
        session.notifyToClear();
        return MessageData.of(
                transferInfos.isEmpty() ? "Нет операций" : String.join("\n", transferInfos)
        );
    }

    private HistoryResponse history(String accountId, String type, String fromDate, String toDate, Authentication authentication) throws NeobankException {
        HistoryRequest request = new HistoryRequest(accountId, type, fromDate, toDate);
        return neobankService.getHistory(request, authentication);
    }

}
