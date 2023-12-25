package me.redteapot.tgbridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.logging.Level;

public class BridgeTelegramBot extends TelegramLongPollingBot implements Listener {
    private final BridgeBotConfig config;
    private final TelegramBridgePlugin plugin;

    public BridgeTelegramBot(BridgeBotConfig config, TelegramBridgePlugin plugin) {
        super(config.token());
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (!update.hasMessage() || !update.getMessage().hasText()) {
                return;
            }

            final Message message = update.getMessage();
            if (config.chatID() != message.getChatId() || config.threadID() != message.getMessageThreadId()) {
                return;
            }

            final String renderedText = config.mcMessageTemplate().render(message);
            plugin.getLogger().log(Level.FINE, "Rendered Minecraft message:\n" + renderedText);

            plugin.getServer().broadcastMessage(renderedText);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not send Minecraft message", e);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        try {
            final String renderedText = config.tgMessageTemplate().render(event);
            plugin.getLogger().log(Level.FINE, "Rendered Telegram message:\n" + renderedText);

            SendMessage message = new SendMessage();
            message.setChatId(config.chatID());
            message.setMessageThreadId(config.threadID());
            message.setText(renderedText);
            message.setParseMode("MarkdownV2");

            execute(message);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not send Telegram message", e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.username();
    }
}
