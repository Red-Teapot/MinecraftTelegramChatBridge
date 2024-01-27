package me.redteapot.tgbridge;

import me.redteapot.tgbridge.mc.listeners.ChatListener;
import me.redteapot.tgbridge.mc.listeners.PlayerAdvancementListener;
import me.redteapot.tgbridge.mc.listeners.PlayerDeathListener;
import me.redteapot.tgbridge.mc.listeners.PlayerJoinListener;
import me.redteapot.tgbridge.mc.listeners.PlayerMeCommandListener;
import me.redteapot.tgbridge.mc.listeners.PlayerQuitListener;
import me.redteapot.tgbridge.utils.QueuedMessageSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class TelegramBridgePlugin extends JavaPlugin {
    private BotSession telegramBotSession = null;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (telegramBotSession != null) {
            return;
        }

        try {
            final Logger logger = getLogger();
            final PluginConfig config = PluginConfig.fromSpigotConfiguration(getConfig());
            final BridgeTelegramBot bridgeTelegramBot = new BridgeTelegramBot(
                    logger,
                    config,
                    message -> getServer().broadcastMessage(message)
            );
            final QueuedMessageSender telegramMessageSender = new QueuedMessageSender(
                    logger,
                    bridgeTelegramBot,
                    256
            );

            final PluginManager pluginManager = getServer().getPluginManager();

            pluginManager.registerEvents(new ChatListener(
                    logger,
                    telegramMessageSender,
                    config.tgMessageTemplate()
            ), this);

            if (config.announceAdvancements()) {
                pluginManager.registerEvents(new PlayerAdvancementListener(
                        logger,
                        telegramMessageSender,
                        config.advancementMessageTemplate()
                ), this);
            }

            if (config.announcePlayersJoining()) {
                pluginManager.registerEvents(new PlayerJoinListener(
                        logger,
                        telegramMessageSender,
                        config.playerJoinMessageTemplate()
                ), this);
            }

            if (config.announcePlayersQuitting()) {
                pluginManager.registerEvents(new PlayerQuitListener(
                        logger,
                        telegramMessageSender,
                        config.playerQuitMessageTemplate()
                ), this);
            }

            if (config.announcePlayerDeaths()) {
                pluginManager.registerEvents(new PlayerDeathListener(
                        logger,
                        telegramMessageSender,
                        config.playerDeathMessageTemplate()
                ), this);
            }

            if (config.forwardMeCommand()) {
                pluginManager.registerEvents(new PlayerMeCommandListener(
                        logger,
                        telegramMessageSender,
                        config.playerMeCommandTemplate()
                ), this);
            }

            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotSession = api.registerBot(bridgeTelegramBot);
            telegramMessageSender.start();
        } catch (TelegramApiException e) {
            getLogger().log(Level.SEVERE, "Could not initialize Telegram API", e);
        } catch (InvalidConfigurationException e) {
            getLogger().log(Level.SEVERE, "Could not initialize the plugin: invalid configuration", e);
        }
    }

    @Override
    public void onDisable() {
        try {
            telegramBotSession.stop();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Exception while stopping Telegram bot session", e);
        } finally {
            telegramBotSession = null;
        }
    }
}
