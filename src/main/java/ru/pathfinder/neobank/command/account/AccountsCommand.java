package ru.pathfinder.neobank.command.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.Currency;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.response.AccountResponse;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AccountsCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.ACCOUNTS;
    }

    @Override
    public MessageData execute(String message, Session session) throws NeobankException {
        List<AccountResponse> accounts = neobankService.getAccounts(session.getAuthentication());
        Map<String, Currency> currencies = neobankService.getCurrencies(session.getAuthentication());
        return MessageData.of(getAccountInfos(accounts, currencies));
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_ACCOUNTS;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private String getAccountInfos(List<AccountResponse> accounts, Map<String, Currency> currencies) {
        return CollectionUtils.isEmpty(accounts)
                ? Messages.COMMAND_ACCOUNTS_EMPTY
                : MessageFormat.format(Messages.COMMAND_ACCOUNTS_INFO_ALL, formatAccounts(accounts, currencies));
    }

    private String formatAccounts(List<AccountResponse> accounts, Map<String, Currency> currencies) {
        List<String> accountInfos = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            AccountResponse account = accounts.get(i);
            String accountInfo = formatAccount(account, currencies);
            accountInfos.add(MessageFormat.format(Messages.COMMAND_ACCOUNTS_INFO_ONCE, i + 1, accountInfo));
        }
        return String.join("\n", accountInfos);
    }

    private String formatAccount(AccountResponse account, Map<String, Currency> currencies) {
        return Messages.COMMAND_ACCOUNTS_INFO_ONCE_DETAILS
                .formatted(
                        account.accountNumber(), getCurrencyCode(currencies, account), account.accountType(),
                        account.amount(),
                        account.accountNumber()
                );
    }

    private String getCurrencyCode(Map<String, Currency> currencies, AccountResponse account) {
        Currency currency = currencies.get(Integer.toString(account.currencyNumber()));
        return currency == null ? Messages.NOT_SPECIFIED : currency.currencyCode();
    }

}
