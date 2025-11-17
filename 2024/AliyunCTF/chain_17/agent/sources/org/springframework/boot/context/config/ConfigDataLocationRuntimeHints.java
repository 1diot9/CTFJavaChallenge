package org.springframework.boot.context.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.support.FilePatternResourceHintsRegistrar;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/ConfigDataLocationRuntimeHints.class */
class ConfigDataLocationRuntimeHints implements RuntimeHintsRegistrar {
    private static final Log logger = LogFactory.getLog((Class<?>) ConfigDataLocationRuntimeHints.class);

    ConfigDataLocationRuntimeHints() {
    }

    @Override // org.springframework.aot.hint.RuntimeHintsRegistrar
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        List<String> fileNames = getFileNames(classLoader);
        List<String> locations = getLocations(classLoader);
        List<String> extensions = getExtensions(classLoader);
        if (logger.isDebugEnabled()) {
            logger.debug("Registering application configuration hints for " + fileNames + "(" + extensions + ") at " + locations);
        }
        FilePatternResourceHintsRegistrar.forClassPathLocations(locations).withFilePrefixes(fileNames).withFileExtensions(extensions).registerHints(hints.resources(), classLoader);
    }

    protected List<String> getFileNames(ClassLoader classLoader) {
        return Arrays.asList(StandardConfigDataLocationResolver.DEFAULT_CONFIG_NAMES);
    }

    protected List<String> getLocations(ClassLoader classLoader) {
        List<String> classpathLocations = new ArrayList<>();
        for (ConfigDataLocation candidate : ConfigDataEnvironment.DEFAULT_SEARCH_LOCATIONS) {
            for (ConfigDataLocation configDataLocation : candidate.split()) {
                String location = configDataLocation.getValue();
                if (location.startsWith("classpath:")) {
                    classpathLocations.add(location);
                }
            }
        }
        return classpathLocations;
    }

    protected List<String> getExtensions(ClassLoader classLoader) {
        List<String> extensions = new ArrayList<>();
        List<PropertySourceLoader> propertySourceLoaders = getSpringFactoriesLoader(classLoader).load(PropertySourceLoader.class);
        for (PropertySourceLoader propertySourceLoader : propertySourceLoaders) {
            for (String fileExtension : propertySourceLoader.getFileExtensions()) {
                String candidate = "." + fileExtension;
                if (!extensions.contains(candidate)) {
                    extensions.add(candidate);
                }
            }
        }
        return extensions;
    }

    protected SpringFactoriesLoader getSpringFactoriesLoader(ClassLoader classLoader) {
        return SpringFactoriesLoader.forDefaultResourceLocation(classLoader);
    }
}
