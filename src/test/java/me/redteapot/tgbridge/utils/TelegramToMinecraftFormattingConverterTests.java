package me.redteapot.tgbridge.utils;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;

import static me.redteapot.tgbridge.utils.TelegramToMinecraftFormattingConverter.formatForMinecraft;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TelegramToMinecraftFormattingConverterTests {
    @Test
    public void testFormatForMinecraft_WhenMessageIsEmpty_ShouldReturnEmptyString() {
        final Message message = new Message();
        message.setText("");

        final String formatted = formatForMinecraft(message);

        assertEquals("", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenMessageHasNoEntities_ShouldOnlyRemoveSectionSigns() {
        final Message message = new Message();
        message.setText("Foo §bar baz");

        final String formatted = formatForMinecraft(message);

        assertEquals("Foo bar baz", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenEntitiesDontOverlap_ShouldFormatAccordingly() {
        final Message message = new Message();
        message.setText("foo bar");
        message.setEntities(List.of(
                new MessageEntity("bold", 0, 3),
                new MessageEntity("italic", 4, 3)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("§r§lfoo§r §r§obar§r", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenOneEntityIsStrikethrough_ShouldFormatAccordingly() {
        final Message message = new Message();
        message.setText("foo bar");
        message.setEntities(List.of(
                new MessageEntity("strikethrough", 4, 3)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("foo §r§mbar§r", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenEntitiesCoverSameRange_ShouldFormatAccordingly() {
        final Message message = new Message();
        message.setText("foo bar");
        message.setEntities(List.of(
                new MessageEntity("bold", 0, 7),
                new MessageEntity("italic", 0, 7)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("§r§l§ofoo bar§r", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenEntitiesPartiallyIntersect_ShouldFormatAccordingly() {
        final Message message = new Message();
        message.setText("foo bar baz");
        message.setEntities(List.of(
                new MessageEntity("bold", 0, 7),
                new MessageEntity("italic", 4, 7)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("§r§lfoo §r§l§obar§r§o baz§r", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenOneEntityCoversAnother_ShouldFormatAccordingly() {
        final Message message = new Message();
        message.setText("foo bar baz");
        message.setEntities(List.of(
                new MessageEntity("bold", 0, 11),
                new MessageEntity("italic", 4, 3)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("§r§lfoo §r§l§obar§r§l baz§r", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenOneEntityCoversAnotherMatchingOnStart_ShouldFormatAccordingly() {
        final Message message = new Message();
        message.setText("foo bar");
        message.setEntities(List.of(
                new MessageEntity("bold", 0, 7),
                new MessageEntity("italic", 0, 3)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("§r§l§ofoo§r§l bar§r", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenOneEntityCoversAnotherMatchingOnEnd_ShouldFormatAccordingly() {
        final Message message = new Message();
        message.setText("foo bar");
        message.setEntities(List.of(
                new MessageEntity("bold", 0, 7),
                new MessageEntity("italic", 4, 3)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("§r§lfoo §r§l§obar§r", formatted);
    }

    @Test
    public void testFormatForMinecraft_WhenMessageHasMultipleLines_ShouldResetFormattingAtNewline() {
        final Message message = new Message();
        message.setText("foo\nbar");
        message.setEntities(List.of(
                new MessageEntity("bold", 0, 7)
        ));

        final String formatted = formatForMinecraft(message);

        assertEquals("§r§lfoo§r\n§r§lbar§r", formatted);
    }
}
