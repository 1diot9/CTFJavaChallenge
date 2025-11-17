package org.springframework.boot.autoconfigure.mustache;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mustache/MustacheTemplateAvailabilityProvider.class */
public class MustacheTemplateAvailabilityProvider implements TemplateAvailabilityProvider {
    @Override // org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider
    public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader) {
        if (ClassUtils.isPresent("com.samskivert.mustache.Template", classLoader)) {
            String prefix = environment.getProperty("spring.mustache.prefix", "classpath:/templates/");
            String suffix = environment.getProperty("spring.mustache.suffix", MustacheProperties.DEFAULT_SUFFIX);
            return resourceLoader.getResource(prefix + view + suffix).exists();
        }
        return false;
    }
}
