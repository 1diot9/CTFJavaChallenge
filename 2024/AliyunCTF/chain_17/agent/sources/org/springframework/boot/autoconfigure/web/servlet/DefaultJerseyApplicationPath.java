package org.springframework.boot.autoconfigure.web.servlet;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/web/servlet/DefaultJerseyApplicationPath.class */
public class DefaultJerseyApplicationPath implements JerseyApplicationPath {
    private final String applicationPath;
    private final ResourceConfig config;

    public DefaultJerseyApplicationPath(String applicationPath, ResourceConfig config) {
        this.applicationPath = applicationPath;
        this.config = config;
    }

    @Override // org.springframework.boot.autoconfigure.web.servlet.JerseyApplicationPath
    public String getPath() {
        return resolveApplicationPath();
    }

    private String resolveApplicationPath() {
        if (StringUtils.hasLength(this.applicationPath)) {
            return this.applicationPath;
        }
        return (String) MergedAnnotations.from(this.config.getApplication().getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ApplicationPath.class).getValue("value", String.class).orElse("/*");
    }
}
