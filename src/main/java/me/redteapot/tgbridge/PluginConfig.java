package me.redteapot.tgbridge;

import me.redteapot.tgbridge.templates.Template;
import me.redteapot.tgbridge.templates.TemplateSubstitutor;
import me.redteapot.tgbridge.utils.UserUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

import static me.redteapot.tgbridge.utils.Sanitizing.sanitizeForMinecraft;
import static me.redteapot.tgbridge.utils.Sanitizing.sanitizeForTelegram;
import static me.redteapot.tgbridge.utils.TelegramToMinecraftFormattingConverter.formatForMinecraft;
import static me.redteapot.tgbridge.utils.UserUtils.fallback;
import static me.redteapot.tgbridge.utils.UserUtils.getFullName;

public record PluginConfig(
        String username,
        String token,
        long chatID,
        int threadID,
        boolean allowChatIDCommand,
        Template<Message> mcMessageTemplate,
        Template<AsyncPlayerChatEvent> tgMessageTemplate
) {
    @SuppressWarnings("Convert2MethodRef")
    public static PluginConfig fromSpigotConfiguration(Configuration configuration)
    throws InvalidConfigurationException {
        final Map<String, TemplateSubstitutor<Message>> mcSubstitutors = new HashMap<>();
        mcSubstitutors.put("firstName", message ->
                sanitizeForMinecraft(message.getFrom().getFirstName()));
        mcSubstitutors.put("lastName", message ->
                sanitizeForMinecraft(fallback(message.getFrom(), User::getLastName, User::getFirstName)));
        mcSubstitutors.put("fullName", message ->
                sanitizeForMinecraft(getFullName(message.getFrom())));
        mcSubstitutors.put("username", message ->
                sanitizeForMinecraft(fallback(message.getFrom(), User::getUserName, UserUtils::getFullName)));
        if (configuration.getBoolean("mc.passFormattingFromTelegram")) {
            mcSubstitutors.put("message", message -> formatForMinecraft(message));
        } else {
            mcSubstitutors.put("message", message -> sanitizeForMinecraft(message.getText()));
        }

        final Map<String, TemplateSubstitutor<AsyncPlayerChatEvent>> tgSubstitutors = new HashMap<>();
        tgSubstitutors.put("username", event ->
                sanitizeForTelegram(event.getPlayer().getName()));
        tgSubstitutors.put("message", event ->
                sanitizeForTelegram(event.getMessage()));

        return new PluginConfig(
                requireString(configuration, "telegram.botUsername"),
                requireString(configuration, "telegram.botToken"),
                configuration.getLong("telegram.chatID"),
                configuration.getInt("telegram.threadID"),
                configuration.getBoolean("telegram.allowChatIDCommand"),
                Template.fromString(requireString(configuration, "mc.messageTemplate"), mcSubstitutors),
                Template.fromString(requireString(configuration, "telegram.messageTemplate"), tgSubstitutors)
        );
    }

    private static String requireString(Configuration configuration, String path) throws InvalidConfigurationException {
        final String value = configuration.getString(path);
        if (value == null) {
            throw new InvalidConfigurationException("Option " + path + " is required but not provided");
        } else {
            return value;
        }
    }
}
