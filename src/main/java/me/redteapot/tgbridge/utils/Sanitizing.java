package me.redteapot.tgbridge.utils;

import me.redteapot.tgbridge.mc.FormatCode;

public final class Sanitizing {
    private Sanitizing() { }

    public static String sanitizeForMinecraft(String string) {
        if (string == null) {
            return null;
        } else {
            return string.replace(FormatCode.MARKER_STR, "");
        }
    }

    public static String sanitizeForTelegram(String string) {
        if (string == null) {
            return null;
        }

        final StringBuilder result = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            final char c = string.charAt(i);

            switch (c) {
                case '_', '*', '[', ']', '(', ')', '~', '`',
                     '>', '#', '+', '-', '=', '|', '{', '}',
                     '.', '!', '\\' -> result.append('\\').append(c);
                default -> result.append(c);
            }
        }

        return result.toString();
    }
}
