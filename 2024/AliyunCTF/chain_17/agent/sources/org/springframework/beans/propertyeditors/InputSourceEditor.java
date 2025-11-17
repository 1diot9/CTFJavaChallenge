package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;
import org.xml.sax.InputSource;

/* loaded from: agent.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/InputSourceEditor.class */
public class InputSourceEditor extends PropertyEditorSupport {
    private final ResourceEditor resourceEditor;

    public InputSourceEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    public InputSourceEditor(ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        InputSource inputSource;
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource) this.resourceEditor.getValue();
        if (resource != null) {
            try {
                inputSource = new InputSource(resource.getURL().toString());
            } catch (IOException ex) {
                throw new IllegalArgumentException("Could not retrieve URL for " + resource + ": " + ex.getMessage());
            }
        } else {
            inputSource = null;
        }
        setValue(inputSource);
    }

    public String getAsText() {
        InputSource value = (InputSource) getValue();
        return value != null ? value.getSystemId() : "";
    }
}
