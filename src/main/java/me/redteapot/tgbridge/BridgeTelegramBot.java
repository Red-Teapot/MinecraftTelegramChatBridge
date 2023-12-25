package me.redteapot.tgbridge;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BridgeTelegramBot extends TelegramLongPollingBot implements MessageSender {
    private final PluginConfig config;
    private final Logger logger;
    private final MessageSender minecraftMessageSender;

    public BridgeTelegramBot(Logger logger, PluginConfig config, MessageSender minecraftMessageSender) {
        super(config.token());
        this.logger = logger;
        this.config = config;
        this.minecraftMessageSender = minecraftMessageSender;
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

            final String renderedMessage = config.mcMessageTemplate().render(message);
            logger.log(Level.FINE, "Rendered Minecraft message:\n" + renderedMessage);

            minecraftMessageSender.send(renderedMessage);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not send Minecraft message", e);
        }
    }

    @Override
    public void send(String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(config.chatID());
        sendMessage.setMessageThreadId(config.threadID());
        sendMessage.setText(message);
        sendMessage.setParseMode("MarkdownV2");
        execute(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return config.username();
    }
}
