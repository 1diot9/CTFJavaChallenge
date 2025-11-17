package org.springframework.boot.autoconfigure.template;

import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/template/TemplateLocation.class */
public class TemplateLocation {
    private final String path;

    public TemplateLocation(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path;
    }

    public boolean exists(ResourcePatternResolver resolver) {
        Assert.notNull(resolver, "Resolver must not be null");
        if (resolver.getResource(this.path).exists()) {
            return true;
        }
        try {
            return anyExists(resolver);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean anyExists(ResourcePatternResolver resolver) throws IOException {
        String searchPath = this.path;
        if (searchPath.startsWith("classpath:")) {
            searchPath = "classpath*:" + searchPath.substring("classpath:".length());
        }
        if (searchPath.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
            Resource[] resources = resolver.getResources(searchPath);
            for (Resource resource : resources) {
                if (resource.exists()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public String toString() {
        return this.path;
    }
}
