package org.springframework.boot.web.embedded.undertow;

import io.undertow.UndertowMessages;
import io.undertow.server.handlers.resource.Resource;
import io.undertow.server.handlers.resource.ResourceChangeListener;
import io.undertow.server.handlers.resource.ResourceManager;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/CompositeResourceManager.class */
class CompositeResourceManager implements ResourceManager {
    private final List<ResourceManager> resourceManagers;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompositeResourceManager(ResourceManager... resourceManagers) {
        this.resourceManagers = Arrays.asList(resourceManagers);
    }

    public void close() throws IOException {
        for (ResourceManager resourceManager : this.resourceManagers) {
            resourceManager.close();
        }
    }

    public Resource getResource(String path) throws IOException {
        for (ResourceManager resourceManager : this.resourceManagers) {
            Resource resource = resourceManager.getResource(path);
            if (resource != null) {
                return resource;
            }
        }
        return null;
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
}
