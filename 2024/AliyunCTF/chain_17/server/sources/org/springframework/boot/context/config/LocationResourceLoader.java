package org.springframework.boot.context.config;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/LocationResourceLoader.class */
class LocationResourceLoader {
    private static final Resource[] EMPTY_RESOURCES = new Resource[0];
    private static final Comparator<File> FILE_PATH_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.getAbsolutePath();
    });
    private static final Comparator<File> FILE_NAME_COMPARATOR = Comparator.comparing((v0) -> {
        return v0.getName();
    });
    private final ResourceLoader resourceLoader;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/context/config/LocationResourceLoader$ResourceType.class */
    public enum ResourceType {
        FILE,
        DIRECTORY
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LocationResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isPattern(String location) {
        return StringUtils.hasLength(location) && location.contains("*");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Resource getResource(String location) {
        validateNonPattern(location);
        String location2 = StringUtils.cleanPath(location);
        if (!ResourceUtils.isUrl(location2)) {
            location2 = "file:" + location2;
        }
        return this.resourceLoader.getResource(location2);
    }

    private void validateNonPattern(String location) {
        Assert.state(!isPattern(location), (Supplier<String>) () -> {
            return String.format("Location '%s' must not be a pattern", location);
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Resource[] getResources(String location, ResourceType type) {
        validatePattern(location, type);
        String directoryPath = location.substring(0, location.indexOf(ResourceUtils.WAR_URL_SEPARATOR));
        String fileName = location.substring(location.lastIndexOf("/") + 1);
        Resource resource = getResource(directoryPath);
        if (!resource.exists()) {
            return EMPTY_RESOURCES;
        }
        File file = getFile(location, resource);
        if (!file.isDirectory()) {
            return EMPTY_RESOURCES;
        }
        File[] subDirectories = file.listFiles(this::isVisibleDirectory);
        if (subDirectories == null) {
            return EMPTY_RESOURCES;
        }
        Arrays.sort(subDirectories, FILE_PATH_COMPARATOR);
        if (type == ResourceType.DIRECTORY) {
            return (Resource[]) Arrays.stream(subDirectories).map(FileSystemResource::new).toArray(x$0 -> {
                return new Resource[x$0];
            });
        }
        List<Resource> resources = new ArrayList<>();
        FilenameFilter filter = (dir, name) -> {
            return name.equals(fileName);
        };
        for (File subDirectory : subDirectories) {
            File[] files = subDirectory.listFiles(filter);
            if (files != null) {
                Arrays.sort(files, FILE_NAME_COMPARATOR);
                Stream map = Arrays.stream(files).map(FileSystemResource::new);
                Objects.requireNonNull(resources);
                map.forEach((v1) -> {
                    r1.add(v1);
                });
            }
        }
        return (Resource[]) resources.toArray(EMPTY_RESOURCES);
    }

    private void validatePattern(String location, ResourceType type) {
        Assert.state(isPattern(location), (Supplier<String>) () -> {
            return String.format("Location '%s' must be a pattern", location);
        });
        Assert.state(!location.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX), (Supplier<String>) () -> {
            return String.format("Location '%s' cannot use classpath wildcards", location);
        });
        Assert.state(StringUtils.countOccurrencesOf(location, "*") == 1, (Supplier<String>) () -> {
            return String.format("Location '%s' cannot contain multiple wildcards", location);
        });
        String directoryPath = type != ResourceType.DIRECTORY ? location.substring(0, location.lastIndexOf("/") + 1) : location;
        Assert.state(directoryPath.endsWith(ResourceUtils.WAR_URL_SEPARATOR), (Supplier<String>) () -> {
            return String.format("Location '%s' must end with '*/'", location);
        });
    }

    private File getFile(String patternLocation, Resource resource) {
        try {
            return resource.getFile();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to load config data resource from pattern '" + patternLocation + "'", ex);
        }
    }

    private boolean isVisibleDirectory(File file) {
        return file.isDirectory() && !file.getName().startsWith(CallerDataConverter.DEFAULT_RANGE_DELIMITER);
    }
}
