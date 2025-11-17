package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/StringTrimmerEditor.class */
public class StringTrimmerEditor extends PropertyEditorSupport {

    @Nullable
    private final String charsToDelete;
    private final boolean emptyAsNull;

    public StringTrimmerEditor(boolean emptyAsNull) {
        this.charsToDelete = null;
        this.emptyAsNull = emptyAsNull;
    }

    public StringTrimmerEditor(String charsToDelete, boolean emptyAsNull) {
        this.charsToDelete = charsToDelete;
        this.emptyAsNull = emptyAsNull;
    }

    public void setAsText(@Nullable String text) {
        if (text == null) {
            setValue(null);
            return;
        }
        String value = text.trim();
        if (this.charsToDelete != null) {
            value = StringUtils.deleteAny(value, this.charsToDelete);
        }
        if (this.emptyAsNull && value.isEmpty()) {
            setValue(null);
        } else {
            setValue(value);
        }
    }

    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }
}
