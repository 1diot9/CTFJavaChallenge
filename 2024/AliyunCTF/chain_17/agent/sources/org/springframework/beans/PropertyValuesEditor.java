package org.springframework.beans;

import java.beans.PropertyEditorSupport;
import java.util.Properties;
import org.springframework.beans.propertyeditors.PropertiesEditor;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/PropertyValuesEditor.class */
public class PropertyValuesEditor extends PropertyEditorSupport {
    private final PropertiesEditor propertiesEditor = new PropertiesEditor();

    public void setAsText(String text) throws IllegalArgumentException {
        this.propertiesEditor.setAsText(text);
        Properties props = (Properties) this.propertiesEditor.getValue();
        setValue(new MutablePropertyValues(props));
    }
}
