package org.apache.tomcat.util.http.fileupload.servlet;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.UploadContext;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/servlet/ServletRequestContext.class */
public class ServletRequestContext implements UploadContext {
    private final HttpServletRequest request;

    public ServletRequestContext(HttpServletRequest request) {
        this.request = request;
    }

    @Override // org.apache.tomcat.util.http.fileupload.RequestContext
    public String getCharacterEncoding() {
        return this.request.getCharacterEncoding();
    }

    @Override // org.apache.tomcat.util.http.fileupload.RequestContext
    public String getContentType() {
        return this.request.getContentType();
    }

    @Override // org.apache.tomcat.util.http.fileupload.UploadContext
    public long contentLength() {
        long size;
        try {
            size = Long.parseLong(this.request.getHeader(FileUploadBase.CONTENT_LENGTH));
        } catch (NumberFormatException e) {
            size = this.request.getContentLength();
        }
        return size;
    }

    @Override // org.apache.tomcat.util.http.fileupload.RequestContext
    public InputStream getInputStream() throws IOException {
        return this.request.getInputStream();
    }

    public String toString() {
        return String.format("ContentLength=%s, ContentType=%s", Long.valueOf(contentLength()), getContentType());
    }
}
