package ru.pathfinder.neobank.command.deposit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.request.OpenDepositRequest;
import ru.pathfinder.neobank.domain.dto.response.AccountResponse;
import ru.pathfinder.neobank.domain.dto.response.ProductDepositResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewDepositCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.NEW_DEPOSIT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null) {
            return handleMessage(session);
        }
        return handleOpenNewDeposit(message, session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_NEW_DEPOSIT;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleMessage(Session session) throws NeobankException {
        List<ProductDepositResponse> deposits = neobankService.getDepositProducts(session.getAuthentication());
        return MessageData.of(MessageFormat.format("""
                Открытие вклада:
                Выберите:
                {0}
                Введите сумму и срок:
                /new_deposit 1 100000 12 04050 (тип сумма срок_в_мес счет)
                """, formatDeposits(deposits)));
    }

    private String formatDeposits(List<ProductDepositResponse> deposits) {
        List<String> depositInfos = new ArrayList<>();
        for (int i = 0; i < deposits.size(); i++) {
            depositInfos.add(MessageFormat.format("\t{0}. {1}", i + 1, deposits.get(i).name()));
        }
        return String.join("\n", depositInfos);
    }

    private MessageData handleOpenNewDeposit(String message, Session session) throws CommandHandleException, NeobankException {
        String[] args = message.split(" ");
        if (args.length != 4) {
            throw new CommandHandleException("Неверно введены параметры (Формат: 1 100000 12 (тип сумма срок_в_мес  счет))");
        }
        ProductDepositResponse deposit = findDeposit(Integer.parseInt(args[0]) - 1, session.getAuthentication());
        AccountResponse account = neobankService.findAccount(args[3], session.getAuthentication());
        if (account == null) {
            throw new CommandHandleException("Не найден счет");
        }
        OpenDepositRequest request = new OpenDepositRequest(
            account.id().toString(), Integer.parseInt(args[1]), 1, deposit.id(),
                deposit.currencyNumber(), Integer.parseInt(args[2]), true
        );
        neobankService.openDeposit(request, session.getAuthentication());
        session.notifyToClear();
        return MessageData.of("Вклад успешно открыт");
    }

    private ProductDepositResponse findDeposit(int id, Authentication authentication) throws NeobankException, CommandHandleException {
        List<ProductDepositResponse> deposits = neobankService.getDepositProducts(authentication);
        try {
            return deposits.get(id);
        } catch (IndexOutOfBoundsException e) {
            throw new CommandHandleException("Вклад не найден");
        }
    }

}
