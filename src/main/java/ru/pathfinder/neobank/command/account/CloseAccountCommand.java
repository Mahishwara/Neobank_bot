package ru.pathfinder.neobank.command.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.domain.Currency;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.response.AccountResponse;
import ru.pathfinder.neobank.domain.dto.request.CloseAccountRequest;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloseAccountCommand implements Command {

    private static final String TRANSACTION_CONFIRMATION_KEY = "transaction_confirmation_key";

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.CLOSE_ACCOUNT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (session.getCurrentCommand() == null) {
            return getInfoMessage(session.getAuthentication());
        } else if (session.getContext().containsKey(TRANSACTION_CONFIRMATION_KEY)) {
            return handleCloseMessage(message, session.getContext().get(TRANSACTION_CONFIRMATION_KEY), session);
        }
        return handleMessage(message, session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_CLOSE_ACCOUNT;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData getInfoMessage(Authentication authentication) throws NeobankException {
        List<AccountResponse> accounts = neobankService.getAccounts(authentication);
        Map<String, Currency> currencies = neobankService.getCurrencies(authentication);
        return MessageData.of(getInfoMessage(accounts, currencies));
    }

    private String getInfoMessage(List<AccountResponse> accounts, Map<String, Currency> currencies) {
        return MessageFormat.format(Messages.COMMAND_CLOSE_ACCOUNT_INFO, formatAccounts(accounts, currencies));
    }

    private MessageData handleCloseMessage(String message, Object data, Session session) throws CommandHandleException, NeobankException {
        AccountResponse accountResponse = (AccountResponse) data;
        String resultMessage;
        if (message.equals(Messages.CONFIRM)) {
            CloseAccountRequest request = new CloseAccountRequest(accountResponse.id().toString(), accountResponse.currencyNumber());
            neobankService.closeAccount(request, session.getAuthentication());
            resultMessage = Messages.COMMAND_CLOSE_ACCOUNT_SUCCESS;
        } else if (message.equals(Messages.CANCEL)) {
            resultMessage = Messages.COMMAND_CLOSE_ACCOUNT_CANCEL;
        } else {
            throw new CommandHandleException(Messages.NO_SUCH_ANSWER_EXCEPTION);
        }
        session.notifyToClear();
        return MessageData.of(resultMessage);
    }

    private MessageData handleMessage(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null || message.length() != 5) {
            throw new CommandHandleException(MessageFormat.format(Messages.MESSAGE_LENGTH_EXCEPTION, 5));
        }
        if (!message.matches("\\d+")) {
            throw new CommandHandleException(Messages.MESSAGE_MUST_CONTAINS_ONLY_NUMBERS_EXCEPTION);
        }
        AccountResponse accountToDelete = neobankService.findAccount(message, session.getAuthentication());
        if (accountToDelete == null) {
            throw new CommandHandleException(Messages.ACCOUNT_NOT_FOUND_EXCEPTION);
        }
        session.getContext().put(TRANSACTION_CONFIRMATION_KEY, accountToDelete);
        return MessageData.of(
                MessageFormat.format(Messages.COMMAND_CLOSE_ACCOUNT_CONFIRM, accountToDelete.accountNumber(), accountToDelete.amount()),
                Messages.CONFIRM, Messages.CANCEL
        );
    }

    private String formatAccounts(List<AccountResponse> accounts, Map<String, Currency> currencies) {
        List<String> accountInfos = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            AccountResponse account = accounts.get(i);
            accountInfos.add(MessageFormat.format(Messages.COMMAND_CLOSE_ACCOUNT_INFO_ONCE, i + 1, formatAccount(account, currencies)));
        }
        return String.join("\n", accountInfos);
    }

    private String formatAccount(AccountResponse account, Map<String, Currency> currencies) {
        return MessageFormat.format(Messages.COMMAND_CLOSE_ACCOUNT_INFO_ONCE_DETAILS, account.accountNumber(), getCurrencyCode(currencies, account), account.amount());
    }

    private String getCurrencyCode(Map<String, Currency> currencies, AccountResponse account) {
        Currency currency = currencies.get(Integer.toString(account.currencyNumber()));
        return currency == null ? "RUB" : currency.currencyCode();
    }

}
