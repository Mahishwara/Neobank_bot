package ru.pathfinder.neobank.command.loan;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.request.OpenCreditRequest;
import ru.pathfinder.neobank.domain.dto.response.AccountResponse;
import ru.pathfinder.neobank.domain.dto.response.ProductCreditResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanApplyCommand implements Command {

    private static final String PRODUCT_CREDIT_INFO_KEY = "product_credit_info";

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.LOAN_APPLY;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null){
            throw new CommandHandleException("Необходимо выбрать тип кредита (/loan_apply [тип])");
        }
        if (session.getContext().containsKey(PRODUCT_CREDIT_INFO_KEY)) {
            return handleOpenCredit(message, session);
        } else {
            return handleFindCredit(message, session);
        }
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_LOAN_APPLY;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleOpenCredit(String message, Session session) throws CommandHandleException, NeobankException {
        String[] args = message.split(" ");
        if (args.length != 3) {
            throw new CommandHandleException("Неверно введены параметры (Формат: 100000 12 12341)");
        }
        if (neobankService.getCurrentCredit(session.getAuthentication()) != null) {
            throw new CommandHandleException("У вас уже есть активный кредит. Новый кредит можно оформить " +
                    "только после полного погашения текущего");
        }
        ProductCreditResponse credit = (ProductCreditResponse) session.getContext().get(PRODUCT_CREDIT_INFO_KEY);
        AccountResponse account = neobankService.findAccount(args[2], session.getAuthentication());
        if (account == null) {
            throw new CommandHandleException(Messages.ACCOUNT_NOT_FOUND_EXCEPTION);
        }
        OpenCreditRequest request = new OpenCreditRequest(
            Long.parseLong(args[0]), 15, Integer.parseInt(args[1]), credit.currencyNumber(), credit.id(), account.id().toString()
        );
        neobankService.openCredit(request, session.getAuthentication());
        session.notifyToClear();
        return MessageData.of("Кредит успешно открыт");
    }

    private MessageData handleFindCredit(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null) {
            throw new CommandHandleException("Необходимо выбрать тип кредита (/loan_apply [тип])");
        }
        ProductCreditResponse productCreditResponse = findCredit(session, message);
        if (productCreditResponse == null) {
            throw new CommandHandleException(MessageFormat.format("Не найден тип кредита: {0}", message));
        }
        session.getContext().put(PRODUCT_CREDIT_INFO_KEY, productCreditResponse);
        return MessageData.of(MessageFormat.format("""
                Оформление кредита:
                Вы выбрали: {0}
                Введите:
                1. Сумму (от 10 000)₽
                2. Срок (6-60 мес.)
                3. 5 последних цифр счета
                Формат: 100000 12 04045
                """, productCreditResponse.name()));
    }

    @Nullable
    private ProductCreditResponse findCredit(Session session, String id) throws NeobankException {
        List<ProductCreditResponse> credits = neobankService.getCreditProducts(session.getAuthentication());
        return credits.stream()
                .filter(c -> c.id() == Integer.parseInt(id))
                .findFirst()
                .orElse(null);
    }

}
