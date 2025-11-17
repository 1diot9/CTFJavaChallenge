package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/PropertiesEditor.class */
public class PropertiesEditor extends PropertyEditorSupport {
    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        Properties props = new Properties();
        if (text != null) {
            try {
                props.load(new ByteArrayInputStream(text.getBytes(StandardCharsets.ISO_8859_1)));
            } catch (IOException ex) {
                throw new IllegalArgumentException("Failed to parse [" + text + "] into Properties", ex);
            }
        }
        setValue(props);
    }

    public void setValue(Object value) {
        if (!(value instanceof Properties) && (value instanceof Map)) {
            Map<?, ?> map = (Map) value;
            Properties props = new Properties();
            props.putAll(map);
            super.setValue(props);
            return;
        }
        super.setValue(value);
    }
}
