package org.springframework.aot.nativex.feature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/nativex/feature/ThrowawayClassLoader.class */
class ThrowawayClassLoader extends ClassLoader {
    private final ClassLoader resourceLoader;

    static {
        registerAsParallelCapable();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ThrowawayClassLoader(ClassLoader parent) {
        super(parent.getParent());
        this.resourceLoader = parent;
    }

    @Override // java.lang.ClassLoader
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                return loaded;
            }
            try {
                return super.loadClass(name, true);
            } catch (ClassNotFoundException e) {
                return loadClassFromResource(name);
            }
        }
    }

    private Class<?> loadClassFromResource(String name) throws ClassNotFoundException, ClassFormatError {
        String resourceName = name.replace('.', '/') + ".class";
        InputStream inputStream = this.resourceLoader.getResourceAsStream(resourceName);
        if (inputStream == null) {
            return null;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            inputStream.transferTo(outputStream);
            byte[] bytes = outputStream.toByteArray();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
        }
    }

    @Override // java.lang.ClassLoader
    protected URL findResource(String name) {
        return this.resourceLoader.getResource(name);
    }
}
