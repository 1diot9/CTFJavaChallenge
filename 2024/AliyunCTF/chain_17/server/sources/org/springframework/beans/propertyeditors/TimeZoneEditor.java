package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.TimeZone;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/TimeZoneEditor.class */
public class TimeZoneEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            text = text.trim();
        }
        setValue(StringUtils.parseTimeZoneString(text));
    }

    public String getAsText() {
        TimeZone value = (TimeZone) getValue();
        return value != null ? value.getID() : "";
    }
}
