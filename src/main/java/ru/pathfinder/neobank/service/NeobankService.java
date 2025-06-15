package ru.pathfinder.neobank.service;

import jakarta.annotation.Nullable;
import ru.pathfinder.neobank.domain.dto.HistoryResponse;
import ru.pathfinder.neobank.domain.dto.request.TransferRequest;
import ru.pathfinder.neobank.domain.dto.request.OpenDepositRequest;
import ru.pathfinder.neobank.domain.Currency;
import ru.pathfinder.neobank.domain.dto.request.*;
import ru.pathfinder.neobank.domain.dto.response.*;
import ru.pathfinder.neobank.exception.NeobankException;
import ru.pathfinder.neobank.security.Authentication;

import java.util.List;
import java.util.Map;

/**
 * Сервис банка "Neobank"
 */
public interface NeobankService {

    /**
     * Получить список открытых счетов
     *
     * @param authentication авторизация пользователя
     */
    List<AccountResponse> getAccounts(Authentication authentication) throws NeobankException;

    /**
     * Найти счет по последним цифрам
     *
     * @param lastNumbers       последние цифры счета
     * @param authentication    авторизация пользователя
     */
    @Nullable
    AccountResponse findAccount(String lastNumbers, Authentication authentication) throws NeobankException;

    /**
     * Открыть счет
     *
     * @param request           запрос с данными для открытия счета
     * @param authentication    авторизация пользователя
     */
    AccountResponse openAccount(OpenAccountRequest request, Authentication authentication) throws NeobankException;

    /**
     * Закрыть счет
     *
     * @param request           запрос с данными для закрытия счета
     * @param authentication    авторизация пользователя
     */
    AccountResponse closeAccount(CloseAccountRequest request, Authentication authentication) throws NeobankException;

    /**
     * Получить список доступных кредитов
     *
     * @param authentication авторизация пользователя
     */
    List<ProductCreditResponse> getCreditProducts(Authentication authentication) throws NeobankException;

    /**
     * Открыть кредит
     *
     * @param request           запрос с данными для открытия кредита
     * @param authentication    авторизация пользователя
     *
     * @return данные об открытом кредите
     */
    CreditResponse openCredit(OpenCreditRequest request, Authentication authentication) throws NeobankException;

    /**
     * Получить текущий кредит пользователя
     *
     * @param authentication авторизация пользователя
     */
    @Nullable
    CreditResponse getCurrentCredit(Authentication authentication) throws NeobankException;

    /**
     * Получить график платежей по кредиту
     *
     * @param creditId          идентификатор кредита
     * @param authentication    авторизация пользователя
     */
    PaymentPlanResponse getPaymentPlan(String creditId, Authentication authentication) throws NeobankException;

    /**
     * Полное досрочное погашение кредита
     *
     * @param request           запрос с данными для досрочного погашения кредита
     * @param authentication    авторизация пользователя
     */
    void repayment(EarlyRepaymentRequest request, Authentication authentication) throws NeobankException;

    /**
     * Получить список доступных вкладов
     *
     * @param authentication авторизация пользователя
     */
    List<ProductDepositResponse> getDepositProducts(Authentication authentication) throws NeobankException;

    /**
     * Открыть вклад
     *
     * @param request           запрос с данными о вкладе
     * @param authentication    авторизация пользователя
     */
    void openDeposit(OpenDepositRequest request, Authentication authentication) throws NeobankException;

    /**
     * Получить текущий вклад пользователя
     *
     * @param authentication авторизация пользователя
     */
    DepositResponse getCurrentDeposit(Authentication authentication) throws NeobankException;

    /**
     * Закрыть вклад
     *
     * @param request           данные для закрытия вклада
     * @param authentication    авторизация пользователя
     */
    void closeDeposit(CloseDepositRequest request, Authentication authentication) throws NeobankException;

    /**
     * Получить информацию о валютах
     *
     * @param authentication авторизация пользователя
     * @return карта вида [currencyNumber -> currency]
     */
    Map<String, Currency> getCurrencies(Authentication authentication);

    /**
     * Перевести деньги на другой счет
     *
     * @param request           данные для перевода
     * @param authentication    авторизация пользователя
     */
    void transfer(TransferRequest request, Authentication authentication) throws NeobankException;

    /**
     * Получить историю переводов
     *
     * @param request           запрос с данными для получения истории
     * @param authentication    авторизация пользователя
     */
    HistoryResponse getHistory(HistoryRequest request, Authentication authentication) throws NeobankException;

    /**
     * Получить токен авторизации пользователя
     *
     * @param params параметры для получения авторизации
     * @return bearer токен, или {@code null}
     */
    String getToken(Map<String, Object> params) throws NeobankException;
}
