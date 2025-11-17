package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-beans-6.1.3.jar:org/springframework/beans/propertyeditors/URIEditor.class */
public class URIEditor extends PropertyEditorSupport {

    @Nullable
    private final ClassLoader classLoader;
    private final boolean encode;

    public URIEditor() {
        this(true);
    }

    public URIEditor(boolean encode) {
        this.classLoader = null;
        this.encode = encode;
    }

    public URIEditor(@Nullable ClassLoader classLoader) {
        this(classLoader, true);
    }

    public URIEditor(@Nullable ClassLoader classLoader, boolean encode) {
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
        this.encode = encode;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            String uri = text.trim();
            if (this.classLoader != null && uri.startsWith("classpath:")) {
                ClassPathResource resource = new ClassPathResource(uri.substring("classpath:".length()), this.classLoader);
                try {
                    setValue(resource.getURI());
                    return;
                } catch (IOException ex) {
                    throw new IllegalArgumentException("Could not retrieve URI for " + resource + ": " + ex.getMessage());
                }
            }
            try {
                setValue(createURI(uri));
                return;
            } catch (URISyntaxException ex2) {
                throw new IllegalArgumentException("Invalid URI syntax: " + ex2.getMessage());
            }
        }
        setValue(null);
    }

    protected URI createURI(String value) throws URISyntaxException {
        int colonIndex = value.indexOf(58);
        if (this.encode && colonIndex != -1) {
            int fragmentIndex = value.indexOf(35, colonIndex + 1);
            String scheme = value.substring(0, colonIndex);
            String ssp = value.substring(colonIndex + 1, fragmentIndex > 0 ? fragmentIndex : value.length());
            String fragment = fragmentIndex > 0 ? value.substring(fragmentIndex + 1) : null;
            return new URI(scheme, ssp, fragment);
        }
        return new URI(value);
    }

    public String getAsText() {
        URI value = (URI) getValue();
        return value != null ? value.toString() : "";
    }
}
