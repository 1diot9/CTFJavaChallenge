package org.apache.tomcat.util.http.fileupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/DeferredFileOutputStream.class */
public class DeferredFileOutputStream extends ThresholdingOutputStream {
    private ByteArrayOutputStream memoryOutputStream;
    private OutputStream currentOutputStream;
    private File outputFile;
    private final String prefix;
    private final String suffix;
    private final File directory;

    public DeferredFileOutputStream(int threshold, File outputFile) {
        this(threshold, outputFile, null, null, null, 1024);
    }

    private DeferredFileOutputStream(int threshold, File outputFile, String prefix, String suffix, File directory, int initialBufferSize) {
        super(threshold);
        this.outputFile = outputFile;
        this.prefix = prefix;
        this.suffix = suffix;
        this.directory = directory;
        this.memoryOutputStream = new ByteArrayOutputStream(initialBufferSize);
        this.currentOutputStream = this.memoryOutputStream;
    }

    @Override // org.apache.tomcat.util.http.fileupload.ThresholdingOutputStream
    protected OutputStream getStream() throws IOException {
        return this.currentOutputStream;
    }

    @Override // org.apache.tomcat.util.http.fileupload.ThresholdingOutputStream
    protected void thresholdReached() throws IOException {
        if (this.prefix != null) {
            this.outputFile = File.createTempFile(this.prefix, this.suffix, this.directory);
        }
        FileUtils.forceMkdirParent(this.outputFile);
        FileOutputStream fos = new FileOutputStream(this.outputFile);
        try {
            this.memoryOutputStream.writeTo(fos);
            this.currentOutputStream = fos;
            this.memoryOutputStream = null;
        } catch (IOException e) {
            fos.close();
            throw e;
        }
    }

    public boolean isInMemory() {
        return !isThresholdExceeded();
    }

    public byte[] getData() {
        if (this.memoryOutputStream != null) {
            return this.memoryOutputStream.toByteArray();
        }
        return null;
    }

    public File getFile() {
        return this.outputFile;
    }

    @Override // org.apache.tomcat.util.http.fileupload.ThresholdingOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        super.close();
    }
}
