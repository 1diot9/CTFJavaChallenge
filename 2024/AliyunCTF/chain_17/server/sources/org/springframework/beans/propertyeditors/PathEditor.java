package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/PathEditor.class */
public class PathEditor extends PropertyEditorSupport {
    private final ResourceEditor resourceEditor;

    public PathEditor() {
        this.resourceEditor = new ResourceEditor();
    }

    public PathEditor(ResourceEditor resourceEditor) {
        Assert.notNull(resourceEditor, "ResourceEditor must not be null");
        this.resourceEditor = resourceEditor;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        boolean nioPathCandidate = !text.startsWith("classpath:");
        if (nioPathCandidate && !text.startsWith("/")) {
            try {
                URI uri = ResourceUtils.toURI(text);
                String scheme = uri.getScheme();
                if (scheme != null) {
                    nioPathCandidate = scheme.length() == 1;
                    setValue(Paths.get(uri).normalize());
                    return;
                }
            } catch (URISyntaxException e) {
                nioPathCandidate = !text.startsWith(ResourceUtils.FILE_URL_PREFIX);
            } catch (FileSystemNotFoundException e2) {
            }
        }
        this.resourceEditor.setAsText(text);
        Resource resource = (Resource) this.resourceEditor.getValue();
        if (resource == null) {
            setValue(null);
            return;
        }
        if (nioPathCandidate && !resource.exists()) {
            setValue(Paths.get(text, new String[0]).normalize());
            return;
        }
        try {
            setValue(resource.getFile().toPath());
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not retrieve file for " + resource + ": " + ex.getMessage());
        }
    }

    public String getAsText() {
        Path value = (Path) getValue();
        return value != null ? value.toString() : "";
    }
}
