package ru.pathfinder.neobank.command.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.domain.CommandPath;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.AccountResponse;
import ru.pathfinder.neobank.service.NeobankService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountsCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.ACCOUNTS;
    }

    @Override
    public MessageData execute(String message, Session session) {
        List<AccountResponse> accounts = neobankService.getAccounts(session.getAuthentication());
        return MessageData.of(getAccountInfos(accounts));
    }

    @Override
    public List<Command> getNextCommands() {
        return Collections.emptyList();
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public String getDescription() {
        return "Просмотр списка открытых счетов";
    }

    @Override
    public boolean requiresAuthorization() {
        return false;
    }

    private String getAccountInfos(List<AccountResponse> accounts) {
        return CollectionUtils.isEmpty(accounts)
                ? "У вас нет доступных счетов"
                : String.format("%s:\n%s", "Ваши счета", formatAccounts(accounts));
    }

    private String formatAccounts(List<AccountResponse> accounts) {
        List<String> accountInfos = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            AccountResponse account = accounts.get(i);
            String accountInfo = formatAccount(account);
            accountInfos.add(String.format("%d. %s", i, accountInfo));
        }
        return String.join("\n", accountInfos);
    }

    private String formatAccount(AccountResponse account) {
        return """
                •%s (%s) - %s
                \t\t\t▸ Баланс: %s
                \t\t\t▸ Реквизиты: %s
                """.formatted(account.accountNumber(), null, account.accountType(), account.amount(), null);
    }

}
