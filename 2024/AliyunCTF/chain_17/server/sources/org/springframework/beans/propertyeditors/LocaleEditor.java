package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/LocaleEditor.class */
public class LocaleEditor extends PropertyEditorSupport {
    public void setAsText(String text) {
        setValue(StringUtils.parseLocaleString(text));
    }

    public String getAsText() {
        Object value = getValue();
        return value != null ? value.toString() : "";
    }
}
