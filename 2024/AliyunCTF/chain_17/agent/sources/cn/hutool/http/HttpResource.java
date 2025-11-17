package cn.hutool.http;

import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/HttpResource.class */
public class HttpResource implements Resource, Serializable {
    private static final long serialVersionUID = 1;
    private final Resource resource;
    private final String contentType;

    public HttpResource(Resource resource, String contentType) {
        this.resource = (Resource) Assert.notNull(resource, "Resource must be not null !", new Object[0]);
        this.contentType = contentType;
    }

    @Override // cn.hutool.core.io.resource.Resource
    public String getName() {
        return this.resource.getName();
    }

    @Override // cn.hutool.core.io.resource.Resource
    public URL getUrl() {
        return this.resource.getUrl();
    }

    @Override // cn.hutool.core.io.resource.Resource
    public InputStream getStream() {
        return this.resource.getStream();
    }

    public String getContentType() {
        return this.contentType;
    }
}
