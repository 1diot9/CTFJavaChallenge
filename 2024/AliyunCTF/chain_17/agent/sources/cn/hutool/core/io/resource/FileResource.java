package cn.hutool.core.io.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/FileResource.class */
public class FileResource implements Resource, Serializable {
    private static final long serialVersionUID = 1;
    private final File file;
    private final long lastModified;
    private final String name;

    public FileResource(String path) {
        this(FileUtil.file(path));
    }

    public FileResource(Path path) {
        this(path.toFile());
    }

    public FileResource(File file) {
        this(file, null);
    }

    public FileResource(File file, String fileName) {
        Assert.notNull(file, "File must be not null !", new Object[0]);
        this.file = file;
        this.lastModified = file.lastModified();
        file.getClass();
        this.name = (String) ObjectUtil.defaultIfNull(fileName, (Supplier<? extends String>) file::getName);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String getName() {
        return this.name;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public URL getUrl() {
        return URLUtil.getURL(this.file);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public InputStream getStream() throws NoResourceException {
        return FileUtil.getInputStream(this.file);
    }

    public File getFile() {
        return this.file;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public boolean isModified() {
        return this.lastModified != this.file.lastModified();
    }

    public String toString() {
        return this.file.toString();
    }
}
