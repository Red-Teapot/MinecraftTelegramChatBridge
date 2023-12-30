# Minecraft Telegram Bridge

This Spigot plugin relays messages from a Telegram thread
into Minecraft server chat and vice versa. Supports (only) Telegram threads.

## Usage

1. Install the plugin and run the server once to generate the
   default config. You will need to edit it.
2. Create a Telegram bot via BotFather. Copy the token and bot username provided by
   BotFather into the plugin config.
3. If you need the chat and thread ID, set `allowChatIDCommand` to `true` in the plugin config,
   start the server and run the `/chatid` command in the thread you want to
   use the bot in.
4. Specify chat and thread IDs in plugin config.
5. Optionally, set `allowChatIDCommand` to `false`.

## Development

This project uses [Maven](https://maven.apache.org/) and Java 17.

- To run required checks, use `mvn clean verify`. 
  This command will run unit tests and linters.
- To build a plugin JAR, run `mvn clean package`. 
  The plugin JAR will be located at `target/tg-bridge-v0.0.1-SNAPSHOT.jar`.

## License

This project is licensed under the terms of the MIT license. 
See [LICENSE](LICENSE) file for more details.
