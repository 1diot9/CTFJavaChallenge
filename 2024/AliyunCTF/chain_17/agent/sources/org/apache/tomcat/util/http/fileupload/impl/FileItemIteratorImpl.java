package org.apache.tomcat.util.http.fileupload.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemHeaders;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.MultipartStream;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/impl/FileItemIteratorImpl.class */
public class FileItemIteratorImpl implements FileItemIterator {
    private final FileUploadBase fileUploadBase;
    private final RequestContext ctx;
    private long sizeMax;
    private long fileSizeMax;
    private MultipartStream multiPartStream;
    private MultipartStream.ProgressNotifier progressNotifier;
    private byte[] multiPartBoundary;
    private FileItemStreamImpl currentItem;
    private String currentFieldName;
    private boolean skipPreamble = true;
    private boolean itemValid;
    private boolean eof;

    @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
    public long getSizeMax() {
        return this.sizeMax;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
    public long getFileSizeMax() {
        return this.fileSizeMax;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
    public void setFileSizeMax(long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }

    public FileItemIteratorImpl(FileUploadBase fileUploadBase, RequestContext requestContext) throws FileUploadException, IOException {
        this.fileUploadBase = fileUploadBase;
        this.sizeMax = fileUploadBase.getSizeMax();
        this.fileSizeMax = fileUploadBase.getFileSizeMax();
        this.ctx = (RequestContext) Objects.requireNonNull(requestContext, "requestContext");
        findNextItem();
    }

    protected void init(FileUploadBase fileUploadBase, RequestContext pRequestContext) throws FileUploadException, IOException {
        InputStream input;
        String contentType = this.ctx.getContentType();
        if (null == contentType || !contentType.toLowerCase(Locale.ENGLISH).startsWith(FileUploadBase.MULTIPART)) {
            throw new InvalidContentTypeException(String.format("the request doesn't contain a %s or %s stream, content type header is %s", "multipart/form-data", "multipart/mixed", contentType));
        }
        long requestSize = ((UploadContext) this.ctx).contentLength();
        if (this.sizeMax < 0) {
            input = this.ctx.getInputStream();
        } else {
            if (requestSize != -1 && requestSize > this.sizeMax) {
                throw new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", Long.valueOf(requestSize), Long.valueOf(this.sizeMax)), requestSize, this.sizeMax);
            }
            input = new LimitedInputStream(this.ctx.getInputStream(), this.sizeMax) { // from class: org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl.1
                @Override // org.apache.tomcat.util.http.fileupload.util.LimitedInputStream
                protected void raiseError(long pSizeMax, long pCount) throws IOException {
                    FileUploadException ex = new SizeLimitExceededException(String.format("the request was rejected because its size (%s) exceeds the configured maximum (%s)", Long.valueOf(pCount), Long.valueOf(pSizeMax)), pCount, pSizeMax);
                    throw new FileUploadIOException(ex);
                }
            };
        }
        String charEncoding = fileUploadBase.getHeaderEncoding();
        if (charEncoding == null) {
            charEncoding = this.ctx.getCharacterEncoding();
        }
        this.multiPartBoundary = fileUploadBase.getBoundary(contentType);
        if (this.multiPartBoundary == null) {
            IOUtils.closeQuietly(input);
            throw new FileUploadException("the request was rejected because no multipart boundary was found");
        }
        this.progressNotifier = new MultipartStream.ProgressNotifier(fileUploadBase.getProgressListener(), requestSize);
        try {
            this.multiPartStream = new MultipartStream(input, this.multiPartBoundary, this.progressNotifier);
            this.multiPartStream.setHeaderEncoding(charEncoding);
        } catch (IllegalArgumentException iae) {
            IOUtils.closeQuietly(input);
            throw new InvalidContentTypeException(String.format("The boundary specified in the %s header is too long", FileUploadBase.CONTENT_TYPE), iae);
        }
    }

    public MultipartStream getMultiPartStream() throws FileUploadException, IOException {
        if (this.multiPartStream == null) {
            init(this.fileUploadBase, this.ctx);
        }
        return this.multiPartStream;
    }

    /* JADX WARN: Code restructure failed: missing block: B:31:0x00b7, code lost:            r0 = r11.fileUploadBase.getFileName(r0);        r6 = r0.getHeader(org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE);     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x00d5, code lost:            if (r0 != null) goto L33;     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00d8, code lost:            r7 = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x00dd, code lost:            r11.currentItem = new org.apache.tomcat.util.http.fileupload.impl.FileItemStreamImpl(r11, r0, r0, r6, r7, getContentLength(r0));        r11.currentItem.setHeaders(r0);        r11.progressNotifier.noteItem();        r11.itemValid = true;     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00fd, code lost:            return true;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00dc, code lost:            r7 = false;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean findNextItem() throws org.apache.tomcat.util.http.fileupload.FileUploadException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 334
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.tomcat.util.http.fileupload.impl.FileItemIteratorImpl.findNextItem():boolean");
    }

    private long getContentLength(FileItemHeaders pHeaders) {
        try {
            return Long.parseLong(pHeaders.getHeader(FileUploadBase.CONTENT_LENGTH));
        } catch (Exception e) {
            return -1L;
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
    public boolean hasNext() throws FileUploadException, IOException {
        if (this.eof) {
            return false;
        }
        if (this.itemValid) {
            return true;
        }
        try {
            return findNextItem();
        } catch (FileUploadIOException e) {
            throw ((FileUploadException) e.getCause());
        }
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
    public FileItemStream next() throws FileUploadException, IOException {
        if (this.eof || (!this.itemValid && !hasNext())) {
            throw new NoSuchElementException();
        }
        this.itemValid = false;
        return this.currentItem;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileItemIterator
    public List<FileItem> getFileItems() throws FileUploadException, IOException {
        List<FileItem> items = new ArrayList<>();
        while (hasNext()) {
            FileItemStream fis = next();
            FileItem fi = this.fileUploadBase.getFileItemFactory().createItem(fis.getFieldName(), fis.getContentType(), fis.isFormField(), fis.getName());
            items.add(fi);
        }
        return items;
    }
}
