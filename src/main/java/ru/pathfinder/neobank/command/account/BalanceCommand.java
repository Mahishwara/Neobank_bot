package ru.pathfinder.neobank.command.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.Currency;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.response.AccountResponse;
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
public class BalanceCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.BALANCE;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null) {
            return getInfoMessage(session.getAuthentication());
        }
        return handleMessage(message, session);
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_BALANCE;
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

    private MessageData handleMessage(String message, Session session) throws CommandHandleException, NeobankException {
        if (message == null || message.length() != 4) {
            throw new CommandHandleException(MessageFormat.format(Messages.MESSAGE_LENGTH_EXCEPTION, 4));
        }
        if (!message.matches("\\d+")) {
            throw new CommandHandleException(Messages.MESSAGE_MUST_CONTAINS_ONLY_NUMBERS_EXCEPTION);
        }
        List<AccountResponse> accounts = neobankService.getAccounts(session.getAuthentication());
        Map<String, Currency> currencies = neobankService.getCurrencies(session.getAuthentication());
        AccountResponse account = accounts.stream()
                .filter(a -> a.accountNumber().substring(a.accountNumber().length() - 4).equals(message))
                .findFirst().orElse(null);
        if (account == null) {
            throw new CommandHandleException(Messages.ACCOUNT_NOT_FOUND_EXCEPTION);
        }
        return MessageData.of(MessageFormat.format(Messages.COMMAND_BALANCE_INFO_ALL_DETAILS,
                account.accountNumber(),
                account.amount(), getCurrencyCode(currencies, account),
                account.amount() - account.availableAmount()));
    }

    private String getInfoMessage(List<AccountResponse> accounts, Map<String, Currency> currencies) {
        return MessageFormat.format(Messages.COMMAND_BALANCE_INFO_ALL, formatAccounts(accounts, currencies));
    }

    private String formatAccounts(List<AccountResponse> accounts, Map<String, Currency> currencies) {
        List<String> accountInfos = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            AccountResponse account = accounts.get(i);
            accountInfos.add(MessageFormat.format(Messages.COMMAND_BALANCE_INFO_ONCE,
                    i + 1, formatAccount(account, currencies)));
        }
        return String.join("\n", accountInfos);
    }

    private String formatAccount(AccountResponse account, Map<String, Currency> currencies) {
        return MessageFormat.format(Messages.COMMAND_BALANCE_INFO_ONCE_DETAILS,
                account.accountNumber(), account.amount(), getCurrencyCode(currencies, account));
    }

    private String getCurrencyCode(Map<String, Currency> currencies, AccountResponse account) {
        Currency currency = currencies.get(Integer.toString(account.currencyNumber()));
        return currency == null ? "RUB" : currency.currencyCode();
    }

}
