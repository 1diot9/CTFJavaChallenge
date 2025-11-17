package org.springframework.boot.context.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataEnvironmentPostProcessor.class */
public class ConfigDataEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    public static final int ORDER = -2147483638;
    public static final String ON_LOCATION_NOT_FOUND_PROPERTY = "spring.config.on-not-found";
    private final DeferredLogFactory logFactory;
    private final Log logger;
    private final ConfigurableBootstrapContext bootstrapContext;
    private final ConfigDataEnvironmentUpdateListener environmentUpdateListener;

    public ConfigDataEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext) {
        this(logFactory, bootstrapContext, null);
    }

    private ConfigDataEnvironmentPostProcessor(DeferredLogFactory logFactory, ConfigurableBootstrapContext bootstrapContext, ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        this.logFactory = logFactory;
        this.logger = logFactory.getLog(getClass());
        this.bootstrapContext = bootstrapContext;
        this.environmentUpdateListener = environmentUpdateListener;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return -2147483638;
    }

    @Override // org.springframework.boot.env.EnvironmentPostProcessor
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        postProcessEnvironment(environment, application.getResourceLoader(), application.getAdditionalProfiles());
    }

    void postProcessEnvironment(ConfigurableEnvironment environment, ResourceLoader resourceLoader, Collection<String> additionalProfiles) {
        this.logger.trace("Post-processing environment to add config data");
        getConfigDataEnvironment(environment, resourceLoader != null ? resourceLoader : new DefaultResourceLoader(), additionalProfiles).processAndApply();
    }

    ConfigDataEnvironment getConfigDataEnvironment(ConfigurableEnvironment environment, ResourceLoader resourceLoader, Collection<String> additionalProfiles) {
        return new ConfigDataEnvironment(this.logFactory, this.bootstrapContext, environment, resourceLoader, additionalProfiles, this.environmentUpdateListener);
    }

    public static void applyTo(ConfigurableEnvironment environment) {
        applyTo(environment, (ResourceLoader) null, (ConfigurableBootstrapContext) null, Collections.emptyList());
    }

    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader, ConfigurableBootstrapContext bootstrapContext, String... additionalProfiles) {
        applyTo(environment, resourceLoader, bootstrapContext, Arrays.asList(additionalProfiles));
    }

    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader, ConfigurableBootstrapContext bootstrapContext, Collection<String> additionalProfiles) {
        DeferredLogFactory logFactory = (v0) -> {
            return v0.get();
        };
        ConfigDataEnvironmentPostProcessor postProcessor = new ConfigDataEnvironmentPostProcessor(logFactory, bootstrapContext != null ? bootstrapContext : new DefaultBootstrapContext());
        postProcessor.postProcessEnvironment(environment, resourceLoader, additionalProfiles);
    }

    public static void applyTo(ConfigurableEnvironment environment, ResourceLoader resourceLoader, ConfigurableBootstrapContext bootstrapContext, Collection<String> additionalProfiles, ConfigDataEnvironmentUpdateListener environmentUpdateListener) {
        DeferredLogFactory logFactory = (v0) -> {
            return v0.get();
        };
        ConfigDataEnvironmentPostProcessor postProcessor = new ConfigDataEnvironmentPostProcessor(logFactory, bootstrapContext != null ? bootstrapContext : new DefaultBootstrapContext(), environmentUpdateListener);
        postProcessor.postProcessEnvironment(environment, resourceLoader, additionalProfiles);
    }
}
