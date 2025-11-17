package org.springframework.web.service.invoker;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.service.invoker.HttpRequestValues;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/AbstractNamedValueArgumentResolver.class */
public abstract class AbstractNamedValueArgumentResolver implements HttpServiceArgumentResolver {
    private static final TypeDescriptor STRING_TARGET_TYPE = TypeDescriptor.valueOf(String.class);
    protected final Log logger;

    @Nullable
    private final ConversionService conversionService;
    private final Map<MethodParameter, NamedValueInfo> namedValueInfoCache;

    @Nullable
    protected abstract NamedValueInfo createNamedValueInfo(MethodParameter parameter);

    protected abstract void addRequestValue(String name, Object value, MethodParameter parameter, HttpRequestValues.Builder requestValues);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractNamedValueArgumentResolver(ConversionService conversionService) {
        this.logger = LogFactory.getLog(getClass());
        this.namedValueInfoCache = new ConcurrentHashMap(256);
        Assert.notNull(conversionService, "ConversionService is required");
        this.conversionService = conversionService;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractNamedValueArgumentResolver() {
        this.logger = LogFactory.getLog(getClass());
        this.namedValueInfoCache = new ConcurrentHashMap(256);
        this.conversionService = null;
    }

    @Override // org.springframework.web.service.invoker.HttpServiceArgumentResolver
    public boolean resolve(@Nullable Object argument, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        NamedValueInfo info = getNamedValueInfo(parameter);
        if (info == null) {
            return false;
        }
        if (Map.class.isAssignableFrom(parameter.getParameterType())) {
            Assert.isInstanceOf(Map.class, argument);
            MethodParameter parameter2 = parameter.nested(1);
            for (Map.Entry<String, ?> entry : ((Map) (argument != null ? argument : Collections.emptyMap())).entrySet()) {
                addSingleOrMultipleValues(entry.getKey(), entry.getValue(), false, null, info.label, info.multiValued, parameter2, requestValues);
            }
            return true;
        }
        addSingleOrMultipleValues(info.name, argument, info.required, info.defaultValue, info.label, info.multiValued, parameter, requestValues);
        return true;
    }

    @Nullable
    private NamedValueInfo getNamedValueInfo(MethodParameter parameter) {
        NamedValueInfo info = this.namedValueInfoCache.get(parameter);
        if (info == null) {
            NamedValueInfo info2 = createNamedValueInfo(parameter);
            if (info2 == null) {
                return null;
            }
            info = updateNamedValueInfo(parameter, info2);
            this.namedValueInfoCache.put(parameter, info);
        }
        return info;
    }

    private NamedValueInfo updateNamedValueInfo(MethodParameter parameter, NamedValueInfo info) {
        String name = info.name;
        if (info.name.isEmpty()) {
            name = parameter.getParameterName();
            if (name == null) {
                throw new IllegalArgumentException("Name for argument of type [%s] not specified, and parameter name information not available via reflection. Ensure that the compiler uses the '-parameters' flag.".formatted(parameter.getNestedParameterType().getName()));
            }
        }
        boolean required = info.required && !parameter.getParameterType().equals(Optional.class);
        String defaultValue = ValueConstants.DEFAULT_NONE.equals(info.defaultValue) ? null : info.defaultValue;
        return info.update(name, required, defaultValue);
    }

    private void addSingleOrMultipleValues(String name, @Nullable Object value, boolean required, @Nullable Object defaultValue, String valueLabel, boolean supportsMultiValues, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        if (supportsMultiValues) {
            if (ObjectUtils.isArray(value)) {
                value = Arrays.asList((Object[]) value);
            }
            if (value instanceof Collection) {
                Collection<?> elements = (Collection) value;
                parameter = parameter.nested();
                boolean hasValues = false;
                for (Object element : elements) {
                    if (element != null) {
                        hasValues = true;
                        addSingleValue(name, element, false, null, valueLabel, parameter, requestValues);
                    }
                }
                if (hasValues) {
                    return;
                } else {
                    value = null;
                }
            }
        }
        addSingleValue(name, value, required, defaultValue, valueLabel, parameter, requestValues);
    }

    private void addSingleValue(String name, @Nullable Object value, boolean required, @Nullable Object defaultValue, String valueLabel, MethodParameter parameter, HttpRequestValues.Builder requestValues) {
        Object convert;
        if (value instanceof Optional) {
            Optional<?> optionalValue = (Optional) value;
            value = optionalValue.orElse(null);
        }
        if (value == null && defaultValue != null) {
            value = defaultValue;
        }
        if (this.conversionService != null && !(value instanceof String)) {
            parameter = parameter.nestedIfOptional();
            Class<?> type = parameter.getNestedParameterType();
            if (type != Object.class && !type.isArray()) {
                convert = this.conversionService.convert(value, new TypeDescriptor(parameter), STRING_TARGET_TYPE);
            } else {
                convert = this.conversionService.convert(value, (Class<Object>) String.class);
            }
            value = convert;
        }
        if (value == null) {
            Assert.isTrue(!required, (Supplier<String>) () -> {
                return "Missing " + valueLabel + " value '" + name + "'";
            });
            return;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Resolved " + valueLabel + " value '" + name + ":" + value + "'");
        }
        addRequestValue(name, value, parameter, requestValues);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/service/invoker/AbstractNamedValueArgumentResolver$NamedValueInfo.class */
    public static class NamedValueInfo {
        private final String name;
        private final boolean required;

        @Nullable
        private final String defaultValue;
        private final String label;
        private final boolean multiValued;

        public NamedValueInfo(String name, boolean required, @Nullable String defaultValue, String label, boolean multiValued) {
            this.name = name;
            this.required = required;
            this.defaultValue = defaultValue;
            this.label = label;
            this.multiValued = multiValued;
        }

        public NamedValueInfo update(String name, boolean required, @Nullable String defaultValue) {
            return new NamedValueInfo(name, required, defaultValue, this.label, this.multiValued);
        }
    }
}
