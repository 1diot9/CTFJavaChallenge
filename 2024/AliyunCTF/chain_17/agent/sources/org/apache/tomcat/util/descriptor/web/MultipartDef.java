package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/descriptor/web/MultipartDef.class */
public class MultipartDef implements Serializable {
    private static final long serialVersionUID = 1;
    private String location;
    private String maxFileSize;
    private String maxRequestSize;
    private String fileSizeThreshold;

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMaxFileSize() {
        return this.maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getMaxRequestSize() {
        return this.maxRequestSize;
    }

    public void setMaxRequestSize(String maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public String getFileSizeThreshold() {
        return this.fileSizeThreshold;
    }

    public void setFileSizeThreshold(String fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.fileSizeThreshold == null ? 0 : this.fileSizeThreshold.hashCode());
        return (31 * ((31 * ((31 * result) + (this.location == null ? 0 : this.location.hashCode()))) + (this.maxFileSize == null ? 0 : this.maxFileSize.hashCode()))) + (this.maxRequestSize == null ? 0 : this.maxRequestSize.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof MultipartDef)) {
            return false;
        }
        MultipartDef other = (MultipartDef) obj;
        if (this.fileSizeThreshold == null) {
            if (other.fileSizeThreshold != null) {
                return false;
            }
        } else if (!this.fileSizeThreshold.equals(other.fileSizeThreshold)) {
            return false;
        }
        if (this.location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!this.location.equals(other.location)) {
            return false;
        }
        if (this.maxFileSize == null) {
            if (other.maxFileSize != null) {
                return false;
            }
        } else if (!this.maxFileSize.equals(other.maxFileSize)) {
            return false;
        }
        if (this.maxRequestSize == null) {
            if (other.maxRequestSize != null) {
                return false;
            }
            return true;
        }
        if (!this.maxRequestSize.equals(other.maxRequestSize)) {
            return false;
        }
        return true;
    }
}
