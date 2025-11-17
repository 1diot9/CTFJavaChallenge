package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: server.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/PathResourceResolver.class */
public class PathResourceResolver extends AbstractResourceResolver {

    @Nullable
    private Resource[] allowedLocations;
    private final Map<Resource, Charset> locationCharsets = new HashMap(4);

    @Nullable
    private UrlPathHelper urlPathHelper;

    public void setAllowedLocations(@Nullable Resource... locations) {
        this.allowedLocations = locations;
    }

    @Nullable
    public Resource[] getAllowedLocations() {
        return this.allowedLocations;
    }

    public void setLocationCharsets(Map<Resource, Charset> locationCharsets) {
        this.locationCharsets.clear();
        this.locationCharsets.putAll(locationCharsets);
    }

    public Map<Resource, Charset> getLocationCharsets() {
        return Collections.unmodifiableMap(this.locationCharsets);
    }

    public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return getResource(requestPath, request, locations);
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected String resolveUrlPathInternal(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
        if (!StringUtils.hasText(resourcePath) || getResource(resourcePath, null, locations) == null) {
            return null;
        }
        return resourcePath;
    }

    @Nullable
    private Resource getResource(String resourcePath, @Nullable HttpServletRequest request, List<? extends Resource> locations) {
        Resource resource;
        for (Resource location : locations) {
            try {
                String pathToUse = encodeOrDecodeIfNecessary(resourcePath, request, location);
                resource = getResource(pathToUse, location);
            } catch (IOException ex) {
                if (this.logger.isDebugEnabled()) {
                    String error = "Skip location [" + location + "] due to error";
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace(error, ex);
                    } else {
                        this.logger.debug(error + ": " + ex.getMessage());
                    }
                }
            }
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Nullable
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        Resource resource = location.createRelative(resourcePath);
        if (resource.isReadable()) {
            if (checkResource(resource, location)) {
                return resource;
            }
            if (this.logger.isWarnEnabled()) {
                Resource[] allowed = getAllowedLocations();
                this.logger.warn(LogFormatUtils.formatValue("Resource path \"" + resourcePath + "\" was successfully resolved but resource \"" + resource + "\" is neither under the current location \"" + location + "\" nor under any of the allowed locations " + (allowed != null ? Arrays.asList(allowed) : ClassUtils.ARRAY_SUFFIX), -1, true));
                return null;
            }
            return null;
        }
        return null;
    }

    protected boolean checkResource(Resource resource, Resource location) throws IOException {
        if (isResourceUnderLocation(resource, location)) {
            return true;
        }
        Resource[] allowedLocations = getAllowedLocations();
        if (allowedLocations != null) {
            for (Resource current : allowedLocations) {
                if (isResourceUnderLocation(resource, current)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isResourceUnderLocation(Resource resource, Resource location) throws IOException {
        String resourcePath;
        String locationPath;
        if (resource.getClass() != location.getClass()) {
            return false;
        }
        if (resource instanceof UrlResource) {
            resourcePath = resource.getURL().toExternalForm();
            locationPath = StringUtils.cleanPath(location.getURL().toString());
        } else if (resource instanceof ClassPathResource) {
            ClassPathResource classPathResource = (ClassPathResource) resource;
            resourcePath = classPathResource.getPath();
            locationPath = StringUtils.cleanPath(((ClassPathResource) location).getPath());
        } else if (resource instanceof ServletContextResource) {
            ServletContextResource servletContextResource = (ServletContextResource) resource;
            resourcePath = servletContextResource.getPath();
            locationPath = StringUtils.cleanPath(((ServletContextResource) location).getPath());
        } else {
            resourcePath = resource.getURL().getPath();
            locationPath = StringUtils.cleanPath(location.getURL().getPath());
        }
        if (locationPath.equals(resourcePath)) {
            return true;
        }
        return resourcePath.startsWith((locationPath.endsWith("/") || locationPath.isEmpty()) ? locationPath : locationPath + "/") && !isInvalidEncodedPath(resourcePath);
    }

    private String encodeOrDecodeIfNecessary(String path, @Nullable HttpServletRequest request, Resource location) {
        if (request != null) {
            boolean usesPathPattern = ServletRequestPathUtils.hasCachedPath(request) && (ServletRequestPathUtils.getCachedPath(request) instanceof PathContainer);
            if (shouldDecodeRelativePath(location, usesPathPattern)) {
                return UriUtils.decode(path, StandardCharsets.UTF_8);
            }
            if (shouldEncodeRelativePath(location, usesPathPattern)) {
                Charset charset = this.locationCharsets.getOrDefault(location, StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                StringTokenizer tokenizer = new StringTokenizer(path, "/");
                while (tokenizer.hasMoreTokens()) {
                    String value = UriUtils.encode(tokenizer.nextToken(), charset);
                    sb.append(value);
                    sb.append('/');
                }
                if (!path.endsWith("/")) {
                    sb.setLength(sb.length() - 1);
                }
                return sb.toString();
            }
        }
        return path;
    }

    private boolean shouldDecodeRelativePath(Resource location, boolean usesPathPattern) {
        return !(location instanceof UrlResource) && (usesPathPattern || !(this.urlPathHelper == null || this.urlPathHelper.isUrlDecode()));
    }

    private boolean shouldEncodeRelativePath(Resource location, boolean usesPathPattern) {
        return (location instanceof UrlResource) && !usesPathPattern && this.urlPathHelper != null && this.urlPathHelper.isUrlDecode();
    }

    private boolean isInvalidEncodedPath(String resourcePath) {
        if (resourcePath.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            try {
                String decodedPath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8);
                if (decodedPath.contains("../") || decodedPath.contains("..\\")) {
                    this.logger.warn(LogFormatUtils.formatValue("Resolved resource path contains encoded \"../\" or \"..\\\": " + resourcePath, -1, true));
                    return true;
                }
                return false;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }
}
