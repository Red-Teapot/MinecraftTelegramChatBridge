package me.redteapot.tgbridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
        if (!update.hasMessage()) {
            return;
        }

        final Message message = update.getMessage();
        if (config.chatID() != message.getChatId() || config.threadID() != message.getMessageThreadId()) {
            return;
        }

        plugin.getServer().broadcastMessage(config.mcMessageTemplate().render(message));
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(config.chatID());
            message.setMessageThreadId(config.threadID());
            message.setText(config.tgMessageTemplate().render(event));
            message.setParseMode("MarkdownV2");

            execute(message);
        } catch (TelegramApiException e) {
            plugin.getLogger().log(Level.WARNING, "Could not send Telegram message", e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.username();
    }
}
