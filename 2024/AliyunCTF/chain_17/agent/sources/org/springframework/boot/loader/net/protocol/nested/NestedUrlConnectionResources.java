package org.springframework.boot.loader.net.protocol.nested;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.springframework.boot.loader.zip.CloseableDataBlock;
import org.springframework.boot.loader.zip.ZipContent;

/* loaded from: agent.jar:org/springframework/boot/loader/net/protocol/nested/NestedUrlConnectionResources.class */
class NestedUrlConnectionResources implements Runnable {
    private final NestedLocation location;
    private volatile ZipContent zipContent;
    private volatile long size = -1;
    private volatile InputStream inputStream;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedUrlConnectionResources(NestedLocation location) {
        this.location = location;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NestedLocation getLocation() {
        return this.location;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void connect() throws IOException {
        synchronized (this) {
            if (this.zipContent == null) {
                this.zipContent = ZipContent.open(this.location.path(), this.location.nestedEntryName());
                try {
                    connectData();
                } catch (IOException | RuntimeException ex) {
                    this.zipContent.close();
                    this.zipContent = null;
                    throw ex;
                }
            }
        }
    }

    private void connectData() throws IOException {
        CloseableDataBlock data = this.zipContent.openRawZipData();
        try {
            this.size = data.size();
            this.inputStream = data.asInputStream();
        } catch (IOException | RuntimeException e) {
            data.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public InputStream getInputStream() throws IOException {
        InputStream inputStream;
        synchronized (this) {
            if (this.inputStream == null) {
                throw new IOException("Nested location not found " + this.location);
            }
            inputStream = this.inputStream;
        }
        return inputStream;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getContentLength() {
        return this.size;
    }

    @Override // java.lang.Runnable
    public void run() {
        releaseAll();
    }

    private void releaseAll() {
        synchronized (this) {
            if (this.zipContent != null) {
                IOException exceptionChain = null;
                try {
                    this.inputStream.close();
                } catch (IOException ex) {
                    exceptionChain = addToExceptionChain(null, ex);
                }
                try {
                    this.zipContent.close();
                } catch (IOException ex2) {
                    exceptionChain = addToExceptionChain(exceptionChain, ex2);
                }
                this.size = -1L;
                if (exceptionChain != null) {
                    throw new UncheckedIOException(exceptionChain);
                }
            }
        }
    }

    private IOException addToExceptionChain(IOException exceptionChain, IOException ex) {
        if (exceptionChain != null) {
            exceptionChain.addSuppressed(ex);
            return exceptionChain;
        }
        return ex;
    }
}
