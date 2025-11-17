package org.springframework.boot.autoconfigure.web.servlet;

import jakarta.servlet.MultipartConfigElement;
import java.util.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.servlet.multipart", ignoreUnknownFields = false)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/MultipartProperties.class */
public class MultipartProperties {
    private String location;
    private boolean enabled = true;
    private DataSize maxFileSize = DataSize.ofMegabytes(1);
    private DataSize maxRequestSize = DataSize.ofMegabytes(10);
    private DataSize fileSizeThreshold = DataSize.ofBytes(0);
    private boolean resolveLazily = false;
    private boolean strictServletCompliance = false;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DataSize getMaxFileSize() {
        return this.maxFileSize;
    }

    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public DataSize getMaxRequestSize() {
        return this.maxRequestSize;
    }

    public void setMaxRequestSize(DataSize maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public DataSize getFileSizeThreshold() {
        return this.fileSizeThreshold;
    }

    public void setFileSizeThreshold(DataSize fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }

    public boolean isResolveLazily() {
        return this.resolveLazily;
    }

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    public boolean isStrictServletCompliance() {
        return this.strictServletCompliance;
    }

    public void setStrictServletCompliance(boolean strictServletCompliance) {
        this.strictServletCompliance = strictServletCompliance;
    }

    public MultipartConfigElement createMultipartConfig() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source from = map.from((PropertyMapper) this.fileSizeThreshold);
        Objects.requireNonNull(factory);
        from.to(factory::setFileSizeThreshold);
        PropertyMapper.Source whenHasText = map.from((PropertyMapper) this.location).whenHasText();
        Objects.requireNonNull(factory);
        whenHasText.to(factory::setLocation);
        PropertyMapper.Source from2 = map.from((PropertyMapper) this.maxRequestSize);
        Objects.requireNonNull(factory);
        from2.to(factory::setMaxRequestSize);
        PropertyMapper.Source from3 = map.from((PropertyMapper) this.maxFileSize);
        Objects.requireNonNull(factory);
        from3.to(factory::setMaxFileSize);
        return factory.createMultipartConfig();
    }
}
