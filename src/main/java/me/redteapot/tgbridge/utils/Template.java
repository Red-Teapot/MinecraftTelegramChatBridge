package me.redteapot.tgbridge.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Template<C> {
    private static final Pattern substitutionPattern = Pattern.compile("^[a-zA-Z0-9]+$");

    private final List<Fragment<C>> fragments = new ArrayList<>();

    public Template(String template, Map<String, Substitutor<C>> substitutors) {
        for (String substitutorName : substitutors.keySet()) {
            if (!substitutionPattern.matcher(substitutorName).matches()) {
                throw new IllegalArgumentException("Invalid substitutor name: " + substitutorName);
            }
        }

        ParserState state = ParserState.TEXT;
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < template.length(); i++) {
            final char c = template.charAt(i);

            switch (state) {
                case TEXT:
                    switch (c) {
                        case '\\':
                            state = ParserState.ESCAPE;
                            break;
                        case '{':
                            fragments.add(new TextFragment<>(token.toString()));
                            token.setLength(0);
                            state = ParserState.SUBSTITUTION;
                            break;
                        default:
                            token.append(c);
                            break;
                    }
                    break;

                case ESCAPE:
                    token.append(c);
                    state = ParserState.TEXT;
                    break;

                case SUBSTITUTION:
                    switch (c) {
                        case '}':
                            final String substitution = token.toString();
                            token.setLength(0);
                            final Substitutor<C> substitutor = substitutors.get(substitution);
                            if (substitutor == null) {
                                throw new IllegalArgumentException("Invalid substitution: " + substitution);
                            }
                            fragments.add(new SubstitutionFragment<>(substitutor));
                            state = ParserState.TEXT;
                            break;
                        default:
                            token.append(c);
                            break;
                    }
                    break;
            }
        }
    }

    public String render(C context) {
        StringBuilder result = new StringBuilder();

        for (Fragment<C> fragment : fragments) {
            result.append(fragment.render(context));
        }

        return result.toString();
    }

    private enum ParserState {
        TEXT,
        ESCAPE,
        SUBSTITUTION,
    }

    private interface Fragment<C> {
        String render(C context);
    }

    private static class TextFragment<C> implements Fragment<C> {
        private final String contents;

        public TextFragment(String contents) {
            this.contents = contents;
        }

        @Override
        public String render(C context) {
            return contents;
        }
    }

    private static class SubstitutionFragment<C> implements Fragment<C> {
        private final Substitutor<C> substitutor;

        public SubstitutionFragment(Substitutor<C> substitutor) {
            this.substitutor = substitutor;
        }

        @Override
        public String render(C context) {
            return substitutor.produce(context);
        }
    }

    @FunctionalInterface
    public interface Substitutor<C> {
        String produce(C context);
    }
}
