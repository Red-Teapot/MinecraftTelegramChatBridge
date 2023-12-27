package me.redteapot.tgbridge.templates;

import me.redteapot.tgbridge.templates.TemplateFragment.Substitution;
import me.redteapot.tgbridge.templates.TemplateFragment.Text;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TemplateTests {
    @Test
    public void testRender_WhenFragmentsIsEmpty_ShouldReturnEmptyString() {
        final List<TemplateFragment> fragments = new ArrayList<>();
        final Map<String, TemplateSubstitutor<Object>> substitutors = new HashMap<>();
        final Template<Object> template = new Template<>(fragments, substitutors);

        final String rendered = template.render(new Object());

        assertTrue(rendered.isEmpty());
    }

    @Test
    public void testConstructor_WhenFragmentsContainUnknownSubstitution_ShouldThrowException() {
        final List<TemplateFragment> fragments = new ArrayList<>();
        fragments.add(new Substitution("foo"));
        final Map<String, TemplateSubstitutor<Object>> substitutors = new HashMap<>();
        substitutors.put("bar", c -> "bar");

        assertThrows(IllegalArgumentException.class, () -> new Template<>(fragments, substitutors));
    }

    @Test
    public void testRender_WhenFragmentsIsCorrect_ShouldReturnRenderedTemplate() {
        final List<TemplateFragment> fragments = new ArrayList<>();
        fragments.add(new Text("Here is an example foo: "));
        fragments.add(new Substitution("foo"));
        fragments.add(new Text(", and here is bar: "));
        fragments.add(new Substitution("bar"));
        final Map<String, TemplateSubstitutor<Integer>> substitutors = new HashMap<>();
        substitutors.put("foo", c -> "foo " + c);
        substitutors.put("bar", c -> "bar");
        final Template<Integer> template = new Template<>(fragments, substitutors);

        final String rendered = template.render(123);

        assertEquals("Here is an example foo: foo 123, and here is bar: bar", rendered);
    }
}
