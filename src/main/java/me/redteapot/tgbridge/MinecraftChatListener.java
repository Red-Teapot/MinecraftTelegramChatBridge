package me.redteapot.tgbridge;

import me.redteapot.tgbridge.templates.Template;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MinecraftChatListener extends Thread implements Listener {
    private final Logger logger;
    private final MessageSender telegramMessageSender;
    private final Template<AsyncPlayerChatEvent> messageTemplate;
    private final BlockingQueue<AsyncPlayerChatEvent> events = new LinkedBlockingQueue<>();

    public MinecraftChatListener(
            Logger logger,
            MessageSender telegramMessageSender,
            Template<AsyncPlayerChatEvent> messageTemplate
    ) {
        this.logger = logger;
        this.telegramMessageSender = telegramMessageSender;
        this.messageTemplate = messageTemplate;
    }

    @Override
    public void run() {
        while (true) {
            try {
                final AsyncPlayerChatEvent event = events.take();

                final String renderedMessage = messageTemplate.render(event);
                logger.log(Level.FINE, "Rendered Telegram message:\n" + renderedMessage);

                telegramMessageSender.send(renderedMessage);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to handle Minecraft chat event", e);
            }
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!events.offer(event)) {
            logger.warning("Failed to queue Minecraft chat event");
        }
    }
}
