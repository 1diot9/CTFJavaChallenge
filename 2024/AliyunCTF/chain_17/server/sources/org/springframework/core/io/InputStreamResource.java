package org.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/InputStreamResource.class */
public class InputStreamResource extends AbstractResource {
    private final InputStream inputStream;
    private final String description;
    private boolean read;

    public InputStreamResource(InputStream inputStream) {
        this(inputStream, "resource loaded through InputStream");
    }

    public InputStreamResource(InputStream inputStream, @Nullable String description) {
        this.read = false;
        Assert.notNull(inputStream, "InputStream must not be null");
        this.inputStream = inputStream;
        this.description = description != null ? description : "";
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isOpen() {
        return true;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException, IllegalStateException {
        if (this.read) {
            throw new IllegalStateException("InputStream has already been read - do not use InputStreamResource if a stream needs to be read multiple times");
        }
        this.read = true;
        return this.inputStream;
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "InputStream resource [" + this.description + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof InputStreamResource) {
                InputStreamResource that = (InputStreamResource) other;
                if (this.inputStream.equals(that.inputStream)) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.inputStream.hashCode();
    }
}
