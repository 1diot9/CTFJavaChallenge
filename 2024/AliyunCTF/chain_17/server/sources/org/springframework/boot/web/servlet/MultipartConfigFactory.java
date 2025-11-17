package org.springframework.boot.web.servlet;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.util.unit.DataSize;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/servlet/MultipartConfigFactory.class */
public class MultipartConfigFactory {
    private String location;
    private DataSize maxFileSize;
    private DataSize maxRequestSize;
    private DataSize fileSizeThreshold;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public void setMaxRequestSize(DataSize maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public void setFileSizeThreshold(DataSize fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }

    public MultipartConfigElement createMultipartConfig() {
        long maxFileSizeBytes = convertToBytes(this.maxFileSize, -1);
        long maxRequestSizeBytes = convertToBytes(this.maxRequestSize, -1);
        long fileSizeThresholdBytes = convertToBytes(this.fileSizeThreshold, 0);
        return new MultipartConfigElement(this.location, maxFileSizeBytes, maxRequestSizeBytes, (int) fileSizeThresholdBytes);
    }

    private long convertToBytes(DataSize size, int defaultValue) {
        if (size != null && !size.isNegative()) {
            return size.toBytes();
        }
        return defaultValue;
    }
}
