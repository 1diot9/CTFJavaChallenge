package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/AbstractResource.class */
public abstract class AbstractResource implements Resource {
    @Override // org.springframework.core.io.Resource
    public boolean exists() {
        if (isFile()) {
            try {
                return getFile().exists();
            } catch (IOException ex) {
                debug(() -> {
                    return "Could not retrieve File for existence check of " + getDescription();
                }, ex);
            }
        }
        try {
            getInputStream().close();
            return true;
        } catch (Throwable ex2) {
            debug(() -> {
                return "Could not retrieve InputStream for existence check of " + getDescription();
            }, ex2);
            return false;
        }
    }

    @Override // org.springframework.core.io.Resource
    public boolean isReadable() {
        return exists();
    }

    @Override // org.springframework.core.io.Resource
    public boolean isOpen() {
        return false;
    }

    @Override // org.springframework.core.io.Resource
    public boolean isFile() {
        return false;
    }

    @Override // org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    @Override // org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        } catch (URISyntaxException ex) {
            throw new IOException("Invalid URI [" + url + "]", ex);
        }
    }

    @Override // org.springframework.core.io.Resource
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    @Override // org.springframework.core.io.Resource
    public ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    @Override // org.springframework.core.io.Resource
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        try {
            long size = 0;
            byte[] buf = new byte[256];
            while (true) {
                int read = is.read(buf);
                if (read == -1) {
                    break;
                }
                size += read;
            }
            return size;
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                debug(() -> {
                    return "Could not close content-length InputStream for " + getDescription();
                }, ex);
            }
        }
    }

    @Override // org.springframework.core.io.Resource
    public long lastModified() throws IOException {
        File fileToCheck = getFileForLastModifiedCheck();
        long lastModified = fileToCheck.lastModified();
        if (lastModified == 0 && !fileToCheck.exists()) {
            throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for checking its last-modified timestamp");
        }
        return lastModified;
    }

    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    @Override // org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
    }

    @Override // org.springframework.core.io.Resource
    @Nullable
    public String getFilename() {
        return null;
    }

    private void debug(Supplier<String> message, Throwable ex) {
        Log logger = LogFactory.getLog(getClass());
        if (logger.isDebugEnabled()) {
            logger.debug(message.get(), ex);
        }
    }

    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof Resource) {
                Resource that = (Resource) other;
                if (getDescription().equals(that.getDescription())) {
                }
            }
            return false;
        }
        return true;
    }

    public int hashCode() {
        return getDescription().hashCode();
    }

    public String toString() {
        return getDescription();
    }
}
