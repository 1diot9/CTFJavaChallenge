package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/PathResource.class */
public class PathResource extends AbstractResource implements WritableResource {
    private final Path path;

    public PathResource(Path path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path.normalize();
    }

    public PathResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = Paths.get(path, new String[0]).normalize();
    }

    public PathResource(URI uri) {
        Assert.notNull(uri, "URI must not be null");
        this.path = Paths.get(uri).normalize();
    }

    public final String getPath() {
        return this.path.toString();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return Files.exists(this.path, new LinkOption[0]);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        return Files.isReadable(this.path) && !Files.isDirectory(this.path, new LinkOption[0]);
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        if (!exists()) {
            throw new FileNotFoundException(getPath() + " (no such file or directory)");
        }
        if (Files.isDirectory(this.path, new LinkOption[0])) {
            throw new FileNotFoundException(getPath() + " (is a directory)");
        }
        return Files.newInputStream(this.path, new OpenOption[0]);
    }

    @Override // org.springframework.core.io.Resource
    public byte[] getContentAsByteArray() throws IOException {
        try {
            return Files.readAllBytes(this.path);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.Resource
    public String getContentAsString(Charset charset) throws IOException {
        try {
            return Files.readString(this.path, charset);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.WritableResource
    public boolean isWritable() {
        return Files.isWritable(this.path) && !Files.isDirectory(this.path, new LinkOption[0]);
    }

    @Override // org.springframework.core.io.WritableResource
    public OutputStream getOutputStream() throws IOException {
        if (Files.isDirectory(this.path, new LinkOption[0])) {
            throw new FileNotFoundException(getPath() + " (is a directory)");
        }
        return Files.newOutputStream(this.path, new OpenOption[0]);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        return this.path.toUri().toURL();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        return this.path.toUri();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isFile() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() throws IOException {
        try {
            return this.path.toFile();
        } catch (UnsupportedOperationException e) {
            throw new FileNotFoundException(this.path + " cannot be resolved to absolute file path");
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public ReadableByteChannel readableChannel() throws IOException {
        try {
            return Files.newByteChannel(this.path, StandardOpenOption.READ);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.WritableResource
    public WritableByteChannel writableChannel() throws IOException {
        return Files.newByteChannel(this.path, StandardOpenOption.WRITE);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() throws IOException {
        return Files.size(this.path);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long lastModified() throws IOException {
        return Files.getLastModifiedTime(this.path, new LinkOption[0]).toMillis();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) {
        return new PathResource(this.path.resolve(relativePath));
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public String getFilename() {
        return this.path.getFileName().toString();
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "path [" + this.path.toAbsolutePath() + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof PathResource) {
                PathResource that = (PathResource) other;
                if (this.path.equals(that.path)) {
                }
            }
            return false;
        }
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.path.hashCode();
    }
}
