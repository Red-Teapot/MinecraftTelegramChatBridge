package me.redteapot.tgbridge.mc.listeners;

import me.redteapot.tgbridge.MessageSender;
import me.redteapot.tgbridge.templates.Template;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerMeCommandListener implements Listener {
    private final Logger logger;
    private final MessageSender telegramMessageSender;
    private final Template<Context> messageTemplate;

    public PlayerMeCommandListener(
            Logger logger,
            MessageSender telegramMessageSender,
            Template<Context> messageTemplate
    ) {
        this.logger = logger;
        this.telegramMessageSender = telegramMessageSender;
        this.messageTemplate = messageTemplate;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().toLowerCase().startsWith("/me ")) {
            return;
        }

        try {
            final String message = event.getMessage().substring("/me".length()).trim();
            final String renderedMessage = messageTemplate.render(new Context(event.getPlayer(), message));
            logger.log(Level.FINE, "Rendered Telegram message:\n" + renderedMessage);
            telegramMessageSender.send(renderedMessage);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to forward player me command to Telegram", e);
        }
    }

    public record Context(Player player, String message) { }
}
