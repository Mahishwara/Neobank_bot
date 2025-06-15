package ru.pathfinder.neobank.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pathfinder.neobank.command.Command;
import ru.pathfinder.neobank.security.Authentication;

import java.util.HashMap;
import java.util.Map;

/**
 * Сессия
 */
@Data
@NoArgsConstructor
public class Session {
    /**
     * Идентификатор пользователя
     */
    private Long userId;
    /**
     * Текущая обрабатываемая команда
     */
    private Command currentCommand;
    /**
     * Авторизация пользователя
     */
    private Authentication authentication;
    /**
     * Контекст обработки команды
     */
    private final Map<String, Object> context = new HashMap<>();
    /**
     * Необходима очистка сессии
     */
    private boolean needClear = false;

    public Session(Long userId) {
        this.userId = userId;
    }

    /**
     * @return нет авторизации
     */
    public boolean isUnauthorized() {
        return authentication == null;
    }

    /**
     * Уведомить о необходимости очистить сессию
     */
    public void notifyToClear() {
        needClear = true;
    }

    /**
     * Обновить состояние сессии
     *
     * @param currentCommand текущая обработанная команда
     */
    public void update(Command currentCommand, boolean forceClearCommand) {
        if (clearIfNeeded()) {
            if (forceClearCommand) {
                updateCurrentCommandIfNeeded(currentCommand);
            }
            return;
        }
        updateCurrentCommandIfNeeded(currentCommand);
    }

    /**
     * See also {@link #update}
     */
    public void update(Command currentCommand) {
        update(currentCommand, false);
    }

    private void updateCurrentCommandIfNeeded(Command currentCommand) {
        if (this.currentCommand == null) {
            this.currentCommand = currentCommand;
        }
    }

    private boolean clearIfNeeded() {
        if (needClear) {
            clear();
            needClear = false;
            return true;
        }
        return false;
    }

    /**
     * Очистить сессию
     */
    private void clear() {
        currentCommand = null;
        authentication = null;
        context.clear();
    }

}
