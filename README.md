# Minecraft Telegram Bridge

This Spigot plugin relays messages from a Telegram thread
into Minecraft server chat and vice versa. Supports (only) Telegram threads.

## Usage

1. Create a Telegram bot
2. Specify bot username, token, chat and thread IDs in plugin config.
   Default config file will be created if you launch the server without one.

## Development

This project uses [Maven](https://maven.apache.org/) and Java 17.

- To run required checks, use `mvn clean verify`. 
  This command will run unit tests and linters.
- To build a plugin JAR, run `mvn clean package`. 
  The plugin JAR will be located at `target/tg-bridge-v0.0.1-SNAPSHOT.jar`.

## License

This project is licensed under the terms of the MIT license. 
See [LICENSE](LICENSE) file for more details.
