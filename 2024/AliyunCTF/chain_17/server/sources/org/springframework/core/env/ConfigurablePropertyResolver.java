package org.springframework.core.env;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/env/ConfigurablePropertyResolver.class */
public interface ConfigurablePropertyResolver extends PropertyResolver {
    ConfigurableConversionService getConversionService();

    void setConversionService(ConfigurableConversionService conversionService);

    void setPlaceholderPrefix(String placeholderPrefix);

    void setPlaceholderSuffix(String placeholderSuffix);

    void setValueSeparator(@Nullable String valueSeparator);

    void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

    void setRequiredProperties(String... requiredProperties);

    void validateRequiredProperties() throws MissingRequiredPropertiesException;
}
