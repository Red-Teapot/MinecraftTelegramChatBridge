package me.redteapot.tgbridge.utils;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.User;

import static me.redteapot.tgbridge.utils.UserUtils.fallback;
import static me.redteapot.tgbridge.utils.UserUtils.getFullName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserUtilsTests {
    @Test
    public void testGetFullName_WhenUserHasBothFirstAndLastName_ShouldReturnFullName() {
        final User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        final String fullName = getFullName(user);

        assertEquals("John Doe", fullName);
    }

    @Test
    public void testGetFullName_WhenLastNameIsEmpty_ShouldReturnFirstName() {
        final User user = new User();
        user.setFirstName("John");
        user.setLastName("");

        final String fullName = getFullName(user);

        assertEquals("John", fullName);
    }

    @Test
    public void testGetFullName_WhenLastNameIsBlank_ShouldReturnFirstName() {
        final User user = new User();
        user.setFirstName("John");
        user.setLastName("  \t\t\n ");

        final String fullName = getFullName(user);

        assertEquals("John", fullName);
    }

    @Test
    public void testFallback_WhenThereAreNoReaders_ShouldReturnNull() {
        final User user = new User();

        final String result = fallback(user);

        assertNull(result);
    }

    @Test
    public void testFallback_WhenFirstReaderReturnsNonNullValue_ShouldReturnFirstValue() {
        final User user = new User();

        final String result = fallback(user, u -> "foo", u -> "bar");

        assertEquals("foo", result);
    }

    @Test
    public void testFallback_WhenFirstReaderReturnsNull_ShouldReturnSecondValue() {
        final User user = new User();

        final String result = fallback(user, u -> null, u -> "bar");

        assertEquals("bar", result);
    }

    @Test
    public void testFallback_WhenFirstReaderReturnsEmptyString_ShouldReturnSecondValue() {
        final User user = new User();

        final String result = fallback(user, u -> "", u -> "bar");

        assertEquals("bar", result);
    }

    @Test
    public void testFallback_WhenFirstReaderReturnsBlankString_ShouldReturnSecondValue() {
        final User user = new User();

        final String result = fallback(user, u -> "\t\n   \t", u -> "bar");

        assertEquals("bar", result);
    }

    @Test
    public void testFallback_WhenAllReadersReturnNull_ShouldReturnNull() {
        final User user = new User();

        final String result = fallback(user, u -> null, u -> null);

        assertNull(result);
    }

    @Test
    public void testFallback_WhenUserIsNull_ShouldReturnReaderValue() {
        final String result = fallback(null, u -> null, u -> "bar");

        assertEquals("bar", result);
    }
}
