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
import ru.pathfinder.neobank.domain.dto.request.OpenAccountRequest;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.security.Authentication;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAccountCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.OPEN_ACCOUNT;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        if (session.getCurrentCommand() == null) {
            return MessageData.of(Messages.COMMAND_OPEN_ACCOUNT_INFO);
        }
        return handleMessage(message, session.getAuthentication());
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_OPEN_ACCOUNT;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private MessageData handleMessage(String message, Authentication authentication) throws CommandHandleException, NeobankException {
        Map<String, Currency> currencies = neobankService.getCurrencies(authentication);
        String currencyCode = switch (message) {
            case "1" -> currencies.get("643").currencyNumber();
            case "2" -> currencies.get("840").currencyNumber();
            case "3" -> currencies.get("978").currencyNumber();
            default -> throw new CommandHandleException(Messages.NO_SUCH_ANSWER_EXCEPTION);
        };
        AccountResponse response = neobankService.openAccount(
                new OpenAccountRequest(Integer.parseInt(currencyCode), 0), authentication
        );
        return MessageData.of(MessageFormat.format(
                Messages.COMMAND_OPEN_ACCOUNT_SUCCESS, response.accountNumber(), Messages.NOT_SPECIFIED
        ));
    }

}
