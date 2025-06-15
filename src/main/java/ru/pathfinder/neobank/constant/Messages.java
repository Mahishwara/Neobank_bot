package ru.pathfinder.neobank.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Временная замена {@link java.util.ResourceBundle}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Messages {

    // Общие
    public static final String YES = "Да";
    public static final String NO = "Нет";
    public static final String CONFIRM = "Подтвердить";
    public static final String CLOSE = "Закрыть";
    public static final String CANCEL = "Отменить";
    public static final String NOT_SPECIFIED = "Не указано";

    // Сообщения исключений
    public static final String DEFAULT_MESSAGE_COMMAND_HANDLE_EXCEPTION = "Ошибка обработки команды";
    public static final String NO_SUCH_ANSWER_EXCEPTION = "Нет такого варианта ответа";

    public static final String MESSAGE_MUST_CONTAINS_ONLY_NUMBERS_EXCEPTION = "Номер должен содержать только цифры";
    public static final String MESSAGE_LENGTH_EXCEPTION = "Длина введенного значения не равна {0}";

    public static final String ACCOUNT_NOT_FOUND_EXCEPTION = "Счет не найден";

    // Описания команд
    public static final String COMMAND_DESCRIPTION_START = "Начало работы с ботом, запуск процесса авторизации";
    public static final String COMMAND_DESCRIPTION_LOGOUT = "Выйти из текущей сессии";
    public static final String COMMAND_DESCRIPTION_HELP = "Получить список доступных команд и справку";

    public static final String COMMAND_DESCRIPTION_ACCOUNTS = "Просмотр списка открытых счетов";
    public static final String COMMAND_DESCRIPTION_OPEN_ACCOUNT = "Открыть новый дебетовый счет (рублевый/валютный)";
    public static final String COMMAND_DESCRIPTION_CLOSE_ACCOUNT = "Закрыть существующий счет";
    public static final String COMMAND_DESCRIPTION_BALANCE = "Проверить баланс по счету";

    public static final String COMMAND_DESCRIPTION_LOANS = "Просмотр доступных кредитных продуктов";
    public static final String COMMAND_DESCRIPTION_LOAN_APPLY = "Оформить кредит";
    public static final String COMMAND_DESCRIPTION_LOAN_INFO = "Информация о текущем кредите";
    public static final String COMMAND_DESCRIPTION_EARLY_REPAYMENT = "Досрочное погашение кредита";

    public static final String COMMAND_DESCRIPTION_DEPOSITS = "Список доступных вкладов";
    public static final String COMMAND_DESCRIPTION_NEW_DEPOSIT = "Открыть вклад";
    public static final String COMMAND_DESCRIPTION_CLOSE_DEPOSIT = "Досрочно закрыть вклад";

    public static final String COMMAND_DESCRIPTION_TRANSFER = "Перевод средств между счетами";
    public static final String COMMAND_DESCRIPTION_HISTORY = "История транзакций";
    public static final String COMMAND_DESCRIPTION_PAY = "Оплата услуг (ЖКХ, мобильная связь и др.)";

    // Сообщения команды START
    public static final String COMMAND_START_MESSAGE = "Добро пожаловать в NeoBank!";

    // Сообщения команды HELP
    public static final String COMMAND_LOGOUT_CONFIRM_QUESTION = "Вы уверены, что хотите завершить сеанс?";
    public static final String COMMAND_LOGOUT_CONFIRM_YES = "Сеанс завершен. Для повторного ввода используйте /start";
    public static final String COMMAND_LOGOUT_CONFIRM_NO = "Вы отменили операцию завершения сеанса";

    // Сообщения команды ACCOUNTS
    public static final String COMMAND_ACCOUNTS_EMPTY = "У вас нет доступных счетов";
    public static final String COMMAND_ACCOUNTS_INFO_ALL = "Ваши счета:\n{0}";
    public static final String COMMAND_ACCOUNTS_INFO_ONCE = "{0}. {1}";
    public static final String COMMAND_ACCOUNTS_INFO_ONCE_DETAILS = """
                •%s (%s) - %s
                \t\t\t▸ Баланс: %s
                \t\t\t▸ Реквизиты: %s
                """;

    // Сообщения команды OPEN_ACCOUNT
    public static final String COMMAND_OPEN_ACCOUNT_INFO = """
                        Счет в USD открыт!
                        Номер: •{0}
                        Реквизиты для пополнения: {1}
                        Комиссия списывается сразу после открытия:
                        USD: 1%
                        EUR: 1.5%
                        """;
    public static final String COMMAND_OPEN_ACCOUNT_SUCCESS = """
                        Счет в USD открыт!
                        Номер: •{0}
                        Реквизиты для пополнения: {1}
                        Комиссия списывается сразу после открытия:
                        USD: 1%
                        EUR: 1.5%
                        """;

    // Сообщения команды CLOSE_ACCOUNT
    public static final String COMMAND_CLOSE_ACCOUNT_INFO = """
                Закрытие счета:
                Доступные для закрытия:
                {0}
                Введите последние 5 цифр счета:
                """;
    public static final String COMMAND_CLOSE_ACCOUNT_SUCCESS = "Счет был успешно закрыт";
    public static final String COMMAND_CLOSE_ACCOUNT_CANCEL = "Вы отменили операцию закрытия счета";
    public static final String COMMAND_CLOSE_ACCOUNT_CONFIRM = """
                Подтверждение:
                Закрыть счет {0}? Остаток: {1}
                """;
    public static final String COMMAND_CLOSE_ACCOUNT_INFO_ONCE = "{0}. •{1}";
    public static final String COMMAND_CLOSE_ACCOUNT_INFO_ONCE_DETAILS = "{0} {1} - {2}";

    // Сообщения команды BALANCE
    public static final String COMMAND_BALANCE_INFO_ALL = """
               Проверка баланса:
               {0}
               Для деталей введите:
               /balance XXXX (последние 4 цифры)
               """;
    public static final String COMMAND_BALANCE_INFO_ALL_DETAILS = """
                Счет •{0}:
                ▸ Доступно: {1} ({2})
                ▸ Заблокировано: {3}
                """;
    public static final String COMMAND_BALANCE_INFO_ONCE = "{0}. •{1}";
    public static final String COMMAND_BALANCE_INFO_ONCE_DETAILS = "{0}: {1} ({2})";

    // Сообщения команды LOANS
    public static final String COMMAND_LOANS_INFO = """
            Доступные кредиты:
            {0}
            Для оформления: /loan_apply [тип]
            """;

    public static final String COMMAND_LOAN_INFO_EARLY_REPAYMENT = "Досрочное погашение кредита";
    public static final String COMMAND_LOAN_INFO_SCHEDULE_PAYMENT = "График платежей";

}
