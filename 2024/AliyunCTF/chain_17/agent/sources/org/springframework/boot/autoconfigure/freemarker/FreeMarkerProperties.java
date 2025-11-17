package org.springframework.boot.autoconfigure.freemarker;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.freemarker")
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerProperties.class */
public class FreeMarkerProperties extends AbstractTemplateViewResolverProperties {
    public static final String DEFAULT_TEMPLATE_LOADER_PATH = "classpath:/templates/";
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_SUFFIX = ".ftlh";
    private Map<String, String> settings;
    private String[] templateLoaderPath;
    private boolean preferFileSystemAccess;

    public FreeMarkerProperties() {
        super("", DEFAULT_SUFFIX);
        this.settings = new HashMap();
        this.templateLoaderPath = new String[]{"classpath:/templates/"};
    }

    public Map<String, String> getSettings() {
        return this.settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String[] getTemplateLoaderPath() {
        return this.templateLoaderPath;
    }

    public boolean isPreferFileSystemAccess() {
        return this.preferFileSystemAccess;
    }

    public void setPreferFileSystemAccess(boolean preferFileSystemAccess) {
        this.preferFileSystemAccess = preferFileSystemAccess;
    }

    public void setTemplateLoaderPath(String... templateLoaderPaths) {
        this.templateLoaderPath = templateLoaderPaths;
    }
}
