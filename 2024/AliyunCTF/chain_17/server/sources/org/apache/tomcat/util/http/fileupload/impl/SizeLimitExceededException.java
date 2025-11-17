package org.apache.tomcat.util.http.fileupload.impl;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/impl/SizeLimitExceededException.class */
public class SizeLimitExceededException extends SizeException {
    private static final long serialVersionUID = -2474893167098052828L;

    public SizeLimitExceededException(String message, long actual, long permitted) {
        super(message, actual, permitted);
    }
}
