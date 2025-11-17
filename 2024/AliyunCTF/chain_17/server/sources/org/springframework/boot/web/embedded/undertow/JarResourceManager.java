package org.springframework.boot.web.embedded.undertow;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.handlers.resource.URLResource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/JarResourceManager.class */
class JarResourceManager implements ResourceManager {
    private final String jarPath;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JarResourceManager(File jarFile) {
        try {
            this.jarPath = jarFile.getAbsoluteFile().toURI().toURL().toString();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public Resource getResource(String path) throws IOException {
        URL url = new URL("jar:" + this.jarPath + "!" + (path.startsWith("/") ? path : "/" + path));
        URLResource resource = new URLResource(url, path);
        if (StringUtils.hasText(path) && !"/".equals(path) && resource.getContentLength().longValue() < 0) {
            return null;
        }
        return resource;
    }

    public boolean isResourceChangeListenerSupported() {
        return false;
    }

    public void registerResourceChangeListener(ResourceChangeListener listener) {
        throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
    }

    public void removeResourceChangeListener(ResourceChangeListener listener) {
        throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
    }

    public void close() throws IOException {
    }
}
