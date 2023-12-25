package me.redteapot.tgbridge;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BridgeTelegramBot extends TelegramLongPollingBot {
    private final BridgeBotConfig config;
    private final Server server;

    public BridgeTelegramBot(BridgeBotConfig config, Server server) {
        super(config.token());
        this.config = config;
        this.server = server;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        final Message message = update.getMessage();
        if (message.getChatId() != config.chatID() || message.getMessageThreadId() != config.threadID()) {
            return;
        }

        for (Player player : server.getOnlinePlayers()) {
            player.sendMessage(config.mcMessageTemplate().render(message));
        }
    }

    @Override
    public String getBotUsername() {
        return config.username();
    }
}
