package cn.hutool.core.io.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/io/resource/UrlResource.class */
public class UrlResource implements Resource, Serializable {
    private static final long serialVersionUID = 1;
    protected URL url;
    private long lastModified;
    protected String name;

    public UrlResource(URI uri) {
        this(URLUtil.url(uri), null);
    }

    public UrlResource(URL url) {
        this(url, null);
    }

    public UrlResource(URL url, String name) {
        this.lastModified = 0L;
        this.url = url;
        if (null != url && "file".equals(url.getProtocol())) {
            this.lastModified = FileUtil.file(url).lastModified();
        }
        this.name = (String) ObjectUtil.defaultIfNull(name, (Supplier<? extends String>) () -> {
            if (null != url) {
                return FileUtil.getName(url.getPath());
            }
            return null;
        });
    }

    @Deprecated
    public UrlResource(File file) {
        this.lastModified = 0L;
        this.url = URLUtil.getURL(file);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String getName() {
        return this.name;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public URL getUrl() {
        return this.url;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public InputStream getStream() throws NoResourceException {
        if (null == this.url) {
            throw new NoResourceException("Resource URL is null!");
        }
        return URLUtil.getStream(this.url);
    }

    @Override // cn.hutool.core.io.resource.Resource
    public boolean isModified() {
        return (0 == this.lastModified || this.lastModified == getFile().lastModified()) ? false : true;
    }

    public File getFile() {
        return FileUtil.file(this.url);
    }

    public String toString() {
        return null == this.url ? "null" : this.url.toString();
    }
}
