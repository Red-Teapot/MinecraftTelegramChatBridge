package me.redteapot.tgbridge.mc.listeners;

import me.redteapot.tgbridge.MessageSender;
import me.redteapot.tgbridge.templates.Template;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerJoinListener implements Listener {
    private final Logger logger;
    private final MessageSender telegramMessageSender;
    private final Template<PlayerJoinEvent> messageTemplate;

    public PlayerJoinListener(
            Logger logger,
            MessageSender telegramMessageSender,
            Template<PlayerJoinEvent> messageTemplate
    ) {
        this.logger = logger;
        this.telegramMessageSender = telegramMessageSender;
        this.messageTemplate = messageTemplate;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            final String renderedMessage = messageTemplate.render(event);
            logger.log(Level.FINE, "Rendered Telegram message:\n" + renderedMessage);
            telegramMessageSender.send(renderedMessage);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to forward player join announcement to Telegram", e);
        }
    }
}
