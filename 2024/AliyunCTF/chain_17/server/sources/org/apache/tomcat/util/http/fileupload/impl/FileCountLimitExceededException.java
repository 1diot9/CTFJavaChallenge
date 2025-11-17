package org.apache.tomcat.util.http.fileupload.impl;

import org.apache.tomcat.util.http.fileupload.FileUploadException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/impl/FileCountLimitExceededException.class */
public class FileCountLimitExceededException extends FileUploadException {
    private static final long serialVersionUID = 2408766352570556046L;
    private final long limit;

    public FileCountLimitExceededException(String message, long limit) {
        super(message);
        this.limit = limit;
    }

    public long getLimit() {
        return this.limit;
    }
}
