package me.redteapot.tgbridge.templates;

import me.redteapot.tgbridge.templates.TemplateFragment.Substitution;
import me.redteapot.tgbridge.templates.TemplateFragment.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TemplateParser {
    private static final Pattern SUBSTITUTION_KEY = Pattern.compile("^[a-zA-Z0-9]+$");

    public static List<TemplateFragment> parseTemplate(String template) {
        final List<TemplateFragment> fragments = new ArrayList<>();

        State state = State.TEXT;
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < template.length(); i++) {
            final char c = template.charAt(i);

            switch (state) {
                case TEXT -> {
                    switch (c) {
                        case '\\' -> state = State.ESCAPE;
                        case '{' -> {
                            fragments.add(new Text(token.toString()));
                            token.setLength(0);
                            state = State.SUBSTITUTION;
                        }
                        default -> token.append(c);
                    }
                }

                case ESCAPE -> {
                    token.append(c);
                    state = State.TEXT;
                }

                case SUBSTITUTION -> {
                    if (c == '}') {
                        final String substitutionKey = token.toString();
                        token.setLength(0);
                        if (!SUBSTITUTION_KEY.matcher(substitutionKey).matches()) {
                            throw new IllegalArgumentException("Invalid substitution key: " + substitutionKey);
                        }
                        fragments.add(new Substitution(substitutionKey));
                        state = State.TEXT;
                    } else {
                        token.append(c);
                    }
                }
            }
        }

        return fragments;
    }

    private enum State {
        TEXT,
        ESCAPE,
        SUBSTITUTION,
    }
}
