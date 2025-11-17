package org.springframework.jndi;

import java.beans.PropertyEditorSupport;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/jndi/JndiTemplateEditor.class */
public class JndiTemplateEditor extends PropertyEditorSupport {
    private final PropertiesEditor propertiesEditor = new PropertiesEditor();

    public void setAsText(@Nullable String text) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException("JndiTemplate cannot be created from null string");
        }
        if (text.isEmpty()) {
            setValue(new JndiTemplate());
            return;
        }
        this.propertiesEditor.setAsText(text);
        Properties props = (Properties) this.propertiesEditor.getValue();
        setValue(new JndiTemplate(props));
    }
}
