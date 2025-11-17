package org.springframework.boot.origin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/origin/OriginTrackedResource.class */
public class OriginTrackedResource implements Resource, OriginProvider {
    private final Resource resource;
    private final Origin origin;

    OriginTrackedResource(Resource resource, Origin origin) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.origin = origin;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        return getResource().getInputStream();
    }

    @Override // org.springframework.core.io.Resource
    public boolean exists() {
        return getResource().exists();
    }

    @Override // org.springframework.core.io.Resource
    public boolean isReadable() {
        return getResource().isReadable();
    }

    @Override // org.springframework.core.io.Resource
    public boolean isOpen() {
        return getResource().isOpen();
    }

    @Override // org.springframework.core.io.Resource
    public boolean isFile() {
        return getResource().isFile();
    }

    @Override // org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        return getResource().getURL();
    }

    @Override // org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        return getResource().getURI();
    }

    @Override // org.springframework.core.io.Resource
    public File getFile() throws IOException {
        return getResource().getFile();
    }

    @Override // org.springframework.core.io.Resource
    public ReadableByteChannel readableChannel() throws IOException {
        return getResource().readableChannel();
    }

    @Override // org.springframework.core.io.Resource
    public long contentLength() throws IOException {
        return getResource().contentLength();
    }

    @Override // org.springframework.core.io.Resource
    public long lastModified() throws IOException {
        return getResource().lastModified();
    }

    @Override // org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws IOException {
        return getResource().createRelative(relativePath);
    }

    @Override // org.springframework.core.io.Resource
    public String getFilename() {
        return getResource().getFilename();
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return getResource().getDescription();
    }

    public Resource getResource() {
        return this.resource;
    }

    @Override // org.springframework.boot.origin.OriginProvider
    public Origin getOrigin() {
        return this.origin;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OriginTrackedResource other = (OriginTrackedResource) obj;
        return this.resource.equals(other) && ObjectUtils.nullSafeEquals(this.origin, other.origin);
    }

    public int hashCode() {
        int result = this.resource.hashCode();
        return (31 * result) + ObjectUtils.nullSafeHashCode(this.origin);
    }

    public String toString() {
        return this.resource.toString();
    }

    public static OriginTrackedWritableResource of(WritableResource resource, Origin origin) {
        return (OriginTrackedWritableResource) of((Resource) resource, origin);
    }

    public static OriginTrackedResource of(Resource resource, Origin origin) {
        if (resource instanceof WritableResource) {
            WritableResource writableResource = (WritableResource) resource;
            return new OriginTrackedWritableResource(writableResource, origin);
        }
        return new OriginTrackedResource(resource, origin);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/origin/OriginTrackedResource$OriginTrackedWritableResource.class */
    public static class OriginTrackedWritableResource extends OriginTrackedResource implements WritableResource {
        OriginTrackedWritableResource(WritableResource resource, Origin origin) {
            super(resource, origin);
        }

        @Override // org.springframework.boot.origin.OriginTrackedResource
        public WritableResource getResource() {
            return (WritableResource) super.getResource();
        }

        @Override // org.springframework.core.io.WritableResource
        public OutputStream getOutputStream() throws IOException {
            return getResource().getOutputStream();
        }
    }
}
