package ru.pathfinder.neobank.command.deposit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.response.ProductDepositResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DepositsCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.DEPOSITS;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        List<ProductDepositResponse> deposits = neobankService.getDepositProducts(session.getAuthentication());
        return MessageData.of(MessageFormat.format(
                """
                Доступные вклады:
                {0}
                """, formatDeposits(deposits)
                ));
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_DEPOSITS;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private String formatDeposits(List<ProductDepositResponse> deposits) {
        List<String> depositInfos = new ArrayList<>();
        for (int i = 0; i < deposits.size(); i++) {
            depositInfos.add(MessageFormat.format("{0}. {1}", i + 1, deposits.get(i).name()));
        }
        return String.join("\n", depositInfos);
    }

}
