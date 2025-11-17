package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/io/FileUrlResource.class */
public class FileUrlResource extends UrlResource implements WritableResource {

    @Nullable
    private volatile File file;

    public FileUrlResource(URL url) {
        super(url);
    }

    public FileUrlResource(String location) throws MalformedURLException {
        super("file", location);
    }

    @Override // org.springframework.core.io.UrlResource, org.springframework.core.io.AbstractFileResolvingResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public File getFile() throws IOException {
        File file = this.file;
        if (file != null) {
            return file;
        }
        File file2 = super.getFile();
        this.file = file2;
        return file2;
    }

    @Override // org.springframework.core.io.WritableResource
    public boolean isWritable() {
        try {
            File file = getFile();
            if (file.canWrite()) {
                if (!file.isDirectory()) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override // org.springframework.core.io.WritableResource
    public OutputStream getOutputStream() throws IOException {
        return Files.newOutputStream(getFile().toPath(), new OpenOption[0]);
    }

    @Override // org.springframework.core.io.WritableResource
    public WritableByteChannel writableChannel() throws IOException {
        return FileChannel.open(getFile().toPath(), StandardOpenOption.WRITE);
    }

    @Override // org.springframework.core.io.UrlResource, org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public Resource createRelative(String relativePath) throws MalformedURLException {
        return new FileUrlResource(createRelativeURL(relativePath));
    }
}
