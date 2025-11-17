package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CustomDateEditor.class */
public class CustomDateEditor extends PropertyEditorSupport {
    private final DateFormat dateFormat;
    private final boolean allowEmpty;
    private final int exactDateLength;

    public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty) {
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.exactDateLength = -1;
    }

    public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
        this.dateFormat = dateFormat;
        this.allowEmpty = allowEmpty;
        this.exactDateLength = exactDateLength;
    }

    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        if (this.allowEmpty && !StringUtils.hasText(text)) {
            setValue(null);
            return;
        }
        if (text != null && this.exactDateLength >= 0 && text.length() != this.exactDateLength) {
            throw new IllegalArgumentException("Could not parse date: it is not exactly" + this.exactDateLength + "characters long");
        }
        try {
            setValue(this.dateFormat.parse(text));
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
        }
    }

    public String getAsText() {
        Date value = (Date) getValue();
        return value != null ? this.dateFormat.format(value) : "";
    }
}
