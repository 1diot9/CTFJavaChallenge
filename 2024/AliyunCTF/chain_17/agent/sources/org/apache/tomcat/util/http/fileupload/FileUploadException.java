package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/FileUploadException.class */
public class FileUploadException extends IOException {
    private static final long serialVersionUID = -4222909057964038517L;

    public FileUploadException() {
    }

    public FileUploadException(String msg) {
        super(msg);
    }

    public FileUploadException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
