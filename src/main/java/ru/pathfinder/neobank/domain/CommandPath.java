package ru.pathfinder.neobank.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandPath {

    public static final String START = "/start";
    public static final String HELP = "/help";
    public static final String LOGOUT = "/logout";

    public static final String ACCOUNTS = "/accounts";
    public static final String OPEN_ACCOUNT = "/open_account";

}
