package org.springframework.boot.loader.launch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.function.Supplier;
import java.util.jar.Manifest;
import org.springframework.boot.loader.net.protocol.jar.JarUrlClassLoader;

/* loaded from: agent.jar:org/springframework/boot/loader/launch/LaunchedClassLoader.class */
public class LaunchedClassLoader extends JarUrlClassLoader {
    private static final String JAR_MODE_PACKAGE_PREFIX = "org.springframework.boot.loader.jarmode.";
    private static final String JAR_MODE_RUNNER_CLASS_NAME = JarModeRunner.class.getName();
    private final boolean exploded;
    private final Archive rootArchive;
    private final Object definePackageLock;
    private volatile DefinePackageCallType definePackageCallType;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:org/springframework/boot/loader/launch/LaunchedClassLoader$DefinePackageCallType.class */
    public enum DefinePackageCallType {
        MANIFEST,
        ATTRIBUTES
    }

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public LaunchedClassLoader(boolean exploded, URL[] urls, ClassLoader parent) {
        this(exploded, null, urls, parent);
    }

    public LaunchedClassLoader(boolean exploded, Archive rootArchive, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.definePackageLock = new Object();
        this.exploded = exploded;
        this.rootArchive = rootArchive;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.loader.net.protocol.jar.JarUrlClassLoader, java.lang.ClassLoader
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith(JAR_MODE_PACKAGE_PREFIX) || name.equals(JAR_MODE_RUNNER_CLASS_NAME)) {
            try {
                Class<?> result = loadClassInLaunchedClassLoader(name);
                if (resolve) {
                    resolveClass(result);
                }
                return result;
            } catch (ClassNotFoundException e) {
            }
        }
        return super.loadClass(name, resolve);
    }

    private Class<?> loadClassInLaunchedClassLoader(String name) throws ClassNotFoundException {
        try {
            String internalName = name.replace('.', '/') + ".class";
            InputStream inputStream = getParent().getResourceAsStream(internalName);
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    if (inputStream == null) {
                        throw new ClassNotFoundException(name);
                    }
                    inputStream.transferTo(outputStream);
                    byte[] bytes = outputStream.toByteArray();
                    Class<?> definedClass = defineClass(name, bytes, 0, bytes.length);
                    definePackageIfNecessary(name);
                    outputStream.close();
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    return definedClass;
                } catch (Throwable th) {
                    try {
                        outputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } finally {
            }
        } catch (IOException ex) {
            throw new ClassNotFoundException("Cannot load resource for class [" + name + "]", ex);
        }
    }

    @Override // java.net.URLClassLoader
    protected Package definePackage(String name, Manifest man, URL url) throws IllegalArgumentException {
        return !this.exploded ? super.definePackage(name, man, url) : definePackageForExploded(name, man, url);
    }

    private Package definePackageForExploded(String name, Manifest man, URL url) {
        Package r0;
        synchronized (this.definePackageLock) {
            r0 = (Package) definePackage(DefinePackageCallType.MANIFEST, () -> {
                return super.definePackage(name, man, url);
            });
        }
        return r0;
    }

    @Override // java.lang.ClassLoader
    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
        if (!this.exploded) {
            return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
        }
        return definePackageForExploded(name, sealBase, () -> {
            return super.definePackage(name, specTitle, specVersion, specVendor, implTitle, implVersion, implVendor, sealBase);
        });
    }

    private Package definePackageForExploded(String name, URL sealBase, Supplier<Package> call) {
        Manifest manifest;
        synchronized (this.definePackageLock) {
            if (this.definePackageCallType == null && (manifest = getManifest(this.rootArchive)) != null) {
                return definePackage(name, manifest, sealBase);
            }
            return (Package) definePackage(DefinePackageCallType.ATTRIBUTES, call);
        }
    }

    private <T> T definePackage(DefinePackageCallType type, Supplier<T> call) {
        DefinePackageCallType existingType = this.definePackageCallType;
        try {
            this.definePackageCallType = type;
            T t = call.get();
            this.definePackageCallType = existingType;
            return t;
        } catch (Throwable th) {
            this.definePackageCallType = existingType;
            throw th;
        }
    }

    private Manifest getManifest(Archive archive) {
        if (archive == null) {
            return null;
        }
        try {
            return archive.getManifest();
        } catch (IOException e) {
            return null;
        }
    }
}
