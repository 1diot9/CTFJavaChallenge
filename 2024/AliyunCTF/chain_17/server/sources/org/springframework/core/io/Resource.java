package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/Resource.class */
public interface Resource extends InputStreamSource {
    boolean exists();

    URL getURL() throws IOException;

    URI getURI() throws IOException;

    File getFile() throws IOException;

    long contentLength() throws IOException;

    long lastModified() throws IOException;

    Resource createRelative(String relativePath) throws IOException;

    @Nullable
    String getFilename();

    String getDescription();

    default boolean isReadable() {
        return exists();
    }

    default boolean isOpen() {
        return false;
    }

    default boolean isFile() {
        return false;
    }

    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    default byte[] getContentAsByteArray() throws IOException {
        return FileCopyUtils.copyToByteArray(getInputStream());
    }

    default String getContentAsString(Charset charset) throws IOException {
        return FileCopyUtils.copyToString(new InputStreamReader(getInputStream(), charset));
    }
}
