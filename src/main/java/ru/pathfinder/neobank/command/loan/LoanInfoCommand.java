package ru.pathfinder.neobank.command.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.response.CreditResponse;
import ru.pathfinder.neobank.domain.dto.response.PaymentPlanResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoanInfoCommand implements Command {

    private static final String CURRENT_CREDIT_KEY = "current_credit";

    private final NeobankService neobankService;

    private final EarlyRepaymentCommand earlyRepaymentCommand;

    @Override
    public String getCommandPath() {
        return CommandPath.LOAN_INFO;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message != null)
            if (message.equals(Messages.COMMAND_LOAN_INFO_SCHEDULE_PAYMENT)) {
                return handleSchedulePayment(session);
            } else if (message.equals(Messages.COMMAND_LOAN_INFO_EARLY_REPAYMENT)) {
                return handleRepayment(session);
        }
        return handleCommand(session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_LOAN_INFO;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleSchedulePayment(Session session) throws NeobankException {
        CreditResponse credit = (CreditResponse) session.getContext().get(CURRENT_CREDIT_KEY);
        PaymentPlanResponse response = neobankService.getPaymentPlan(credit.id(), session.getAuthentication());
        session.notifyToClear();
        return MessageData.of(MessageFormat.format(
                """
                Кредит №{0} - график платежей:
                {1}
                """, response.creditNumber(), formatPayments(response.paymentPlan())
        ));
    }

    private MessageData handleRepayment(Session session) throws NeobankException, CommandHandleException {
        CreditResponse credit = (CreditResponse) session.getContext().get(CURRENT_CREDIT_KEY);
        session.notifyToClear();
        session.update(earlyRepaymentCommand, true);
        session.getContext().put(EarlyRepaymentCommand.CURRENT_CREDIT_TO_EARLY_REPAYMENT_KEY, credit);
        return earlyRepaymentCommand.execute(null, session);
    }

    private String formatPayments(List<PaymentPlanResponse.PaymentPlanItem> paymentPlanItems) {
        return paymentPlanItems.stream()
                .map(p -> MessageFormat.format("{0} - {1} ₽", p.paymentDate(), p.monthPayment()))
                .collect(Collectors.joining("\n"));
    }

    private MessageData handleCommand(Session session) throws NeobankException {
        CreditResponse credit = neobankService.getCurrentCredit(session.getAuthentication());
        if (credit == null) {
            return MessageData.of("Нет активных кредитов");
        }
        session.getContext().put(CURRENT_CREDIT_KEY, credit);
        return MessageData.of(MessageFormat.format("""
                Кредит №{0}:
                ▸ Остаток: {1}
                ▸ Следующий платеж: 5 500 (25.06) ₽
                ▸ Просрочка: Нет
                """, credit.creditNumber(), credit.amount()), Messages.COMMAND_LOAN_INFO_EARLY_REPAYMENT, Messages.COMMAND_LOAN_INFO_SCHEDULE_PAYMENT);
    }

}
