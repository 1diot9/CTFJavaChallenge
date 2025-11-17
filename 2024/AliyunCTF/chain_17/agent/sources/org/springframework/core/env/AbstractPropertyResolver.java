package org.springframework.core.env;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.PropertyPlaceholderHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/AbstractPropertyResolver.class */
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {

    @Nullable
    private volatile ConfigurableConversionService conversionService;

    @Nullable
    private PropertyPlaceholderHelper nonStrictHelper;

    @Nullable
    private PropertyPlaceholderHelper strictHelper;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean ignoreUnresolvableNestedPlaceholders = false;
    private String placeholderPrefix = "${";
    private String placeholderSuffix = "}";

    @Nullable
    private String valueSeparator = ":";
    private final Set<String> requiredProperties = new LinkedHashSet();

    @Nullable
    protected abstract String getPropertyAsRawString(String key);

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public ConfigurableConversionService getConversionService() {
        ConfigurableConversionService cs = this.conversionService;
        if (cs == null) {
            synchronized (this) {
                cs = this.conversionService;
                if (cs == null) {
                    cs = new DefaultConversionService();
                    this.conversionService = cs;
                }
            }
        }
        return cs;
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setConversionService(ConfigurableConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setPlaceholderPrefix(String placeholderPrefix) {
        Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
        this.placeholderPrefix = placeholderPrefix;
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setPlaceholderSuffix(String placeholderSuffix) {
        Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
        this.placeholderSuffix = placeholderSuffix;
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setValueSeparator(@Nullable String valueSeparator) {
        this.valueSeparator = valueSeparator;
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
        this.ignoreUnresolvableNestedPlaceholders = ignoreUnresolvableNestedPlaceholders;
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void setRequiredProperties(String... requiredProperties) {
        Collections.addAll(this.requiredProperties, requiredProperties);
    }

    @Override // org.springframework.core.env.ConfigurablePropertyResolver
    public void validateRequiredProperties() {
        MissingRequiredPropertiesException ex = new MissingRequiredPropertiesException();
        for (String key : this.requiredProperties) {
            if (getProperty(key) == null) {
                ex.addMissingRequiredProperty(key);
            }
        }
        if (!ex.getMissingRequiredProperties().isEmpty()) {
            throw ex;
        }
    }

    @Override // org.springframework.core.env.PropertyResolver
    public boolean containsProperty(String key) {
        return getProperty(key) != null;
    }

    @Override // org.springframework.core.env.PropertyResolver
    @Nullable
    public String getProperty(String key) {
        return (String) getProperty(key, String.class);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    @Override // org.springframework.core.env.PropertyResolver
    public <T> T getProperty(String str, Class<T> cls, T t) {
        T t2 = (T) getProperty(str, cls);
        return t2 != null ? t2 : t;
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String getRequiredProperty(String key) throws IllegalStateException {
        String value = getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Required key '" + key + "' not found");
        }
        return value;
    }

    @Override // org.springframework.core.env.PropertyResolver
    public <T> T getRequiredProperty(String str, Class<T> cls) throws IllegalStateException {
        T t = (T) getProperty(str, cls);
        if (t == null) {
            throw new IllegalStateException("Required key '" + str + "' not found");
        }
        return t;
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String resolvePlaceholders(String text) {
        if (this.nonStrictHelper == null) {
            this.nonStrictHelper = createPlaceholderHelper(true);
        }
        return doResolvePlaceholders(text, this.nonStrictHelper);
    }

    @Override // org.springframework.core.env.PropertyResolver
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = createPlaceholderHelper(false);
        }
        return doResolvePlaceholders(text, this.strictHelper);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String resolveNestedPlaceholders(String value) {
        if (value.isEmpty()) {
            return value;
        }
        return this.ignoreUnresolvableNestedPlaceholders ? resolvePlaceholders(value) : resolveRequiredPlaceholders(value);
    }

    private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix, this.valueSeparator, ignoreUnresolvablePlaceholders);
    }

    private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
        return helper.replacePlaceholders(text, this::getPropertyAsRawString);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v8, types: [org.springframework.core.convert.ConversionService] */
    /* JADX WARN: Type inference failed for: r5v0, types: [T, java.lang.Object] */
    @Nullable
    public <T> T convertValueIfNecessary(Object obj, @Nullable Class<T> cls) {
        if (cls == null) {
            return obj;
        }
        ConfigurableConversionService configurableConversionService = this.conversionService;
        if (configurableConversionService == null) {
            if (ClassUtils.isAssignableValue(cls, obj)) {
                return obj;
            }
            configurableConversionService = DefaultConversionService.getSharedInstance();
        }
        return (T) configurableConversionService.convert((Object) obj, cls);
    }
}
