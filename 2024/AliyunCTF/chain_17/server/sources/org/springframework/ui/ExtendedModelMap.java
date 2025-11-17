package org.springframework.ui;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/ui/ExtendedModelMap.class */
public class ExtendedModelMap extends ModelMap implements Model {
    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ ModelMap mergeAttributes(@Nullable Map attributes) {
        return mergeAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ ModelMap addAllAttributes(@Nullable Map attributes) {
        return addAllAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ ModelMap addAllAttributes(@Nullable Collection attributeValues) {
        return addAllAttributes((Collection<?>) attributeValues);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model mergeAttributes(@Nullable Map attributes) {
        return mergeAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Map attributes) {
        return addAllAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Collection attributeValues) {
        return addAllAttributes((Collection<?>) attributeValues);
    }

    @Override // org.springframework.ui.Model
    public ExtendedModelMap addAttribute(String attributeName, @Nullable Object attributeValue) {
        super.addAttribute(attributeName, attributeValue);
        return this;
    }

    @Override // org.springframework.ui.Model
    public ExtendedModelMap addAttribute(Object attributeValue) {
        super.addAttribute(attributeValue);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public ExtendedModelMap addAllAttributes(@Nullable Collection<?> attributeValues) {
        super.addAllAttributes(attributeValues);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public ExtendedModelMap addAllAttributes(@Nullable Map<String, ?> attributes) {
        super.addAllAttributes(attributes);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public ExtendedModelMap mergeAttributes(@Nullable Map<String, ?> attributes) {
        super.mergeAttributes(attributes);
        return this;
    }

    @Override // org.springframework.ui.Model
    public Map<String, Object> asMap() {
        return this;
    }
}
