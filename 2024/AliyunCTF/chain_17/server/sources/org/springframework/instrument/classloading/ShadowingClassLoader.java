package org.springframework.instrument.classloading;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-context-6.1.3.jar:org/springframework/instrument/classloading/ShadowingClassLoader.class */
public class ShadowingClassLoader extends DecoratingClassLoader {
    public static final String[] DEFAULT_EXCLUDED_PACKAGES = {"java.", "javax.", "jdk.", "sun.", "oracle.", "com.sun.", "com.ibm.", "COM.ibm.", "org.w3c.", "org.xml.", "org.dom4j.", "org.eclipse", "org.aspectj.", "net.sf.cglib", "org.springframework.cglib", "org.apache.xerces.", "org.apache.commons.logging."};
    private final ClassLoader enclosingClassLoader;
    private final List<ClassFileTransformer> classFileTransformers;
    private final Map<String, Class<?>> classCache;

    public ShadowingClassLoader(ClassLoader enclosingClassLoader) {
        this(enclosingClassLoader, true);
    }

    public ShadowingClassLoader(ClassLoader enclosingClassLoader, boolean defaultExcludes) {
        this.classFileTransformers = new ArrayList(1);
        this.classCache = new HashMap();
        Assert.notNull(enclosingClassLoader, "Enclosing ClassLoader must not be null");
        this.enclosingClassLoader = enclosingClassLoader;
        if (defaultExcludes) {
            for (String excludedPackage : DEFAULT_EXCLUDED_PACKAGES) {
                excludePackage(excludedPackage);
            }
        }
    }

    public void addTransformer(ClassFileTransformer transformer) {
        Assert.notNull(transformer, "Transformer must not be null");
        this.classFileTransformers.add(transformer);
    }

    public void copyTransformers(ShadowingClassLoader other) {
        Assert.notNull(other, "Other ClassLoader must not be null");
        this.classFileTransformers.addAll(other.classFileTransformers);
    }

    @Override // java.lang.ClassLoader
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (shouldShadow(name)) {
            Class<?> cls = this.classCache.get(name);
            if (cls != null) {
                return cls;
            }
            return doLoadClass(name);
        }
        return this.enclosingClassLoader.loadClass(name);
    }

    private boolean shouldShadow(String className) {
        return (className.equals(getClass().getName()) || className.endsWith("ShadowingClassLoader") || !isEligibleForShadowing(className)) ? false : true;
    }

    protected boolean isEligibleForShadowing(String className) {
        return !isExcluded(className);
    }

    private Class<?> doLoadClass(String name) throws ClassNotFoundException {
        int packageSeparator;
        String internalName = StringUtils.replace(name, ".", "/") + ".class";
        InputStream is = this.enclosingClassLoader.getResourceAsStream(internalName);
        if (is == null) {
            throw new ClassNotFoundException(name);
        }
        try {
            byte[] bytes = applyTransformers(name, FileCopyUtils.copyToByteArray(is));
            Class<?> cls = defineClass(name, bytes, 0, bytes.length);
            if (cls.getPackage() == null && (packageSeparator = name.lastIndexOf(46)) != -1) {
                String packageName = name.substring(0, packageSeparator);
                definePackage(packageName, null, null, null, null, null, null, null);
            }
            this.classCache.put(name, cls);
            return cls;
        } catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
        }
    }

    private byte[] applyTransformers(String name, byte[] bytes) {
        String internalName = StringUtils.replace(name, ".", "/");
        try {
            for (ClassFileTransformer transformer : this.classFileTransformers) {
                byte[] transformed = transformer.transform(this, internalName, (Class) null, (ProtectionDomain) null, bytes);
                bytes = transformed != null ? transformed : bytes;
            }
            return bytes;
        } catch (IllegalClassFormatException ex) {
            throw new IllegalStateException((Throwable) ex);
        }
    }

    @Override // java.lang.ClassLoader
    public URL getResource(String name) {
        return this.enclosingClassLoader.getResource(name);
    }

    @Override // java.lang.ClassLoader
    @Nullable
    public InputStream getResourceAsStream(String name) {
        return this.enclosingClassLoader.getResourceAsStream(name);
    }

    @Override // java.lang.ClassLoader
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.enclosingClassLoader.getResources(name);
    }
}
