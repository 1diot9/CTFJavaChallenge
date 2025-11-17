package org.apache.tomcat.util.http.fileupload.impl;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.apache.tomcat.util.http.fileupload.util.Closeable;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.apache.tomcat.util.http.fileupload.util.Streams;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/impl/FileItemStreamImpl.class */
public class FileItemStreamImpl implements FileItemStream {
    private final FileItemIteratorImpl fileItemIteratorImpl;
    private final String contentType;
    private final String fieldName;
    private final String name;
    private final boolean formField;
    private final InputStream stream;
    private FileItemHeaders headers;

    public FileItemStreamImpl(FileItemIteratorImpl pFileItemIterator, String pName, String pFieldName, String pContentType, boolean pFormField, long pContentLength) throws FileUploadException, IOException {
        this.fileItemIteratorImpl = pFileItemIterator;
        this.name = pName;
        this.fieldName = pFieldName;
        this.contentType = pContentType;
        this.formField = pFormField;
        long fileSizeMax = this.fileItemIteratorImpl.getFileSizeMax();
        if (fileSizeMax != -1 && pContentLength != -1 && pContentLength > fileSizeMax) {
            FileSizeLimitExceededException e = new FileSizeLimitExceededException(String.format("The field %s exceeds its maximum permitted size of %s bytes.", this.fieldName, Long.valueOf(fileSizeMax)), pContentLength, fileSizeMax);
            e.setFileName(pName);
            e.setFieldName(pFieldName);
            throw new FileUploadIOException(e);
        }
        final MultipartStream.ItemInputStream itemStream = this.fileItemIteratorImpl.getMultiPartStream().newInputStream();
        InputStream istream = itemStream;
        this.stream = fileSizeMax != -1 ? new LimitedInputStream(istream, fileSizeMax) { // from class: org.apache.tomcat.util.http.fileupload.impl.FileItemStreamImpl.1
            @Override // org.apache.tomcat.util.http.fileupload.util.LimitedInputStream
            protected void raiseError(long pSizeMax, long pCount) throws IOException {
                itemStream.close(true);
                FileSizeLimitExceededException e2 = new FileSizeLimitExceededException(String.format("The field %s exceeds its maximum permitted size of %s bytes.", FileItemStreamImpl.this.fieldName, Long.valueOf(pSizeMax)), pCount, pSizeMax);
                e2.setFieldName(FileItemStreamImpl.this.fieldName);
                e2.setFileName(FileItemStreamImpl.this.name);
                throw new FileUploadIOException(e2);
            }
        } : istream;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
    public String getContentType() {
        return this.contentType;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
    public String getFieldName() {
        return this.fieldName;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
    public String getName() {
        return Streams.checkFileName(this.name);
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
    public boolean isFormField() {
        return this.formField;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemStream
    public InputStream openStream() throws IOException {
        if (((Closeable) this.stream).isClosed()) {
            throw new FileItemStream.ItemSkippedException();
        }
        return this.stream;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
    public FileItemHeaders getHeaders() {
        return this.headers;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemHeadersSupport
    public void setHeaders(FileItemHeaders pHeaders) {
        this.headers = pHeaders;
    }
}
