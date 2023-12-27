package me.redteapot.tgbridge;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PluginConfigTests {
    @Test
    public void testFromSpigotConfiguration_WhenUsingDefaultConfig_ShouldNotFail() throws Exception {
        final byte[] configBytes = getClass().getResourceAsStream("/config.yml").readAllBytes();
        final String configString = new String(configBytes);
        final YamlConfiguration spigotConfig = new YamlConfiguration();
        spigotConfig.loadFromString(configString);

        final PluginConfig config = PluginConfig.fromSpigotConfiguration(spigotConfig);
        assertNotNull(config.username());
        assertNotNull(config.token());
        assertNotNull(config.mcMessageTemplate());
        assertNotNull(config.tgMessageTemplate());
    }
}
