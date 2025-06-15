package ru.pathfinder.neobank.command.loan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.constant.CommandPath;
import ru.pathfinder.neobank.constant.Messages;
import ru.pathfinder.neobank.domain.MessageData;
import ru.pathfinder.neobank.domain.Session;
import ru.pathfinder.neobank.domain.dto.response.ProductCreditResponse;
import ru.pathfinder.neobank.exception.CommandHandleException;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.service.NeobankService;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LoansCommand implements Command {

    private final NeobankService neobankService;

    @Override
    public String getCommandPath() {
        return CommandPath.LOANS;
    }

    @Override
    public MessageData execute(String message, Session session) throws CommandHandleException, NeobankException {
        List<ProductCreditResponse> credits = neobankService.getCreditProducts(session.getAuthentication());
        credits.sort(Comparator.comparingLong(ProductCreditResponse::id));
        return MessageData.of(
                MessageFormat.format(Messages.COMMAND_LOANS_INFO, formatCredits(credits))
        );
    }

    @Override
    public String getDescription() {
        return Messages.COMMAND_DESCRIPTION_LOANS;
    }

    @Override
    public boolean requiresAuthorization() {
        return true;
    }

    private String formatCredits(List<ProductCreditResponse> credits) {
        return credits.stream()
                .map(c -> MessageFormat.format("{0}. {1}", c.id(), c.name()))
                .collect(Collectors.joining("\n"));
    }

}
