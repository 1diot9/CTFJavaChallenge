package org.springframework.format.support;

import java.beans.PropertyEditorSupport;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.Formatter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/format/support/FormatterPropertyEditorAdapter.class */
public class FormatterPropertyEditorAdapter extends PropertyEditorSupport {
    private final Formatter<Object> formatter;

    public FormatterPropertyEditorAdapter(Formatter<?> formatter) {
        Assert.notNull(formatter, "Formatter must not be null");
        this.formatter = formatter;
    }

    public Class<?> getFieldType() {
        return FormattingConversionService.getFieldType(this.formatter);
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            try {
                setValue(this.formatter.parse(text, LocaleContextHolder.getLocale()));
                return;
            } catch (IllegalArgumentException ex) {
                throw ex;
            } catch (Throwable ex2) {
                throw new IllegalArgumentException("Parse attempt failed for value [" + text + "]", ex2);
            }
        }
        setValue(null);
    }

    public String getAsText() {
        Object value = getValue();
        return value != null ? this.formatter.print(value, LocaleContextHolder.getLocale()) : "";
    }
}
