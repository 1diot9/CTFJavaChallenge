package org.springframework.web.servlet.function;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.server.PathContainer;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/PathResourceLookupFunction.class */
public class PathResourceLookupFunction implements Function<ServerRequest, Optional<Resource>> {
    private final PathPattern pattern;
    private final Resource location;

    public PathResourceLookupFunction(String pattern, Resource location) {
        Assert.hasLength(pattern, "'pattern' must not be empty");
        Assert.notNull(location, "'location' must not be null");
        this.pattern = PathPatternParser.defaultInstance.parse(pattern);
        this.location = location;
    }

    @Override // java.util.function.Function
    public Optional<Resource> apply(ServerRequest request) {
        PathContainer pathContainer = request.requestPath().pathWithinApplication();
        if (!this.pattern.matches(pathContainer)) {
            return Optional.empty();
        }
        String path = processPath(this.pattern.extractPathWithinPattern(pathContainer).value());
        if (path.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            path = StringUtils.uriDecode(path, StandardCharsets.UTF_8);
        }
        if (!StringUtils.hasLength(path) || isInvalidPath(path)) {
            return Optional.empty();
        }
        try {
            Resource resource = this.location.createRelative(path);
            if (resource.isReadable() && isResourceUnderLocation(resource)) {
                return Optional.of(resource);
            }
            return Optional.empty();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String processPath(String path) {
        boolean slash = false;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/') {
                slash = true;
            } else if (path.charAt(i) > ' ' && path.charAt(i) != 127) {
                if (i == 0 || (i == 1 && slash)) {
                    return path;
                }
                return slash ? "/" + path.substring(i) : path.substring(i);
            }
        }
        return slash ? "/" : "";
    }

    private boolean isInvalidPath(String path) {
        if (path.contains("WEB-INF") || path.contains("META-INF")) {
            return true;
        }
        if (path.contains(":/")) {
            String relativePath = path.charAt(0) == '/' ? path.substring(1) : path;
            if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
                return true;
            }
        }
        return path.contains(CallerDataConverter.DEFAULT_RANGE_DELIMITER) && StringUtils.cleanPath(path).contains("../");
    }

    private boolean isResourceUnderLocation(Resource resource) throws IOException {
        String resourcePath;
        String locationPath;
        if (resource.getClass() != this.location.getClass()) {
            return false;
        }
        if (resource instanceof UrlResource) {
            resourcePath = resource.getURL().toExternalForm();
            locationPath = StringUtils.cleanPath(this.location.getURL().toString());
        } else if (resource instanceof ClassPathResource) {
            ClassPathResource classPathResource = (ClassPathResource) resource;
            resourcePath = classPathResource.getPath();
            locationPath = StringUtils.cleanPath(((ClassPathResource) this.location).getPath());
        } else {
            resourcePath = resource.getURL().getPath();
            locationPath = StringUtils.cleanPath(this.location.getURL().getPath());
        }
        if (locationPath.equals(resourcePath)) {
            return true;
        }
        if (resourcePath.startsWith((locationPath.endsWith("/") || locationPath.isEmpty()) ? locationPath : locationPath + "/")) {
            return (resourcePath.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL) && StringUtils.uriDecode(resourcePath, StandardCharsets.UTF_8).contains("../")) ? false : true;
        }
        return false;
    }

    public String toString() {
        return this.pattern + " -> " + this.location;
    }
}
