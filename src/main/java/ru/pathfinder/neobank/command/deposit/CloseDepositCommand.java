package ru.pathfinder.neobank.command.deposit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.request.CloseDepositRequest;
import ru.pathfinder.neobank.domain.dto.response.DepositResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class CloseDepositCommand implements Command {

    private static final String CURRENT_DEPOSIT_KEY = "current_deposit_to_close";

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.CLOSE_DEPOSIT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null) {
            return handleMessage(session);
        }
        return handleCloseDeposit(message, session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_CLOSE_DEPOSIT;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleMessage(Session session) throws NeobankException {
        DepositResponse deposit = neobankService.getCurrentDeposit(session.getAuthentication());
        session.getContext().put(CURRENT_DEPOSIT_KEY, deposit);
        return MessageData.of(MessageFormat.format(
                """
                        Досрочное закрытие:
                        Ваш вклад №{0}:
                        ▸ Сумма: {1} ₽
                        ▸ Потери: 0 ₽
                    """, deposit.depositNumber(), deposit.startAmount()
        ), Messages.CLOSE, Messages.CANCEL);
    }

    private MessageData handleCloseDeposit(String message, Session session) throws CommandHandleException, NeobankException {
        if (message.equals(Messages.CLOSE)) {
            DepositResponse deposit = (DepositResponse) session.getContext().get(CURRENT_DEPOSIT_KEY);
            CloseDepositRequest request = new CloseDepositRequest(deposit.id());
            neobankService.closeDeposit(request, session.getAuthentication());
            session.notifyToClear();
            return MessageData.of("Вклад успешно закрыт");
        } else if(message.equals(Messages.CANCEL)) {
            session.notifyToClear();
            return MessageData.of("Операция отменена");
        } else {
            throw new CommandHandleException();
        }
    }

}
