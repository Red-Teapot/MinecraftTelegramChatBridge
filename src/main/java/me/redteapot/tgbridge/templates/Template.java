package me.redteapot.tgbridge.templates;

import me.redteapot.tgbridge.templates.TemplateFragment.Substitution;
import me.redteapot.tgbridge.templates.TemplateFragment.Text;

import java.util.List;
import java.util.Map;

import static me.redteapot.tgbridge.templates.TemplateParser.parseTemplate;

public class Template<C> {
    private final List<TemplateFragment> fragments;
    private final Map<String, TemplateSubstitutor<C>> substitutors;

    public Template(List<TemplateFragment> fragments, Map<String, TemplateSubstitutor<C>> substitutors) {
        for (TemplateFragment fragment : fragments) {
            if (fragment instanceof Substitution) {
                String key = ((Substitution) fragment).key();

                if (!substitutors.containsKey(key)) {
                    throw new IllegalArgumentException("Unknown substitution key: " + key);
                }
            }
        }

        this.fragments = fragments;
        this.substitutors = substitutors;
    }

    public String render(C context) {
        StringBuilder result = new StringBuilder();

        for (TemplateFragment fragment : fragments) {
            if (fragment instanceof Text) {
                result.append(((Text) fragment).text());
            } else if (fragment instanceof Substitution) {
                TemplateSubstitutor<C> substitutor = substitutors.get(((Substitution) fragment).key());
                result.append(substitutor.produce(context));
            } else {
                throw new IllegalArgumentException("Unsupported template fragment: " + fragment);
            }
        }

        return result.toString();
    }

    public static <C> Template<C> fromString(String template, Map<String, TemplateSubstitutor<C>> substitutors) {
        return new Template<>(
                parseTemplate(template),
                substitutors
        );
    }
}
