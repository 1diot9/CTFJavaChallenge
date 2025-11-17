package org.springframework.boot.autoconfigure.thymeleaf;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/thymeleaf/ThymeleafTemplateAvailabilityProvider.class */
public class ThymeleafTemplateAvailabilityProvider implements TemplateAvailabilityProvider {
    @Override // org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider
    public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader, ResourceLoader resourceLoader) {
        if (ClassUtils.isPresent("org.thymeleaf.spring6.SpringTemplateEngine", classLoader)) {
            String prefix = environment.getProperty("spring.thymeleaf.prefix", "classpath:/templates/");
            String suffix = environment.getProperty("spring.thymeleaf.suffix", ThymeleafProperties.DEFAULT_SUFFIX);
            return resourceLoader.getResource(prefix + view + suffix).exists();
        }
        return false;
    }
}
