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
import static me.redteapot.tgbridge.utils.UserUtils.fallback;
import static me.redteapot.tgbridge.utils.UserUtils.getFullName;

public record PluginConfig(
        String username,
        String token,
        long chatID,
        int threadID,
        Template<Message> mcMessageTemplate,
        Template<AsyncPlayerChatEvent> tgMessageTemplate
) {
    private static final Map<String, TemplateSubstitutor<Message>> MC_SUBSTITUTORS = new HashMap<>();
    private static final Map<String, TemplateSubstitutor<AsyncPlayerChatEvent>> TG_SUBSTITUTORS = new HashMap<>();

    static {
        MC_SUBSTITUTORS.put("firstName", message ->
                sanitizeForMinecraft(message.getFrom().getFirstName()));
        MC_SUBSTITUTORS.put("lastName", message ->
                sanitizeForMinecraft(fallback(message.getFrom(), User::getLastName, User::getFirstName)));
        MC_SUBSTITUTORS.put("fullName", message ->
                sanitizeForMinecraft(getFullName(message.getFrom())));
        MC_SUBSTITUTORS.put("username", message ->
                sanitizeForMinecraft(fallback(message.getFrom(), User::getUserName, UserUtils::getFullName)));
        MC_SUBSTITUTORS.put("message", message ->
                sanitizeForMinecraft(message.getText()));

        TG_SUBSTITUTORS.put("username", event ->
                sanitizeForTelegram(event.getPlayer().getName()));
        TG_SUBSTITUTORS.put("message", event ->
                sanitizeForTelegram(event.getMessage()));
    }

    public static PluginConfig fromSpigotConfiguration(Configuration configuration)
    throws InvalidConfigurationException {
        return new PluginConfig(
                requireString(configuration, "telegram.botUsername"),
                requireString(configuration, "telegram.botToken"),
                configuration.getLong("telegram.chatID"),
                configuration.getInt("telegram.threadID"),
                Template.fromString(requireString(configuration, "mc.messageTemplate"), MC_SUBSTITUTORS),
                Template.fromString(requireString(configuration, "telegram.messageTemplate"), TG_SUBSTITUTORS)
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
