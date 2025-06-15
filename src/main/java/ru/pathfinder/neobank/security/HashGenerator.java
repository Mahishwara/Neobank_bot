package ru.pathfinder.neobank.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Component;
import ru.pathfinder.neobank.config.ApplicationConfig;
import ru.pathfinder.neobank.domain.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final ApplicationConfig applicationConfig;

    public String generate(Long chatId, User user) {
        String secretKey = applicationConfig.getBackend().getTokenHash();
        String signatureData = buildSignatureData(chatId, user);
        return HmacUtils.hmacSha256Hex(secretKey, signatureData);
    }

    private String buildSignatureData(Long chatId, User user) {
        StringBuilder sb = new StringBuilder();
        ApplicationConfig.TelegramConfig telegramConfig = applicationConfig.getTelegram();
        appendField(sb, "bot_id", telegramConfig.getId());
        appendField(sb, "bot_username", telegramConfig.getUsername());
        appendField(sb, "chat_id", Long.toString(chatId));
        appendField(sb, "chat_type", telegramConfig.getChatType());
        if (user.getFirstName() != null) {
            appendField(sb, "first_name", user.getFirstName());
        }
        if (user.getLastName() != null) {
            appendField(sb, "last_name", user.getLastName());
        }
        appendField(sb, "user_id", Long.toString(user.getId()));
        appendField(sb, "username", user.getUsername());
        return sb.toString();
    }

    private void appendField(StringBuilder sb, String fieldName, String fieldValue) {
        if (!sb.isEmpty()) {
            sb.append("\n");
        }
        sb.append(fieldName).append("=").append(fieldValue);
    }

}
