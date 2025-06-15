package ru.pathfinder.neobank.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandPath {

    public static final String START = "/start";
    public static final String HELP = "/help";
    public static final String LOGOUT = "/logout";

    public static final String ACCOUNTS = "/accounts";
    public static final String OPEN_ACCOUNT = "/open_account";
    public static final String CLOSE_ACCOUNT = "/close_account";
    public static final String BALANCE = "/balance";

    public static final String LOANS = "/loans";
    public static final String LOAN_APPLY = "/loan_apply";
    public static final String LOAN_INFO = "/loan_info";
    public static final String EARLY_REPAYMENT = "/early_repayment";

    public static final String DEPOSITS = "/deposits";
    public static final String NEW_DEPOSIT = "/new_deposit";
    public static final String CLOSE_DEPOSIT = "/close_deposit";

    public static final String TRANSFER = "/transfer";
    public static final String HISTORY = "/history";
    public static final String PAY = "/pay";

}
