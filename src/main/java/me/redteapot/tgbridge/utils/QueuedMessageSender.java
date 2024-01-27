package me.redteapot.tgbridge.utils;

import me.redteapot.tgbridge.MessageSender;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class QueuedMessageSender extends Thread implements MessageSender {
    private final Logger logger;
    private final MessageSender underlying;
    private final BlockingQueue<String> messages;

    public QueuedMessageSender(Logger logger, MessageSender underlying, int capacity) {
        this.logger = logger;
        this.underlying = underlying;
        this.messages = new LinkedBlockingQueue<>(capacity);
    }

    @Override
    public void run() {
        while (true) {
            try {
                underlying.send(messages.take());
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to send a message", e);
            }
        }
    }

    @Override
    public void send(String message) {
        if (!messages.offer(message)) {
            logger.warning("Failed to queue a message");
        }
    }
}
