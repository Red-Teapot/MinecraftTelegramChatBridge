package me.redteapot.tgbridge;

import me.redteapot.tgbridge.mc.listeners.PlayerMeCommandListener;
import me.redteapot.tgbridge.templates.Template;
import me.redteapot.tgbridge.templates.TemplateSubstitutor;
import me.redteapot.tgbridge.utils.UserUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static me.redteapot.tgbridge.utils.Sanitizing.sanitizeForMinecraft;
import static me.redteapot.tgbridge.utils.Sanitizing.sanitizeForTelegram;
import static me.redteapot.tgbridge.utils.TelegramToMinecraftFormattingConverter.formatForMinecraft;
import static me.redteapot.tgbridge.utils.UserUtils.fallback;
import static me.redteapot.tgbridge.utils.UserUtils.getFullName;

public record PluginConfig(
        String username,
        String token,
        long chatID,
        Integer threadID,
        boolean allowChatIDCommand,
        Template<Message> mcMessageTemplate,
        Template<AsyncPlayerChatEvent> tgMessageTemplate,
        boolean announceAdvancements,
        Template<PlayerAdvancementDoneEvent> advancementMessageTemplate,
        boolean announcePlayersJoining,
        Template<PlayerJoinEvent> playerJoinMessageTemplate,
        boolean announcePlayersQuitting,
        Template<PlayerQuitEvent> playerQuitMessageTemplate,
        boolean announcePlayerDeaths,
        Template<PlayerDeathEvent> playerDeathMessageTemplate,
        boolean forwardMeCommand,
        Template<PlayerMeCommandListener.Context> playerMeCommandTemplate
) {
    public static PluginConfig fromSpigotConfiguration(Configuration configuration)
    throws InvalidConfigurationException {
        return new PluginConfig(
                requireString(configuration, "telegram.botUsername"),
                requireString(configuration, "telegram.botToken"),
                configuration.getLong("telegram.chatID"),
                optionalInteger(configuration, "telegram.threadID"),
                configuration.getBoolean("telegram.allowChatIDCommand"),
                createMcChatMessageTemplate(configuration),
                createTgChatMessageTemplate(configuration),
                configuration.getBoolean("telegram.announceAdvancements"),
                createTgAdvancementMessageTemplate(configuration),
                configuration.getBoolean("telegram.announcePlayersJoining"),
                createTgPlayerJoinMessageTemplate(configuration),
                configuration.getBoolean("telegram.announcePlayersQuitting"),
                createTgPlayerQuitMessageTemplate(configuration),
                configuration.getBoolean("telegram.announcePlayerDeaths"),
                createTgPlayerDeathMessageTemplate(configuration),
                configuration.getBoolean("telegram.forwardMeCommand"),
                createTgPlayerMeCommandMessageTemplate(configuration)
        );
    }

    @SuppressWarnings("Convert2MethodRef")
    private static Template<Message> createMcChatMessageTemplate(ConfigurationSection configuration)
    throws InvalidConfigurationException {
        return createTemplateFromConfig(configuration, "mc.messageTemplate", substitutors -> {
            substitutors.put("firstName", message ->
                    sanitizeForMinecraft(message.getFrom().getFirstName()));
            substitutors.put("lastName", message ->
                    sanitizeForMinecraft(fallback(message.getFrom(), User::getLastName, User::getFirstName)));
            substitutors.put("fullName", message ->
                    sanitizeForMinecraft(getFullName(message.getFrom())));
            substitutors.put("username", message ->
                    sanitizeForMinecraft(fallback(message.getFrom(), User::getUserName, UserUtils::getFullName)));

            if (configuration.getBoolean("mc.passFormattingFromTelegram")) {
                substitutors.put("message", message -> formatForMinecraft(message));
            } else {
                substitutors.put("message", message -> sanitizeForMinecraft(message.getText()));
            }
        });
    }

    private static Template<AsyncPlayerChatEvent> createTgChatMessageTemplate(ConfigurationSection configuration)
    throws InvalidConfigurationException {
        return createTemplateFromConfig(configuration, "telegram.messageTemplate", substitutors -> {
            substitutors.put("username", event -> sanitizeForTelegram(event.getPlayer().getName()));
            substitutors.put("message", event -> sanitizeForTelegram(event.getMessage()));
        });
    }

    private static Template<PlayerAdvancementDoneEvent> createTgAdvancementMessageTemplate(
            ConfigurationSection configuration
    ) throws InvalidConfigurationException {
        return createTemplateFromConfig(configuration, "telegram.advancementTemplate", substitutors -> {
            substitutors.put("username", event ->
                    sanitizeForTelegram(event.getPlayer().getName()));
            substitutors.put("title", event ->
                    sanitizeForTelegram(event.getAdvancement().getDisplay().getTitle()));
            substitutors.put("description", event ->
                    sanitizeForTelegram(event.getAdvancement().getDisplay().getDescription()));
        });
    }

    private static Template<PlayerJoinEvent> createTgPlayerJoinMessageTemplate(ConfigurationSection configuration)
    throws InvalidConfigurationException {
        return createTemplateFromConfig(configuration, "telegram.playerJoinTemplate", substitutors -> {
            substitutors.put("username", event -> sanitizeForTelegram(event.getPlayer().getName()));
        });
    }

    private static Template<PlayerQuitEvent> createTgPlayerQuitMessageTemplate(ConfigurationSection configuration)
    throws InvalidConfigurationException {
        return createTemplateFromConfig(configuration, "telegram.playerQuitTemplate", substitutors -> {
            substitutors.put("username", event -> sanitizeForTelegram(event.getPlayer().getName()));
        });
    }

    private static Template<PlayerDeathEvent> createTgPlayerDeathMessageTemplate(ConfigurationSection configuration)
    throws InvalidConfigurationException {
        return createTemplateFromConfig(configuration, "telegram.playerDeathTemplate", substitutors -> {
            substitutors.put("username", event -> sanitizeForTelegram(event.getEntity().getName()));
            substitutors.put("message", event -> sanitizeForTelegram(event.getDeathMessage()));
        });
    }

    private static Template<PlayerMeCommandListener.Context> createTgPlayerMeCommandMessageTemplate(
            ConfigurationSection configuration
    ) throws InvalidConfigurationException {
        return createTemplateFromConfig(configuration, "telegram.playerMeCommandTemplate", substitutors -> {
            substitutors.put("username", event -> sanitizeForTelegram(event.player().getName()));
            substitutors.put("message", event -> sanitizeForTelegram(event.message()));
        });
    }

    private static <T> Template<T> createTemplateFromConfig(
            ConfigurationSection configuration,
            String path,
            Consumer<Map<String, TemplateSubstitutor<T>>> substitutorAdder) throws InvalidConfigurationException {
        final Map<String, TemplateSubstitutor<T>> substitutors = new HashMap<>();
        substitutorAdder.accept(substitutors);
        return Template.fromString(requireString(configuration, path), substitutors);
    }

    private static String requireString(ConfigurationSection configuration, String path) throws InvalidConfigurationException {
        final String value = configuration.getString(path);
        if (value == null) {
            throw new InvalidConfigurationException("Option " + path + " is required but not provided");
        } else {
            return value;
        }
    }

    private static Integer optionalInteger(ConfigurationSection configuration, String path) {
        if (configuration.contains(path, true)) {
            return configuration.getInt(path);
        } else {
            return null;
        }
    }
}
