package org.apache.tomcat.util.http.fileupload;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/http/fileupload/InvalidFileNameException.class */
public class InvalidFileNameException extends RuntimeException {
    private static final long serialVersionUID = 7922042602454350470L;
    private final String name;

    public InvalidFileNameException(String pName, String pMessage) {
        super(pMessage);
        this.name = pName;
    }

    public String getName() {
        return this.name;
    }
}
