package jakarta.servlet;

import jakarta.servlet.annotation.MultipartConfig;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:jakarta/servlet/MultipartConfigElement.class */
public class MultipartConfigElement {
    private final String location;
    private final long maxFileSize;
    private final long maxRequestSize;
    private final int fileSizeThreshold;

    public MultipartConfigElement(String location) {
        if (location != null) {
            this.location = location;
        } else {
            this.location = "";
        }
        this.maxFileSize = -1L;
        this.maxRequestSize = -1L;
        this.fileSizeThreshold = 0;
    }

    public MultipartConfigElement(String location, long maxFileSize, long maxRequestSize, int fileSizeThreshold) {
        if (location != null) {
            this.location = location;
        } else {
            this.location = "";
        }
        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
        if (fileSizeThreshold > 0) {
            this.fileSizeThreshold = fileSizeThreshold;
        } else {
            this.fileSizeThreshold = 0;
        }
    }

    public MultipartConfigElement(MultipartConfig annotation) {
        this.location = annotation.location();
        this.maxFileSize = annotation.maxFileSize();
        this.maxRequestSize = annotation.maxRequestSize();
        this.fileSizeThreshold = annotation.fileSizeThreshold();
    }

    public String getLocation() {
        return this.location;
    }

    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    public long getMaxRequestSize() {
        return this.maxRequestSize;
    }

    public int getFileSizeThreshold() {
        return this.fileSizeThreshold;
    }
}
