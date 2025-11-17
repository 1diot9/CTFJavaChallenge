package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import org.apache.tomcat.util.buf.UDecoder;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/ErrorPage.class */
public class ErrorPage extends XmlEncodingBase implements Serializable {
    private static final long serialVersionUID = 2;
    private int errorCode = 0;
    private String exceptionType = null;
    private String location = null;

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorCode(String errorCode) {
        try {
            this.errorCode = Integer.parseInt(errorCode);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(nfe);
        }
    }

    public String getExceptionType() {
        return this.exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = UDecoder.URLDecode(location, getCharset());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ErrorPage[");
        if (this.exceptionType == null) {
            sb.append("errorCode=");
            sb.append(this.errorCode);
        } else {
            sb.append("exceptionType=");
            sb.append(this.exceptionType);
        }
        sb.append(", location=");
        sb.append(this.location);
        sb.append(']');
        return sb.toString();
    }

    public String getName() {
        if (this.exceptionType == null) {
            return Integer.toString(this.errorCode);
        }
        return this.exceptionType;
    }
}
