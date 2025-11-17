package org.springframework.core.convert.support;

import java.util.Set;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/convert/support/StringToBooleanConverter.class */
final class StringToBooleanConverter implements Converter<String, Boolean> {
    private static final Set<String> trueValues = Set.of("true", CustomBooleanEditor.VALUE_ON, CustomBooleanEditor.VALUE_YES, CustomBooleanEditor.VALUE_1);
    private static final Set<String> falseValues = Set.of("false", CustomBooleanEditor.VALUE_OFF, "no", CustomBooleanEditor.VALUE_0);

    @Override // org.springframework.core.convert.converter.Converter
    @Nullable
    public Boolean convert(String source) {
        String value = source.trim();
        if (value.isEmpty()) {
            return null;
        }
        String value2 = value.toLowerCase();
        if (trueValues.contains(value2)) {
            return Boolean.TRUE;
        }
        if (falseValues.contains(value2)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
    }
}
