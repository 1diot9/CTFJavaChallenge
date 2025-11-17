package org.springframework.boot.autoconfigure.groovy.template;

import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.groovy.template", ignoreUnknownFields = true)
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/groovy/template/GroovyTemplateProperties.class */
public class GroovyTemplateProperties extends AbstractTemplateViewResolverProperties {
    public static final String DEFAULT_RESOURCE_LOADER_PATH = "classpath:/templates/";
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_SUFFIX = ".tpl";
    public static final String DEFAULT_REQUEST_CONTEXT_ATTRIBUTE = "spring";
    private String resourceLoaderPath;

    public GroovyTemplateProperties() {
        super("", DEFAULT_SUFFIX);
        this.resourceLoaderPath = "classpath:/templates/";
        setRequestContextAttribute(DEFAULT_REQUEST_CONTEXT_ATTRIBUTE);
    }

    public String getResourceLoaderPath() {
        return this.resourceLoaderPath;
    }

    public void setResourceLoaderPath(String resourceLoaderPath) {
        this.resourceLoaderPath = resourceLoaderPath;
    }
}
