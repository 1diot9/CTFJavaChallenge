package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/StringArrayPropertyEditor.class */
public class StringArrayPropertyEditor extends PropertyEditorSupport {
    public static final String DEFAULT_SEPARATOR = ",";
    private final String separator;

    @Nullable
    private final String charsToDelete;
    private final boolean emptyArrayAsNull;
    private final boolean trimValues;

    public StringArrayPropertyEditor() {
        this(",", (String) null, false);
    }

    public StringArrayPropertyEditor(String separator) {
        this(separator, (String) null, false);
    }

    public StringArrayPropertyEditor(String separator, boolean emptyArrayAsNull) {
        this(separator, (String) null, emptyArrayAsNull);
    }

    public StringArrayPropertyEditor(String separator, boolean emptyArrayAsNull, boolean trimValues) {
        this(separator, null, emptyArrayAsNull, trimValues);
    }

    public StringArrayPropertyEditor(String separator, @Nullable String charsToDelete, boolean emptyArrayAsNull) {
        this(separator, charsToDelete, emptyArrayAsNull, true);
    }

    public StringArrayPropertyEditor(String separator, @Nullable String charsToDelete, boolean emptyArrayAsNull, boolean trimValues) {
        this.separator = separator;
        this.charsToDelete = charsToDelete;
        this.emptyArrayAsNull = emptyArrayAsNull;
        this.trimValues = trimValues;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        String[] array = StringUtils.delimitedListToStringArray(text, this.separator, this.charsToDelete);
        if (this.emptyArrayAsNull && array.length == 0) {
            setValue(null);
            return;
        }
        if (this.trimValues) {
            array = StringUtils.trimArrayElements(array);
        }
        setValue(array);
    }

    public String getAsText() {
        return StringUtils.arrayToDelimitedString(ObjectUtils.toObjectArray(getValue()), this.separator);
    }
}
