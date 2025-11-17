package org.apache.tomcat.util.http.fileupload;

import java.util.Iterator;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/FileItemHeaders.class */
public interface FileItemHeaders {
    String getHeader(String str);

    Iterator<String> getHeaders(String str);

    Iterator<String> getHeaderNames();
}
