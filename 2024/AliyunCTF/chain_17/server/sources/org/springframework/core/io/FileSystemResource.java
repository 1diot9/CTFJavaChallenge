package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/FileSystemResource.class */
public class FileSystemResource extends AbstractResource implements WritableResource {
    private final String path;

    @Nullable
    private final File file;
    private final Path filePath;

    public FileSystemResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        this.file = new File(path);
        this.filePath = this.file.toPath();
    }

    public FileSystemResource(File file) {
        Assert.notNull(file, "File must not be null");
        this.path = StringUtils.cleanPath(file.getPath());
        this.file = file;
        this.filePath = file.toPath();
    }

    public FileSystemResource(Path filePath) {
        Assert.notNull(filePath, "Path must not be null");
        this.path = StringUtils.cleanPath(filePath.toString());
        this.file = null;
        this.filePath = filePath;
    }

    public FileSystemResource(FileSystem fileSystem, String path) {
        Assert.notNull(fileSystem, "FileSystem must not be null");
        Assert.notNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        this.file = null;
        this.filePath = fileSystem.getPath(this.path, new String[0]).normalize();
    }

    public final String getPath() {
        return this.path;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return this.file != null ? this.file.exists() : Files.exists(this.filePath, new LinkOption[0]);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isReadable() {
        return this.file != null ? this.file.canRead() && !this.file.isDirectory() : Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath, new LinkOption[0]);
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IOException {
        try {
            return Files.newInputStream(this.filePath, new OpenOption[0]);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.Resource
    public byte[] getContentAsByteArray() throws IOException {
        try {
            return Files.readAllBytes(this.filePath);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.Resource
    public String getContentAsString(Charset charset) throws IOException {
        try {
            return Files.readString(this.filePath, charset);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.WritableResource
    public boolean isWritable() {
        return this.file != null ? this.file.canWrite() && !this.file.isDirectory() : Files.isWritable(this.filePath) && !Files.isDirectory(this.filePath, new LinkOption[0]);
    }

    @Override // org.springframework.core.io.WritableResource
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(this.filePath, new OpenOption[0]);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URL getURL() throws IOException {
        return this.file != null ? this.file.toURI().toURL() : this.filePath.toUri().toURL();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public URI getURI() throws IOException {
        if (this.file != null) {
            return this.file.toURI();
        }
        URI uri = this.filePath.toUri();
        String scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            try {
                uri = new URI(scheme, uri.getPath(), null);
            } catch (URISyntaxException ex) {
                throw new IOException("Failed to normalize URI: " + uri, ex);
            }
        }
        return uri;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isFile() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() {
        return this.file != null ? this.file : this.filePath.toFile();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public ReadableByteChannel readableChannel() throws IOException {
        try {
            return FileChannel.open(this.filePath, StandardOpenOption.READ);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.WritableResource
    public WritableByteChannel writableChannel() throws IOException {
        return FileChannel.open(this.filePath, StandardOpenOption.WRITE);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() throws IOException {
        if (this.file != null) {
            long length = this.file.length();
            if (length == 0 && !this.file.exists()) {
                throw new FileNotFoundException(getDescription() + " cannot be resolved in the file system for checking its content length");
            }
            return length;
        }
        try {
            return Files.size(this.filePath);
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long lastModified() throws IOException {
        if (this.file != null) {
            return super.lastModified();
        }
        try {
            return Files.getLastModifiedTime(this.filePath, new LinkOption[0]).toMillis();
        } catch (NoSuchFileException ex) {
            throw new FileNotFoundException(ex.getMessage());
        }
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) {
        String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
        return this.file != null ? new FileSystemResource(pathToUse) : new FileSystemResource(this.filePath.getFileSystem(), pathToUse);
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public String getFilename() {
        return this.file != null ? this.file.getName() : this.filePath.getFileName().toString();
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "file [" + (this.file != null ? this.file.getAbsolutePath() : this.filePath.toAbsolutePath()) + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object other) {
        if (this != other) {
            if (other instanceof FileSystemResource) {
                FileSystemResource that = (FileSystemResource) other;
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
