package me.redteapot.tgbridge.utils;

public class Sanitizing {
    public static String sanitizeForMinecraft(String string) {
        return string.replace("§", "");
    }

    public static String sanitizeForTelegram(String string) {
        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            final char c = string.charAt(i);

            switch (c) {
                case '*', '_', '~', '(', ')', '[', ']', '.', '`', '|', '\\' -> result.append('\\').append(c);
                default -> result.append(c);
            }
        }

        return result.toString();
    }
}
