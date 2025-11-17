package org.springframework.core.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/ByteArrayResource.class */
public class ByteArrayResource extends AbstractResource {
    private final byte[] byteArray;
    private final String description;

    public ByteArrayResource(byte[] byteArray) {
        this(byteArray, "resource loaded from byte array");
    }

    public ByteArrayResource(byte[] byteArray, @Nullable String description) {
        Assert.notNull(byteArray, "Byte array must not be null");
        this.byteArray = byteArray;
        this.description = description != null ? description : "";
    }

    public final byte[] getByteArray() {
        return this.byteArray;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() {
        return this.byteArray.length;
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.byteArray);
    }

    @Override // org.springframework.core.io.Resource
    public byte[] getContentAsByteArray() throws IOException {
        int length = this.byteArray.length;
        byte[] result = new byte[length];
        System.arraycopy(this.byteArray, 0, result, 0, length);
        return result;
    }

    @Override // org.springframework.core.io.Resource
    public String getContentAsString(Charset charset) throws IOException {
        return new String(this.byteArray, charset);
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "Byte array resource [" + this.description + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof ByteArrayResource) {
                ByteArrayResource that = (ByteArrayResource) other;
                if (Arrays.equals(this.byteArray, that.byteArray)) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return Arrays.hashCode(this.byteArray);
    }
}
