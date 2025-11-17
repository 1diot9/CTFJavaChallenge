package org.apache.tomcat.util.http.fileupload;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/FileUpload.class */
public class FileUpload extends FileUploadBase {
    private FileItemFactory fileItemFactory;

    @Override // org.apache.tomcat.util.http.fileupload.FileUploadBase
    public FileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }

    @Override // org.apache.tomcat.util.http.fileupload.FileUploadBase
    public void setFileItemFactory(FileItemFactory factory) {
        this.fileItemFactory = factory;
    }
}
