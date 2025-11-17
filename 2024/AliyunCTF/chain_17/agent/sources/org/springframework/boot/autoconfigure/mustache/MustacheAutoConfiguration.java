package org.springframework.boot.autoconfigure.mustache;

import com.samskivert.mustache.Mustache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({MustacheProperties.class})
@AutoConfiguration
@ConditionalOnClass({Mustache.class})
@Import({MustacheServletWebConfiguration.class, MustacheReactiveWebConfiguration.class})
/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-autoconfigure-3.2.2.jar:org/springframework/boot/autoconfigure/mustache/MustacheAutoConfiguration.class */
public class MustacheAutoConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) MustacheAutoConfiguration.class);
    private final MustacheProperties mustache;
    private final ApplicationContext applicationContext;

    public MustacheAutoConfiguration(MustacheProperties mustache, ApplicationContext applicationContext) {
        this.mustache = mustache;
        this.applicationContext = applicationContext;
        checkTemplateLocationExists();
    }

    public void checkTemplateLocationExists() {
        if (this.mustache.isCheckTemplateLocation()) {
            TemplateLocation location = new TemplateLocation(this.mustache.getPrefix());
            if (!location.exists(this.applicationContext) && logger.isWarnEnabled()) {
                logger.warn("Cannot find template location: " + location + " (please add some templates, check your Mustache configuration, or set spring.mustache.check-template-location=false)");
            }
        }
    }

    @ConditionalOnMissingBean
    @Bean
    public Mustache.Compiler mustacheCompiler(Mustache.TemplateLoader mustacheTemplateLoader) {
        return Mustache.compiler().withLoader(mustacheTemplateLoader);
    }

    @ConditionalOnMissingBean({Mustache.TemplateLoader.class})
    @Bean
    public MustacheResourceTemplateLoader mustacheTemplateLoader() {
        MustacheResourceTemplateLoader loader = new MustacheResourceTemplateLoader(this.mustache.getPrefix(), this.mustache.getSuffix());
        loader.setCharset(this.mustache.getCharsetName());
        return loader;
    }
}
