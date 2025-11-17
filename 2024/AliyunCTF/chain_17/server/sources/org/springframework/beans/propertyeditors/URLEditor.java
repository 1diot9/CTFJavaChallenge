package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URL;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/URLEditor.class */
public class URLEditor extends PropertyEditorSupport {
    private final ResourceEditor resourceEditor;

    public URLEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    public URLEditor(ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        URL url;
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource) this.resourceEditor.getValue();
        if (resource != null) {
            try {
                url = resource.getURL();
            } catch (IOException ex) {
                throw new IllegalArgumentException("Could not retrieve URL for " + resource + ": " + ex.getMessage());
            }
        } else {
            url = null;
        }
        setValue(url);
    }

    public String getAsText() {
        URL value = (URL) getValue();
        return value != null ? value.toExternalForm() : "";
    }
}
