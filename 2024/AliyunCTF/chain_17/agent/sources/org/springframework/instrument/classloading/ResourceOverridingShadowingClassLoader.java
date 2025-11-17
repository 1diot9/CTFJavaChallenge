package org.springframework.instrument.classloading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/ResourceOverridingShadowingClassLoader.class */
public class ResourceOverridingShadowingClassLoader extends ShadowingClassLoader {
    private static final Enumeration<URL> EMPTY_URL_ENUMERATION = new Enumeration<URL>() { // from class: org.springframework.instrument.classloading.ResourceOverridingShadowingClassLoader.1
        @Override // java.util.Enumeration
        public boolean hasMoreElements() {
            return false;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Enumeration
        public URL nextElement() {
            throw new UnsupportedOperationException("Should not be called. I am empty.");
        }
    };
    private final Map<String, String> overrides;

    public ResourceOverridingShadowingClassLoader(ClassLoader enclosingClassLoader) {
        super(enclosingClassLoader);
        this.overrides = new HashMap();
    }

    public void override(String oldPath, String newPath) {
        this.overrides.put(oldPath, newPath);
    }

    public void suppress(String oldPath) {
        this.overrides.put(oldPath, null);
    }

    public void copyOverrides(ResourceOverridingShadowingClassLoader other) {
        Assert.notNull(other, "Other ClassLoader must not be null");
        this.overrides.putAll(other.overrides);
    }

    @Override // org.springframework.instrument.classloading.ShadowingClassLoader, java.lang.ClassLoader
    public URL getResource(String requestedPath) {
        if (this.overrides.containsKey(requestedPath)) {
            String overriddenPath = this.overrides.get(requestedPath);
            if (overriddenPath != null) {
                return super.getResource(overriddenPath);
            }
            return null;
        }
        return super.getResource(requestedPath);
    }

    @Override // org.springframework.instrument.classloading.ShadowingClassLoader, java.lang.ClassLoader
    @Nullable
    public InputStream getResourceAsStream(String requestedPath) {
        if (this.overrides.containsKey(requestedPath)) {
            String overriddenPath = this.overrides.get(requestedPath);
            if (overriddenPath != null) {
                return super.getResourceAsStream(overriddenPath);
            }
            return null;
        }
        return super.getResourceAsStream(requestedPath);
    }

    @Override // org.springframework.instrument.classloading.ShadowingClassLoader, java.lang.ClassLoader
    public Enumeration<URL> getResources(String requestedPath) throws IOException {
        if (this.overrides.containsKey(requestedPath)) {
            String overriddenLocation = this.overrides.get(requestedPath);
            return overriddenLocation != null ? super.getResources(overriddenLocation) : EMPTY_URL_ENUMERATION;
        }
        return super.getResources(requestedPath);
    }
}
