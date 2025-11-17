package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/FileItemIterator.class */
public interface FileItemIterator {
    long getFileSizeMax();

    void setFileSizeMax(long j);

    long getSizeMax();

    void setSizeMax(long j);

    boolean hasNext() throws FileUploadException, IOException;

    FileItemStream next() throws FileUploadException, IOException;

    List<FileItem> getFileItems() throws FileUploadException, IOException;
}
