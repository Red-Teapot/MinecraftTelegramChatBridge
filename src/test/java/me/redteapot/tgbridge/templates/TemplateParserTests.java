package me.redteapot.tgbridge.templates;

import me.redteapot.tgbridge.templates.TemplateFragment.Substitution;
import me.redteapot.tgbridge.templates.TemplateFragment.Text;
import org.junit.jupiter.api.Test;

import java.util.List;

import static me.redteapot.tgbridge.templates.TemplateParser.parseTemplate;
import static org.junit.jupiter.api.Assertions.*;

public class TemplateParserTests {
    @Test
    public void testParseTemplate_WhenGivenEmptyString_ShouldReturnOneTextFragment() {
        final String template = "";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertTrue(fragments.isEmpty());
    }

    @Test
    public void testParseTemplate_WhenGivenOnlyText_ShouldReturnOneTextFragment() {
        final String template = "Hello, world!";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertEquals(List.of(new Text("Hello, world!")), fragments);
    }

    @Test
    public void testParseTemplate_WhenGivenOnlyTextEndingWithBackslash_ShouldReturnOneTextFragment() {
        final String template = "Backslash: \\";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertEquals(List.of(new Text("Backslash: \\")), fragments);
    }

    @Test
    public void testParseTemplate_WhenGivenOnlyTextEndingWithUnclosedSubstitution_ShouldReturnTwoTextFragments() {
        final String template = "Almost a substitution: {foo";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertEquals(List.of(
                new Text("Almost a substitution: "),
                // Not sure if it's worth fixing
                new Text("{foo")
        ), fragments);
    }

    @Test
    public void testParseTemplate_WhenGivenTwoSubstitutionsInARow_ShouldReturnTwoSubstitutionFragments() {
        final String template = "{foo}{bar}";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertEquals(List.of(
                new Substitution("foo"),
                new Substitution("bar")
        ), fragments);
    }

    @Test
    public void testParseTemplate_WhenGivenOnlyABackslash_ShouldReturnOneTextFragment() {
        final String template = "\\";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertEquals(List.of(new Text("\\")), fragments);
    }

    @Test
    public void testParseTemplate_WhenGivenOnlyALeftCurlyBrace_ShouldReturnOneTextFragment() {
        final String template = "{";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertEquals(List.of(new Text("{")), fragments);
    }

    @Test
    public void testParseTemplate_WhenGivenIncorrectSubstitution_ShouldThrowException() {
        final String template = "{foo?!}";

        assertThrows(IllegalArgumentException.class, () -> parseTemplate(template));
    }

    @Test
    public void testParseTemplate_WhenGivenEmptySubstitutionKey_ShouldThrowException() {
        final String template = "{}";

        assertThrows(IllegalArgumentException.class, () -> parseTemplate(template));
    }

    @Test
    public void testParseTemplate_WhenGivenEscapedCharsAndSubstitutions_ShouldReturnCorrespondingTextFragments() {
        final String template = "\\{foo} \\b\\a\\r {baz}! {qux}";

        final List<TemplateFragment> fragments = parseTemplate(template);

        assertEquals(List.of(
                new Text("{foo} bar "),
                new Substitution("baz"),
                new Text("! "),
                new Substitution("qux")
        ), fragments);
    }
}
