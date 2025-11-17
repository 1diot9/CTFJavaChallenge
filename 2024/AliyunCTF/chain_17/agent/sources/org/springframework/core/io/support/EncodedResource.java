package org.springframework.core.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/support/EncodedResource.class */
public class EncodedResource implements InputStreamSource {
    private final Resource resource;

    @Nullable
    private final String encoding;

    @Nullable
    private final Charset charset;

    public EncodedResource(Resource resource) {
        this(resource, null, null);
    }

    public EncodedResource(Resource resource, @Nullable String encoding) {
        this(resource, encoding, null);
    }

    public EncodedResource(Resource resource, @Nullable Charset charset) {
        this(resource, null, charset);
    }

    private EncodedResource(Resource resource, @Nullable String encoding, @Nullable Charset charset) {
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.encoding = encoding;
        this.charset = charset;
    }

    public final Resource getResource() {
        return this.resource;
    }

    @Nullable
    public final String getEncoding() {
        return this.encoding;
    }

    @Nullable
    public final Charset getCharset() {
        return this.charset;
    }

    public boolean requiresReader() {
        return (this.encoding == null && this.charset == null) ? false : true;
    }

    public Reader getReader() throws IOException {
        if (this.charset != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.charset);
        }
        if (this.encoding != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.encoding);
        }
        return new InputStreamReader(this.resource.getInputStream());
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }

    public String getContentAsString() throws IOException {
        Charset charset;
        if (this.charset != null) {
            charset = this.charset;
        } else if (this.encoding != null) {
            charset = Charset.forName(this.encoding);
        } else {
            charset = Charset.defaultCharset();
        }
        return this.resource.getContentAsString(charset);
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof EncodedResource) {
                EncodedResource that = (EncodedResource) other;
                if (!this.resource.equals(that.resource) || !ObjectUtils.nullSafeEquals(this.charset, that.charset) || !ObjectUtils.nullSafeEquals(this.encoding, that.encoding)) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.resource.hashCode();
    }

    public String toString() {
        return this.resource.toString();
    }
}
