package org.springframework.web.servlet.resource;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/VersionResourceResolver.class */
public class VersionResourceResolver extends AbstractResourceResolver {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final Map<String, VersionStrategy> versionStrategyMap = new LinkedHashMap();

    public void setStrategyMap(Map<String, VersionStrategy> map) {
        this.versionStrategyMap.clear();
        this.versionStrategyMap.putAll(map);
    }

    public Map<String, VersionStrategy> getStrategyMap() {
        return this.versionStrategyMap;
    }

    public VersionResourceResolver addContentVersionStrategy(String... pathPatterns) {
        addVersionStrategy(new ContentVersionStrategy(), pathPatterns);
        return this;
    }

    public VersionResourceResolver addFixedVersionStrategy(String version, String... pathPatterns) {
        List<String> patternsList = Arrays.asList(pathPatterns);
        List<String> prefixedPatterns = new ArrayList<>(pathPatterns.length);
        String versionPrefix = "/" + version;
        for (String pattern : patternsList) {
            prefixedPatterns.add(pattern);
            if (!pattern.startsWith(versionPrefix) && !patternsList.contains(versionPrefix + pattern)) {
                prefixedPatterns.add(versionPrefix + pattern);
            }
        }
        return addVersionStrategy(new FixedVersionStrategy(version), StringUtils.toStringArray(prefixedPatterns));
    }

    public VersionResourceResolver addVersionStrategy(VersionStrategy strategy, String... pathPatterns) {
        for (String pattern : pathPatterns) {
            getStrategyMap().put(pattern, strategy);
        }
        return this;
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        Resource resolved = chain.resolveResource(request, requestPath, locations);
        if (resolved != null) {
            return resolved;
        }
        VersionStrategy versionStrategy = getStrategyForPath(requestPath);
        if (versionStrategy == null) {
            return null;
        }
        String candidateVersion = versionStrategy.extractVersion(requestPath);
        if (!StringUtils.hasLength(candidateVersion)) {
            return null;
        }
        String simplePath = versionStrategy.removeVersion(requestPath, candidateVersion);
        Resource baseResource = chain.resolveResource(request, simplePath, locations);
        if (baseResource == null) {
            return null;
        }
        String actualVersion = versionStrategy.getResourceVersion(baseResource);
        if (candidateVersion.equals(actualVersion)) {
            return new FileNameVersionedResource(baseResource, candidateVersion);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Found resource for \"" + requestPath + "\", but version [" + candidateVersion + "] does not match");
            return null;
        }
        return null;
    }

    @Override // org.springframework.web.servlet.resource.AbstractResourceResolver
    @Nullable
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String baseUrl = chain.resolveUrlPath(resourceUrlPath, locations);
        if (StringUtils.hasText(baseUrl)) {
            VersionStrategy versionStrategy = getStrategyForPath(resourceUrlPath);
            if (versionStrategy == null) {
                return baseUrl;
            }
            Resource resource = chain.resolveResource(null, baseUrl, locations);
            Assert.state(resource != null, "Unresolvable resource");
            String version = versionStrategy.getResourceVersion(resource);
            return versionStrategy.addVersion(baseUrl, version);
        }
        return baseUrl;
    }

    @Nullable
    protected VersionStrategy getStrategyForPath(String requestPath) {
        String path = "/".concat(requestPath);
        List<String> matchingPatterns = new ArrayList<>();
        for (String pattern : this.versionStrategyMap.keySet()) {
            if (this.pathMatcher.match(pattern, path)) {
                matchingPatterns.add(pattern);
            }
        }
        if (!matchingPatterns.isEmpty()) {
            Comparator<String> comparator = this.pathMatcher.getPatternComparator(path);
            matchingPatterns.sort(comparator);
            return this.versionStrategyMap.get(matchingPatterns.get(0));
        }
        return null;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/VersionResourceResolver$FileNameVersionedResource.class */
    private static class FileNameVersionedResource extends AbstractResource implements HttpResource {
        private final Resource original;
        private final String version;

        public FileNameVersionedResource(Resource original, String version) {
            this.original = original;
            this.version = version;
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean exists() {
            return this.original.exists();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean isReadable() {
            return this.original.isReadable();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean isOpen() {
            return this.original.isOpen();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public boolean isFile() {
            return this.original.isFile();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public URL getURL() throws IOException {
            return this.original.getURL();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public URI getURI() throws IOException {
            return this.original.getURI();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public File getFile() throws IOException {
            return this.original.getFile();
        }

        @Override // org.springframework.core.io.InputStreamSource
        public InputStream getInputStream() throws IOException {
            return this.original.getInputStream();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public ReadableByteChannel readableChannel() throws IOException {
            return this.original.readableChannel();
        }

        @Override // org.springframework.core.io.Resource
        public byte[] getContentAsByteArray() throws IOException {
            return this.original.getContentAsByteArray();
        }

        @Override // org.springframework.core.io.Resource
        public String getContentAsString(Charset charset) throws IOException {
            return this.original.getContentAsString(charset);
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public long contentLength() throws IOException {
            return this.original.contentLength();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public long lastModified() throws IOException {
            return this.original.lastModified();
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        public Resource createRelative(String relativePath) throws IOException {
            return this.original.createRelative(relativePath);
        }

        @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
        @Nullable
        public String getFilename() {
            return this.original.getFilename();
        }

        @Override // org.springframework.core.io.Resource
        public String getDescription() {
            return this.original.getDescription();
        }

        @Override // org.springframework.web.servlet.resource.HttpResource
        public HttpHeaders getResponseHeaders() {
            HttpHeaders httpHeaders;
            Resource resource = this.original;
            if (resource instanceof HttpResource) {
                HttpResource httpResource = (HttpResource) resource;
                httpHeaders = httpResource.getResponseHeaders();
            } else {
                httpHeaders = new HttpHeaders();
            }
            HttpHeaders headers = httpHeaders;
            headers.setETag("W/\"" + this.version + "\"");
            return headers;
        }
    }
}
