package org.springframework.expression.spel.support;

import java.util.function.Supplier;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.expression.TypeConverter;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelMessage;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-expression-6.1.3.jar:org/springframework/expression/spel/support/StandardTypeConverter.class */
public class StandardTypeConverter implements TypeConverter {
    private final Supplier<ConversionService> conversionService;

    public StandardTypeConverter() {
        this.conversionService = DefaultConversionService::getSharedInstance;
    }

    public StandardTypeConverter(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = () -> {
            return conversionService;
        };
    }

    public StandardTypeConverter(Supplier<ConversionService> conversionService) {
        Assert.notNull(conversionService, "Supplier must not be null");
        this.conversionService = conversionService;
    }

    @Override // org.springframework.expression.TypeConverter
    public boolean canConvert(@Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.get().canConvert(sourceType, targetType);
    }

    @Override // org.springframework.expression.TypeConverter
    @Nullable
    public Object convertValue(@Nullable Object value, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            return this.conversionService.get().convert(value, sourceType, targetType);
        } catch (ConversionException ex) {
            SpelMessage spelMessage = SpelMessage.TYPE_CONVERSION_ERROR;
            Object[] objArr = new Object[2];
            objArr[0] = sourceType != null ? sourceType.toString() : value != null ? value.getClass().getName() : "null";
            objArr[1] = targetType.toString();
            throw new SpelEvaluationException(ex, spelMessage, objArr);
        }
    }
}
