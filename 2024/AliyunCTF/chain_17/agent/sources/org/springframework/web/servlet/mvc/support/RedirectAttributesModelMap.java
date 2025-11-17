package org.springframework.web.servlet.mvc.support;

import java.util.Collection;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DataBinder;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/support/RedirectAttributesModelMap.class */
public class RedirectAttributesModelMap extends ModelMap implements RedirectAttributes {

    @Nullable
    private final DataBinder dataBinder;
    private final ModelMap flashAttributes;

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

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ RedirectAttributes mergeAttributes(@Nullable Map attributes) {
        return mergeAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ RedirectAttributes addAllAttributes(@Nullable Collection attributeValues) {
        return addAllAttributes((Collection<?>) attributeValues);
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model mergeAttributes(@Nullable Map attributes) {
        return mergeAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Map attributes) {
        return addAllAttributes((Map<String, ?>) attributes);
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes, org.springframework.ui.Model
    public /* bridge */ /* synthetic */ Model addAllAttributes(@Nullable Collection attributeValues) {
        return addAllAttributes((Collection<?>) attributeValues);
    }

    public RedirectAttributesModelMap() {
        this(null);
    }

    public RedirectAttributesModelMap(@Nullable DataBinder dataBinder) {
        this.flashAttributes = new ModelMap();
        this.dataBinder = dataBinder;
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes
    public Map<String, ?> getFlashAttributes() {
        return this.flashAttributes;
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes, org.springframework.ui.Model
    public RedirectAttributesModelMap addAttribute(String attributeName, @Nullable Object attributeValue) {
        super.addAttribute(attributeName, (Object) formatValue(attributeValue));
        return this;
    }

    @Nullable
    private String formatValue(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        return this.dataBinder != null ? (String) this.dataBinder.convertIfNecessary(value, String.class) : value.toString();
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes, org.springframework.ui.Model
    public RedirectAttributesModelMap addAttribute(Object attributeValue) {
        super.addAttribute(attributeValue);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public RedirectAttributesModelMap addAllAttributes(@Nullable Collection<?> attributeValues) {
        super.addAllAttributes(attributeValues);
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public RedirectAttributesModelMap addAllAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach(this::addAttribute);
        }
        return this;
    }

    @Override // org.springframework.ui.ModelMap, org.springframework.ui.Model
    public RedirectAttributesModelMap mergeAttributes(@Nullable Map<String, ?> attributes) {
        if (attributes != null) {
            attributes.forEach((key, attribute) -> {
                if (!containsKey(key)) {
                    addAttribute(key, attribute);
                }
            });
        }
        return this;
    }

    @Override // org.springframework.ui.Model
    public Map<String, Object> asMap() {
        return this;
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object put(String key, @Nullable Object value) {
        return super.put((Object) key, (Object) formatValue(value));
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void putAll(@Nullable Map<? extends String, ? extends Object> map) {
        if (map != null) {
            map.forEach((key, value) -> {
                put(key, (Object) formatValue(value));
            });
        }
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes
    public RedirectAttributes addFlashAttribute(String attributeName, @Nullable Object attributeValue) {
        this.flashAttributes.addAttribute(attributeName, attributeValue);
        return this;
    }

    @Override // org.springframework.web.servlet.mvc.support.RedirectAttributes
    public RedirectAttributes addFlashAttribute(Object attributeValue) {
        this.flashAttributes.addAttribute(attributeValue);
        return this;
    }
}
