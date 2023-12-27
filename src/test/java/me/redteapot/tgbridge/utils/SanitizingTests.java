package me.redteapot.tgbridge.utils;

import org.junit.jupiter.api.Test;

import static me.redteapot.tgbridge.utils.Sanitizing.sanitizeForMinecraft;
import static me.redteapot.tgbridge.utils.Sanitizing.sanitizeForTelegram;
import static org.junit.jupiter.api.Assertions.*;

public class SanitizingTests {
    @Test
    public void testSanitizeForMinecraft_WhenTextContainsParagraphSigns_ShouldReturnStringWithoutThem() {
        final String raw = "Foo §bar";

        final String sanitized = sanitizeForMinecraft(raw);

        assertEquals("Foo bar", sanitized);
    }

    @Test
    public void testSanitizeForMinecraft_WhenTextIsEmpty_ShouldReturnEmptyString() {
        final String raw = "";

        final String sanitized = sanitizeForMinecraft(raw);

        assertTrue(sanitized.isEmpty());
    }

    @Test
    public void testSanitizeForMinecraft_WhenTextIsNull_ShouldReturnNull() {
        final String raw = null;

        final String sanitized = sanitizeForMinecraft(raw);

        assertNull(sanitized);
    }

    @Test
    public void testSanitizeForTelegram_WhenTextContainsFormatting_ShouldReturnStringWithFormattingEscaped() {
        final String raw = """
                *bold \\*text*
                _italic \\*text_
                __underline__
                ~strikethrough~
                ||spoiler||
                *bold _italic bold ~italic bold strikethrough ||italic bold strikethrough spoiler||~ __underline italic bold___ bold*
                [inline URL](http://www.example.com/)
                [inline mention of a user](tg://user?id=123456789)
                ![👍](tg://emoji?id=5368324170671202286)
                `inline fixed-width code`
                ```
                pre-formatted fixed-width code block
                ```
                ```python
                pre-formatted fixed-width code block written in the Python programming language
                ```
                """;

        final String sanitized = sanitizeForTelegram(raw);

        assertEquals("""
                \\*bold \\\\\\*text\\*
                \\_italic \\\\\\*text\\_
                \\_\\_underline\\_\\_
                \\~strikethrough\\~
                \\|\\|spoiler\\|\\|
                \\*bold \\_italic bold \\~italic bold strikethrough \\|\\|italic bold strikethrough spoiler\\|\\|\\~ \\_\\_underline italic bold\\_\\_\\_ bold\\*
                \\[inline URL\\]\\(http://www\\.example\\.com/\\)
                \\[inline mention of a user\\]\\(tg://user?id=123456789\\)
                !\\[👍\\]\\(tg://emoji?id=5368324170671202286\\)
                \\`inline fixed-width code\\`
                \\`\\`\\`
                pre-formatted fixed-width code block
                \\`\\`\\`
                \\`\\`\\`python
                pre-formatted fixed-width code block written in the Python programming language
                \\`\\`\\`
                """, sanitized);
    }

    @Test
    public void testSanitizeForTelegram_WhenTextIsEmpty_ShouldReturnEmptyString() {
        final String raw = "";

        final String sanitized = sanitizeForTelegram(raw);

        assertTrue(sanitized.isEmpty());
    }

    @Test
    public void testSanitizeForTelegram_WhenTextIsNull_ShouldReturnNull() {
        final String raw = null;

        final String sanitized = sanitizeForTelegram(raw);

        assertNull(sanitized);
    }
}
