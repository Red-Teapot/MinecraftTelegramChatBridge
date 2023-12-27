package me.redteapot.tgbridge.templates;

public interface TemplateSubstitutor<C> {
    String produce(C context);
}
