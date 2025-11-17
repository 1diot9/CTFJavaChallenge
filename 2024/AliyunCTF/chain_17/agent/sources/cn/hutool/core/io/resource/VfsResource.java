package cn.hutool.core.io.resource;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/VfsResource.class */
public class VfsResource implements Resource {
    private static final String VFS3_PKG = "org.jboss.vfs.";
    private static final Method VIRTUAL_FILE_METHOD_EXISTS;
    private static final Method VIRTUAL_FILE_METHOD_GET_INPUT_STREAM;
    private static final Method VIRTUAL_FILE_METHOD_GET_SIZE;
    private static final Method VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED;
    private static final Method VIRTUAL_FILE_METHOD_TO_URL;
    private static final Method VIRTUAL_FILE_METHOD_GET_NAME;
    private final Object virtualFile;
    private final long lastModified;

    static {
        Class<?> virtualFile = ClassLoaderUtil.loadClass("org.jboss.vfs.VirtualFile");
        try {
            VIRTUAL_FILE_METHOD_EXISTS = virtualFile.getMethod("exists", new Class[0]);
            VIRTUAL_FILE_METHOD_GET_INPUT_STREAM = virtualFile.getMethod("openStream", new Class[0]);
            VIRTUAL_FILE_METHOD_GET_SIZE = virtualFile.getMethod("getSize", new Class[0]);
            VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED = virtualFile.getMethod("getLastModified", new Class[0]);
            VIRTUAL_FILE_METHOD_TO_URL = virtualFile.getMethod("toURL", new Class[0]);
            VIRTUAL_FILE_METHOD_GET_NAME = virtualFile.getMethod("getName", new Class[0]);
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
        }
    }

    public VfsResource(Object resource) {
        Assert.notNull(resource, "VirtualFile must not be null", new Object[0]);
        this.virtualFile = resource;
        this.lastModified = getLastModified();
    }

    public boolean exists() {
        return ((Boolean) ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_EXISTS, new Object[0])).booleanValue();
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String getName() {
        return (String) ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_NAME, new Object[0]);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public URL getUrl() {
        return (URL) ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_TO_URL, new Object[0]);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public InputStream getStream() {
        return (InputStream) ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_INPUT_STREAM, new Object[0]);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public boolean isModified() {
        return this.lastModified != getLastModified();
    }

    public long getLastModified() {
        return ((Long) ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_LAST_MODIFIED, new Object[0])).longValue();
    }

    public long size() {
        return ((Long) ReflectUtil.invoke(this.virtualFile, VIRTUAL_FILE_METHOD_GET_SIZE, new Object[0])).longValue();
    }
}
