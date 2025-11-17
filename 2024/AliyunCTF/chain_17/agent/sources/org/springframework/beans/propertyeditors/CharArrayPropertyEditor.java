package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/CharArrayPropertyEditor.class */
public class CharArrayPropertyEditor extends PropertyEditorSupport {
    public void setAsText(@Nullable String text) {
        setValue(text != null ? text.toCharArray() : null);
    }

    public String getAsText() {
        char[] value = (char[]) getValue();
        return value != null ? new String(value) : "";
    }
}
