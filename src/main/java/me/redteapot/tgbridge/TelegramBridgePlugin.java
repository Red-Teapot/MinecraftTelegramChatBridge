package me.redteapot.tgbridge;

import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SuppressWarnings("unused")
public class TelegramBridgePlugin extends JavaPlugin {
    private TelegramBotsApi api;
    private BridgeTelegramBot bridgeTelegramBot;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            api = new TelegramBotsApi(DefaultBotSession.class);
            bridgeTelegramBot = new BridgeTelegramBot(
                    BridgeBotConfig.fromSpigotConfiguration(getConfig()),
                    getServer()
            );
            api.registerBot(bridgeTelegramBot);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
