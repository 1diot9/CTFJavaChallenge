package org.apache.tomcat.util.http.fileupload.util;

import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/util/Closeable.class */
public interface Closeable {
    void close() throws IOException;

    boolean isClosed() throws IOException;
}
