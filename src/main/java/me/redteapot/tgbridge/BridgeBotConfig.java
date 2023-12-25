package me.redteapot.tgbridge;

import me.redteapot.tgbridge.utils.Template;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("Convert2MethodRef")
public record BridgeBotConfig(
        String username,
        String token,
        long chatID,
        int threadID,
        Template<Message> mcMessageTemplate,
        Template<AsyncPlayerChatEvent> tgMessageTemplate
) {
    private static final Map<String, Template.Substitutor<Message>> mcSubstitutors = new HashMap<>();
    private static final Map<String, Template.Substitutor<AsyncPlayerChatEvent>> tgSubstitutors = new HashMap<>();

    static {
        mcSubstitutors.put("firstName", message -> message.getFrom().getFirstName());
        mcSubstitutors.put("lastName", message -> message.getFrom().getLastName());
        mcSubstitutors.put("fullName", message -> {
            final User from = message.getFrom();
            final StringBuilder fullName = new StringBuilder();

            fullName.append(from.getFirstName());
            final String lastName = from.getLastName();
            if (lastName != null && !lastName.isBlank()) {
                fullName.append(' ').append(lastName);
            }

            return fullName.toString();
        });
        mcSubstitutors.put("username", message -> message.getFrom().getUserName());
        mcSubstitutors.put("message", message -> message.getText());

        tgSubstitutors.put("username", event -> event.getPlayer().getName());
        tgSubstitutors.put("message", event -> event.getMessage());
    }

    public static BridgeBotConfig fromSpigotConfiguration(Configuration configuration) {
        return new BridgeBotConfig(
                configuration.getString("telegram.botUsername"),
                configuration.getString("telegram.botToken"),
                configuration.getLong("telegram.chatID"),
                configuration.getInt("telegram.threadID"),
                new Template<>(configuration.getString("mc.messageTemplate"), mcSubstitutors),
                new Template<>(configuration.getString("telegram.messageTemplate"), tgSubstitutors)
        );
    }
}
