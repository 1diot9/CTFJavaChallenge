package org.springframework.web.servlet.tags.form;

import java.beans.PropertyEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.HtmlUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/tags/form/ValueFormatter.class */
abstract class ValueFormatter {
    ValueFormatter() {
    }

    public static String getDisplayString(@Nullable Object value, boolean htmlEscape) {
        String displayValue = ObjectUtils.getDisplayString(value);
        return htmlEscape ? HtmlUtils.htmlEscape(displayValue) : displayValue;
    }

    public static String getDisplayString(@Nullable Object value, @Nullable PropertyEditor propertyEditor, boolean htmlEscape) {
        if (propertyEditor != null && !(value instanceof String)) {
            try {
                propertyEditor.setValue(value);
                String text = propertyEditor.getAsText();
                if (text != null) {
                    return getDisplayString(text, htmlEscape);
                }
            } catch (Throwable th) {
            }
        }
        return getDisplayString(value, htmlEscape);
    }
}
