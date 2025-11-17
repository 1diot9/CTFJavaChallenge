package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.time.ZoneId;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/ZoneIdEditor.class */
public class ZoneIdEditor extends PropertyEditorSupport {
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            text = text.trim();
        }
        setValue(ZoneId.of(text));
    }

    public String getAsText() {
        ZoneId value = (ZoneId) getValue();
        return value != null ? value.getId() : "";
    }
}
