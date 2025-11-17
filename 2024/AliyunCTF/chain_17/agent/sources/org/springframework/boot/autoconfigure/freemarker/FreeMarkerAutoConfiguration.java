package org.springframework.boot.autoconfigure.freemarker;

import freemarker.template.Configuration;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

@EnableConfigurationProperties({FreeMarkerProperties.class})
@AutoConfiguration
@ConditionalOnClass({Configuration.class, FreeMarkerConfigurationFactory.class})
@Import({FreeMarkerServletWebConfiguration.class, FreeMarkerReactiveWebConfiguration.class, FreeMarkerNonWebConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerAutoConfiguration.class */
public class FreeMarkerAutoConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) FreeMarkerAutoConfiguration.class);
    private final ApplicationContext applicationContext;
    private final FreeMarkerProperties properties;

    public FreeMarkerAutoConfiguration(ApplicationContext applicationContext, FreeMarkerProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
        checkTemplateLocationExists();
    }

    public void checkTemplateLocationExists() {
        if (logger.isWarnEnabled() && this.properties.isCheckTemplateLocation()) {
            List<TemplateLocation> locations = getLocations();
            if (locations.stream().noneMatch(this::locationExists)) {
                logger.warn("Cannot find template location(s): " + locations + " (please add some templates, check your FreeMarker configuration, or set spring.freemarker.check-template-location=false)");
            }
        }
    }

    private List<TemplateLocation> getLocations() {
        List<TemplateLocation> locations = new ArrayList<>();
        for (String templateLoaderPath : this.properties.getTemplateLoaderPath()) {
            TemplateLocation location = new TemplateLocation(templateLoaderPath);
            locations.add(location);
        }
        return locations;
    }

    private boolean locationExists(TemplateLocation location) {
        return location.exists(this.applicationContext);
    }
}
