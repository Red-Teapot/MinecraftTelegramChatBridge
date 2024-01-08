package me.redteapot.tgbridge.utils;

import me.redteapot.tgbridge.mc.FormatCode;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.function.Function;

import static me.redteapot.tgbridge.utils.Sanitizing.sanitizeForMinecraft;

public final class TelegramToMinecraftFormattingConverter {
    private TelegramToMinecraftFormattingConverter() { }

    public static String formatForMinecraft(Message message) {
        final String text = message.getText();
        final int length = text.length();
        if (!message.hasEntities()) {
            return sanitizeForMinecraft(text);
        }

        final Format[] formats = new Format[length + 1];

        for (MessageEntity entity : message.getEntities()) {
            final Function<Status, Format> format = switch (entity.getType()) {
                case "bold" -> Format::bold;
                case "italic" -> Format::italic;
                case "underline" -> Format::underline;
                case "strikethrough" -> Format::strikethrough;
                case "spoiler" -> Format::spoiler;
                default -> null;
            };

            if (format == null) {
                continue;
            }

            final int start = entity.getOffset();
            final int end = start + entity.getLength();

            formats[start] = Format.merge(formats[start], format.apply(Status.ENABLE));
            formats[end] = Format.merge(formats[end], format.apply(Status.DISABLE));
        }

        final StringBuilder result = new StringBuilder();
        Format currentFormat = Format.DEFAULT;
        for (int i = 0; i < length; i++) {
            if (formats[i] != null) {
                final Format oldFormat = currentFormat;
                currentFormat = Format.merge(currentFormat, formats[i]);

                if (!currentFormat.equals(oldFormat)) {
                    currentFormat.appendCode(result);
                }
            }

            final char c = text.charAt(i);
            if (c == '\n') {
                result.append(FormatCode.RESET);
                result.append('\n');
                currentFormat.appendCode(result);
            } else if (c != FormatCode.MARKER) {
                result.append(c);
            }
        }

        result.append(FormatCode.RESET);

        return result.toString();
    }

    private record Format(
            Status bold,
            Status italic,
            Status underline,
            Status strikethrough,
            Status spoiler
    ) {
        public static final Format DEFAULT =
                new Format(Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE);

        public static Format bold(Status status) {
            return new Format(status, Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE);
        }

        public static Format italic(Status status) {
            return new Format(Status.NO_CHANGE, status, Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE);
        }

        public static Format underline(Status status) {
            return new Format(Status.NO_CHANGE, Status.NO_CHANGE, status, Status.NO_CHANGE, Status.NO_CHANGE);
        }

        public static Format strikethrough(Status status) {
            return new Format(Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE, status, Status.NO_CHANGE);
        }

        public static Format spoiler(Status status) {
            return new Format(Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE, Status.NO_CHANGE, status);
        }

        public static Format merge(Format first, Format second) {
            if (first == null) {
                return second;
            }

            if (second == null) {
                return first;
            }

            return new Format(
                    Status.merge(first.bold, second.bold),
                    Status.merge(first.italic, second.italic),
                    Status.merge(first.underline, second.underline),
                    Status.merge(first.strikethrough, second.strikethrough),
                    Status.merge(first.spoiler, second.spoiler)
            );
        }

        public void appendCode(StringBuilder output) {
            final boolean noChanges = bold == Status.NO_CHANGE
                    && italic == Status.NO_CHANGE
                    && underline == Status.NO_CHANGE
                    && strikethrough == Status.NO_CHANGE
                    && spoiler == Status.NO_CHANGE;
            if (noChanges) {
                return;
            }

            output.append(FormatCode.RESET);

            if (spoiler == Status.ENABLE) {
                output.append(FormatCode.GRAY);
            }

            if (bold == Status.ENABLE) {
                output.append(FormatCode.BOLD);
            }

            if (italic == Status.ENABLE) {
                output.append(FormatCode.ITALIC);
            }

            if (underline == Status.ENABLE) {
                output.append(FormatCode.UNDERLINE);
            }
        }
    }

    private enum Status {
        NO_CHANGE,
        ENABLE,
        DISABLE;

        public static Status merge(Status first, Status second) {
            if (first == null) {
                return second;
            }

            if (second == null) {
                return first;
            }

            if (first == second) {
                return first;
            }

            if (first == NO_CHANGE) {
                return second;
            }

            if (second == NO_CHANGE) {
                return first;
            }

            return second;
        }
    }
}
