package cn.hutool.http.body;

import cn.hutool.core.io.resource.Resource;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/http/body/ResourceBody.class */
public class ResourceBody implements RequestBody {
    private final Resource resource;

    public static ResourceBody create(Resource resource) {
        return new ResourceBody(resource);
    }

    public ResourceBody(Resource resource) {
        this.resource = resource;
    }

    @Override // cn.hutool.http.body.RequestBody
    public void write(OutputStream out) {
        if (null != this.resource) {
            this.resource.writeTo(out);
        }
    }
}
