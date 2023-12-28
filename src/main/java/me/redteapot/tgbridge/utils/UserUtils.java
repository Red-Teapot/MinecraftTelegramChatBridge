package me.redteapot.tgbridge.utils;

import org.telegram.telegrambots.meta.api.objects.User;

public final class UserUtils {
    private UserUtils() { }

    public static String getFullName(User user) {
        final StringBuilder fullName = new StringBuilder();

        fullName.append(user.getFirstName());
        final String lastName = user.getLastName();
        if (lastName != null && !lastName.isBlank()) {
            fullName.append(' ').append(lastName);
        }

        return fullName.toString();
    }

    public static String fallback(User user, UserReader... readers) {
        for (UserReader reader : readers) {
            final String result = reader.apply(user);

            if (result != null && !result.isBlank()) {
                return result;
            }
        }

        return null;
    }

    @FunctionalInterface
    public interface UserReader {
        String apply(User user);
    }
}
