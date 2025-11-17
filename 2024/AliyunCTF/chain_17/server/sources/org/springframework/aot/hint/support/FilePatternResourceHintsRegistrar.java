package org.springframework.aot.hint.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.springframework.aot.hint.ResourceHints;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/FilePatternResourceHintsRegistrar.class */
public class FilePatternResourceHintsRegistrar {
    private final List<String> classpathLocations;
    private final List<String> filePrefixes;
    private final List<String> fileExtensions;

    @Deprecated(since = "6.0.12", forRemoval = true)
    public FilePatternResourceHintsRegistrar(List<String> filePrefixes, List<String> classpathLocations, List<String> fileExtensions) {
        this.classpathLocations = validateClasspathLocations(classpathLocations);
        this.filePrefixes = validateFilePrefixes(filePrefixes);
        this.fileExtensions = validateFileExtensions(fileExtensions);
    }

    @Deprecated(since = "6.0.12", forRemoval = true)
    public void registerHints(ResourceHints hints, @Nullable ClassLoader classLoader) {
        ClassLoader classLoaderToUse = classLoader != null ? classLoader : getClass().getClassLoader();
        List<String> includes = new ArrayList<>();
        for (String location : this.classpathLocations) {
            if (classLoaderToUse.getResource(location) != null) {
                for (String filePrefix : this.filePrefixes) {
                    for (String fileExtension : this.fileExtensions) {
                        includes.add(location + filePrefix + "*" + fileExtension);
                    }
                }
            }
        }
        if (!includes.isEmpty()) {
            hints.registerPattern(hint -> {
                hint.includes((String[]) includes.toArray(x$0 -> {
                    return new String[x$0];
                }));
            });
        }
    }

    public static Builder forClassPathLocations(String... classpathLocations) {
        return forClassPathLocations((List<String>) Arrays.asList(classpathLocations));
    }

    public static Builder forClassPathLocations(List<String> classpathLocations) {
        return new Builder().withClasspathLocations(classpathLocations);
    }

    private static List<String> validateClasspathLocations(List<String> classpathLocations) {
        Assert.notEmpty(classpathLocations, "At least one classpath location must be specified");
        List<String> parsedLocations = new ArrayList<>();
        Iterator<String> it = classpathLocations.iterator();
        while (it.hasNext()) {
            String location = it.next();
            if (location.startsWith("classpath:")) {
                location = location.substring("classpath:".length());
            }
            if (location.startsWith("/")) {
                location = location.substring(1);
            }
            if (!location.isEmpty() && !location.endsWith("/")) {
                location = location + "/";
            }
            parsedLocations.add(location);
        }
        return parsedLocations;
    }

    private static List<String> validateFilePrefixes(List<String> filePrefixes) {
        for (String filePrefix : filePrefixes) {
            if (filePrefix.contains("*")) {
                throw new IllegalArgumentException("File prefix '" + filePrefix + "' cannot contain '*'");
            }
        }
        return filePrefixes;
    }

    private static List<String> validateFileExtensions(List<String> fileExtensions) {
        for (String fileExtension : fileExtensions) {
            if (!fileExtension.startsWith(".")) {
                throw new IllegalArgumentException("Extension '" + fileExtension + "' must start with '.'");
            }
        }
        return fileExtensions;
    }

    /* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/aot/hint/support/FilePatternResourceHintsRegistrar$Builder.class */
    public static final class Builder {
        private final List<String> classpathLocations = new ArrayList();
        private final List<String> filePrefixes = new ArrayList();
        private final List<String> fileExtensions = new ArrayList();

        private Builder() {
        }

        public Builder withClasspathLocations(String... classpathLocations) {
            return withClasspathLocations(Arrays.asList(classpathLocations));
        }

        public Builder withClasspathLocations(List<String> classpathLocations) {
            this.classpathLocations.addAll(FilePatternResourceHintsRegistrar.validateClasspathLocations(classpathLocations));
            return this;
        }

        public Builder withFilePrefixes(String... filePrefixes) {
            return withFilePrefixes(Arrays.asList(filePrefixes));
        }

        public Builder withFilePrefixes(List<String> filePrefixes) {
            this.filePrefixes.addAll(FilePatternResourceHintsRegistrar.validateFilePrefixes(filePrefixes));
            return this;
        }

        public Builder withFileExtensions(String... fileExtensions) {
            return withFileExtensions(Arrays.asList(fileExtensions));
        }

        public Builder withFileExtensions(List<String> fileExtensions) {
            this.fileExtensions.addAll(FilePatternResourceHintsRegistrar.validateFileExtensions(fileExtensions));
            return this;
        }

        private FilePatternResourceHintsRegistrar build() {
            return new FilePatternResourceHintsRegistrar(this.filePrefixes, this.classpathLocations, this.fileExtensions);
        }

        public void registerHints(ResourceHints hints, @Nullable ClassLoader classLoader) {
            build().registerHints(hints, classLoader);
        }
    }
}
