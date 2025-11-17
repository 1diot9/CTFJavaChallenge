package org.springframework.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/ClassPathResource.class */
public class ClassPathResource extends AbstractFileResolvingResource {
    private final String path;
    private final String absolutePath;

    @Nullable
    private final ClassLoader classLoader;

    @Nullable
    private final Class<?> clazz;

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    public ClassPathResource(String path, @Nullable ClassLoader classLoader) {
        Assert.notNull(path, "Path must not be null");
        String pathToUse = StringUtils.cleanPath(path);
        pathToUse = pathToUse.startsWith("/") ? pathToUse.substring(1) : pathToUse;
        this.path = pathToUse;
        this.absolutePath = pathToUse;
        this.classLoader = classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader();
        this.clazz = null;
    }

    public ClassPathResource(String path, @Nullable Class<?> clazz) {
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        String absolutePath = this.path;
        if (clazz != null && !absolutePath.startsWith("/")) {
            absolutePath = ClassUtils.classPackageAsResourcePath(clazz) + "/" + absolutePath;
        } else if (absolutePath.startsWith("/")) {
            absolutePath = absolutePath.substring(1);
        }
        this.absolutePath = absolutePath;
        this.classLoader = null;
        this.clazz = clazz;
    }

    public final String getPath() {
        return this.absolutePath;
    }

    @Nullable
    public final ClassLoader getClassLoader() {
        return this.clazz != null ? this.clazz.getClassLoader() : this.classLoader;
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return resolveURL() != null;
    }

    @Override // org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        URL url = resolveURL();
        return url != null && checkReadable(url);
    }

    @Nullable
    protected URL resolveURL() {
        try {
            if (this.clazz != null) {
                return this.clazz.getResource(this.path);
            }
            if (this.classLoader != null) {
                return this.classLoader.getResource(this.absolutePath);
            }
            return ClassLoader.getSystemResource(this.absolutePath);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            is = this.clazz.getResourceAsStream(this.path);
        } else if (this.classLoader != null) {
            is = this.classLoader.getResourceAsStream(this.absolutePath);
        } else {
            is = ClassLoader.getSystemResourceAsStream(this.absolutePath);
        }
        if (is == null) {
            throw new FileNotFoundException(getDescription() + " cannot be opened because it does not exist");
        }
        return is;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        URL url = resolveURL();
        if (url == null) {
            throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return this.clazz != null ? new ClassPathResource(pathToUse, this.clazz) : new ClassPathResource(pathToUse, this.classLoader);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        return StringUtils.getFilename(this.absolutePath);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "class path resource [" + this.absolutePath + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ClassPathResource) {
                ClassPathResource that = (ClassPathResource) other;
                if (!this.absolutePath.equals(that.absolutePath) || !ObjectUtils.nullSafeEquals(getClassLoader(), that.getClassLoader())) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.absolutePath.hashCode();
    }
}
