package ru.pathfinder.neobank.command.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.request.EarlyRepaymentRequest;
import ru.pathfinder.neobank.domain.dto.response.CreditResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class EarlyRepaymentCommand implements Command {

    public static final String CURRENT_CREDIT_TO_EARLY_REPAYMENT_KEY = "current_credit_to_early_repayment";

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.EARLY_REPAYMENT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null) {
            return handleMessage(session);
        }
        return handleEarlyRepayment(message, session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_EARLY_REPAYMENT;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleMessage(Session session) throws NeobankException {
        CreditResponse credit;
        if (session.getContext().containsKey(CURRENT_CREDIT_TO_EARLY_REPAYMENT_KEY)) {
            credit = (CreditResponse) session.getContext().get(CURRENT_CREDIT_TO_EARLY_REPAYMENT_KEY);
        } else {
            credit = neobankService.getCurrentCredit(session.getAuthentication());
            session.getContext().put(CURRENT_CREDIT_TO_EARLY_REPAYMENT_KEY, credit);
        }
        if (credit == null) {
            session.notifyToClear();
            return MessageData.of("Нет активных кредитов");
        }
        return MessageData.of(MessageFormat.format("""
                Досрочное погашение:
                Долг: {0} ₽
                Досрочно можно погасить: {1} ₽
                Введите сумму:
                """, credit.amount(), credit.amount()));
    }

    private MessageData handleEarlyRepayment(String message, Session session) throws NeobankException {
        CreditResponse credit = (CreditResponse) session.getContext().get(CURRENT_CREDIT_TO_EARLY_REPAYMENT_KEY);
        EarlyRepaymentRequest request = new EarlyRepaymentRequest(
                credit.id(), Integer.parseInt(message), credit.currencyNumber()
        );
        neobankService.repayment(request, session.getAuthentication());
        session.notifyToClear();
        return MessageData.of(MessageFormat.format(
                "Погашено {0}. Кредит закрыт.", message
        ));
    }

}
