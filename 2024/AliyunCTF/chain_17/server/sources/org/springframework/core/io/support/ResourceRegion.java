package org.springframework.core.io.support;

import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/ResourceRegion.class */
public class ResourceRegion {
    private final Resource resource;
    private final long position;
    private final long count;

    public ResourceRegion(Resource resource, long position, long count) {
        Assert.notNull(resource, "Resource must not be null");
        Assert.isTrue(position >= 0, "'position' must be greater than or equal to 0");
        Assert.isTrue(count >= 0, "'count' must be greater than or equal to 0");
        this.resource = resource;
        this.position = position;
        this.count = count;
    }

    public Resource getResource() {
        return this.resource;
    }

    public long getPosition() {
        return this.position;
    }

    public long getCount() {
        return this.count;
    }
}
