package org.apache.tomcat.util.http.fileupload.impl;

import java.io.IOException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/impl/IOFileUploadException.class */
public class IOFileUploadException extends FileUploadException {
    private static final long serialVersionUID = 1749796615868477269L;
    private final IOException cause;

    public IOFileUploadException(String pMsg, IOException pException) {
        super(pMsg);
        this.cause = pException;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }
}
