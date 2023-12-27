package me.redteapot.tgbridge.templates;

public interface TemplateFragment {
    record Text(String text) implements TemplateFragment { }
    record Substitution(String key) implements TemplateFragment { }
}
