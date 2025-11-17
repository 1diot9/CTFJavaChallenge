package org.springframework.web.servlet.resource;

import cn.hutool.core.text.StrPool;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.target.QuickTargetSourceCreator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/resource/ResourceHttpRequestHandler.class */
public class ResourceHttpRequestHandler extends WebContentGenerator implements HttpRequestHandler, EmbeddedValueResolverAware, InitializingBean, CorsConfigurationSource {
    private static final Log logger = LogFactory.getLog((Class<?>) ResourceHttpRequestHandler.class);
    private static final String URL_RESOURCE_CHARSET_PREFIX = "[charset=";
    private final List<String> locationValues;
    private final List<Resource> locationResources;
    private final List<Resource> locationsToUse;
    private final Map<Resource, Charset> locationCharsets;
    private final List<ResourceResolver> resourceResolvers;
    private final List<ResourceTransformer> resourceTransformers;

    @Nullable
    private ResourceResolverChain resolverChain;

    @Nullable
    private ResourceTransformerChain transformerChain;

    @Nullable
    private ResourceHttpMessageConverter resourceHttpMessageConverter;

    @Nullable
    private ResourceRegionHttpMessageConverter resourceRegionHttpMessageConverter;

    @Nullable
    private ContentNegotiationManager contentNegotiationManager;
    private final Map<String, MediaType> mediaTypes;

    @Nullable
    private CorsConfiguration corsConfiguration;

    @Nullable
    private UrlPathHelper urlPathHelper;
    private boolean useLastModified;

    @Nullable
    private Function<Resource, String> etagGenerator;
    private boolean optimizeLocations;

    @Nullable
    private StringValueResolver embeddedValueResolver;

    public ResourceHttpRequestHandler() {
        super(HttpMethod.GET.name(), HttpMethod.HEAD.name());
        this.locationValues = new ArrayList(4);
        this.locationResources = new ArrayList(4);
        this.locationsToUse = new ArrayList(4);
        this.locationCharsets = new HashMap(4);
        this.resourceResolvers = new ArrayList(4);
        this.resourceTransformers = new ArrayList(4);
        this.mediaTypes = new HashMap(4);
        this.useLastModified = true;
        this.optimizeLocations = false;
    }

    public void setLocationValues(List<String> locations) {
        Assert.notNull(locations, "Locations list must not be null");
        this.locationValues.clear();
        this.locationValues.addAll(locations);
    }

    public void setLocations(List<Resource> locations) {
        Assert.notNull(locations, "Locations list must not be null");
        this.locationResources.clear();
        this.locationResources.addAll(locations);
    }

    public List<Resource> getLocations() {
        if (this.locationsToUse.isEmpty()) {
            return this.locationResources;
        }
        return this.locationsToUse;
    }

    public void setResourceResolvers(@Nullable List<ResourceResolver> resourceResolvers) {
        this.resourceResolvers.clear();
        if (resourceResolvers != null) {
            this.resourceResolvers.addAll(resourceResolvers);
        }
    }

    public List<ResourceResolver> getResourceResolvers() {
        return this.resourceResolvers;
    }

    public void setResourceTransformers(@Nullable List<ResourceTransformer> resourceTransformers) {
        this.resourceTransformers.clear();
        if (resourceTransformers != null) {
            this.resourceTransformers.addAll(resourceTransformers);
        }
    }

    public List<ResourceTransformer> getResourceTransformers() {
        return this.resourceTransformers;
    }

    public void setResourceHttpMessageConverter(@Nullable ResourceHttpMessageConverter messageConverter) {
        this.resourceHttpMessageConverter = messageConverter;
    }

    @Nullable
    public ResourceHttpMessageConverter getResourceHttpMessageConverter() {
        return this.resourceHttpMessageConverter;
    }

    public void setResourceRegionHttpMessageConverter(@Nullable ResourceRegionHttpMessageConverter messageConverter) {
        this.resourceRegionHttpMessageConverter = messageConverter;
    }

    @Nullable
    public ResourceRegionHttpMessageConverter getResourceRegionHttpMessageConverter() {
        return this.resourceRegionHttpMessageConverter;
    }

    @Deprecated
    public void setContentNegotiationManager(@Nullable ContentNegotiationManager contentNegotiationManager) {
        this.contentNegotiationManager = contentNegotiationManager;
    }

    @Nullable
    @Deprecated
    public ContentNegotiationManager getContentNegotiationManager() {
        return this.contentNegotiationManager;
    }

    public void setMediaTypes(Map<String, MediaType> mediaTypes) {
        mediaTypes.forEach((ext, mediaType) -> {
            this.mediaTypes.put(ext.toLowerCase(Locale.ENGLISH), mediaType);
        });
    }

    public Map<String, MediaType> getMediaTypes() {
        return this.mediaTypes;
    }

    public void setCorsConfiguration(CorsConfiguration corsConfiguration) {
        this.corsConfiguration = corsConfiguration;
    }

    @Override // org.springframework.web.cors.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        return this.corsConfiguration;
    }

    public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    public void setUseLastModified(boolean useLastModified) {
        this.useLastModified = useLastModified;
    }

    public boolean isUseLastModified() {
        return this.useLastModified;
    }

    public void setEtagGenerator(@Nullable Function<Resource, String> etagGenerator) {
        this.etagGenerator = etagGenerator;
    }

    @Nullable
    public Function<Resource, String> getEtagGenerator() {
        return this.etagGenerator;
    }

    public void setOptimizeLocations(boolean optimizeLocations) {
        this.optimizeLocations = optimizeLocations;
    }

    public boolean isOptimizeLocations() {
        return this.optimizeLocations;
    }

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        resolveResourceLocations();
        if (this.resourceResolvers.isEmpty()) {
            this.resourceResolvers.add(new PathResourceResolver());
        }
        initAllowedLocations();
        this.resolverChain = new DefaultResourceResolverChain(this.resourceResolvers);
        this.transformerChain = new DefaultResourceTransformerChain(this.resolverChain, this.resourceTransformers);
        if (this.resourceHttpMessageConverter == null) {
            this.resourceHttpMessageConverter = new ResourceHttpMessageConverter();
        }
        if (this.resourceRegionHttpMessageConverter == null) {
            this.resourceRegionHttpMessageConverter = new ResourceRegionHttpMessageConverter();
        }
        ContentNegotiationManager manager = getContentNegotiationManager();
        if (manager != null) {
            setMediaTypes(manager.getMediaTypeMappings());
        }
        PathExtensionContentNegotiationStrategy strategy = initContentNegotiationStrategy();
        if (strategy != null) {
            setMediaTypes(strategy.getMediaTypes());
        }
    }

    private void resolveResourceLocations() {
        List<Resource> result = new ArrayList<>();
        if (!this.locationValues.isEmpty()) {
            ApplicationContext applicationContext = obtainApplicationContext();
            for (String location : this.locationValues) {
                if (this.embeddedValueResolver != null) {
                    String resolvedLocation = this.embeddedValueResolver.resolveStringValue(location);
                    if (resolvedLocation == null) {
                        throw new IllegalArgumentException("Location resolved to null: " + location);
                    }
                    location = resolvedLocation;
                }
                Charset charset = null;
                String location2 = location.trim();
                if (location2.startsWith(URL_RESOURCE_CHARSET_PREFIX)) {
                    int endIndex = location2.indexOf(93, URL_RESOURCE_CHARSET_PREFIX.length());
                    if (endIndex == -1) {
                        throw new IllegalArgumentException("Invalid charset syntax in location: " + location2);
                    }
                    String value = location2.substring(URL_RESOURCE_CHARSET_PREFIX.length(), endIndex);
                    charset = Charset.forName(value);
                    location2 = location2.substring(endIndex + 1);
                }
                Resource resource = applicationContext.getResource(location2);
                if (location2.equals("/") && !(resource instanceof ServletContextResource)) {
                    throw new IllegalStateException("The String-based location \"/\" should be relative to the web application root but resolved to a Resource of type: " + resource.getClass() + ". If this is intentional, please pass it as a pre-configured Resource via setLocations.");
                }
                result.add(resource);
                if (charset != null) {
                    if (!(resource instanceof UrlResource)) {
                        throw new IllegalArgumentException("Unexpected charset for non-UrlResource: " + resource);
                    }
                    this.locationCharsets.put(resource, charset);
                }
            }
        }
        result.addAll(this.locationResources);
        if (isOptimizeLocations()) {
            result = result.stream().filter((v0) -> {
                return v0.exists();
            }).toList();
        }
        this.locationsToUse.clear();
        this.locationsToUse.addAll(result);
    }

    protected void initAllowedLocations() {
        if (CollectionUtils.isEmpty(getLocations())) {
            return;
        }
        for (int i = getResourceResolvers().size() - 1; i >= 0; i--) {
            ResourceResolver resourceResolver = getResourceResolvers().get(i);
            if (resourceResolver instanceof PathResourceResolver) {
                PathResourceResolver pathResolver = (PathResourceResolver) resourceResolver;
                if (ObjectUtils.isEmpty((Object[]) pathResolver.getAllowedLocations())) {
                    pathResolver.setAllowedLocations((Resource[]) getLocations().toArray(new Resource[0]));
                }
                if (this.urlPathHelper != null) {
                    pathResolver.setLocationCharsets(this.locationCharsets);
                    pathResolver.setUrlPathHelper(this.urlPathHelper);
                    return;
                }
                return;
            }
        }
    }

    @Nullable
    @Deprecated
    protected PathExtensionContentNegotiationStrategy initContentNegotiationStrategy() {
        return null;
    }

    @Override // org.springframework.web.HttpRequestHandler
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Resource resource = getResource(request);
        if (resource == null) {
            logger.debug("Resource not found");
            throw new NoResourceFoundException(HttpMethod.valueOf(request.getMethod()), getPath(request));
        }
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader(HttpHeaders.ALLOW, getAllowHeader());
            return;
        }
        checkRequest(request);
        String eTagValue = getEtagGenerator() != null ? getEtagGenerator().apply(resource) : null;
        long lastModified = isUseLastModified() ? resource.lastModified() : -1L;
        if (new ServletWebRequest(request, response).checkNotModified(eTagValue, lastModified)) {
            logger.trace("Resource not modified");
            return;
        }
        prepareResponse(response);
        MediaType mediaType = getMediaType(request, resource);
        setHeaders(response, resource, mediaType);
        ServletServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        if (request.getHeader(HttpHeaders.RANGE) == null) {
            Assert.state(this.resourceHttpMessageConverter != null, "Not initialized");
            if (HttpMethod.HEAD.matches(request.getMethod())) {
                this.resourceHttpMessageConverter.addDefaultHeaders(outputMessage, resource, mediaType);
                outputMessage.flush();
                return;
            } else {
                this.resourceHttpMessageConverter.write(resource, mediaType, outputMessage);
                return;
            }
        }
        Assert.state(this.resourceRegionHttpMessageConverter != null, "Not initialized");
        ServletServerHttpRequest inputMessage = new ServletServerHttpRequest(request);
        try {
            List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
            response.setStatus(206);
            this.resourceRegionHttpMessageConverter.write(HttpRange.toResourceRegions(httpRanges, resource), mediaType, outputMessage);
        } catch (IllegalArgumentException e) {
            response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + resource.contentLength());
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
        }
    }

    @Nullable
    protected Resource getResource(HttpServletRequest request) throws IOException {
        String path = processPath(getPath(request));
        if (!StringUtils.hasText(path) || isInvalidPath(path) || isInvalidEncodedPath(path)) {
            return null;
        }
        Assert.state(this.resolverChain != null, "ResourceResolverChain not initialized.");
        Assert.state(this.transformerChain != null, "ResourceTransformerChain not initialized.");
        Resource resource = this.resolverChain.resolveResource(request, path, getLocations());
        if (resource != null) {
            resource = this.transformerChain.transform(request, resource);
        }
        return resource;
    }

    private static String getPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (path == null) {
            throw new IllegalStateException("Required request attribute '" + HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE + "' is not set");
        }
        return path;
    }

    protected String processPath(String path) {
        return cleanLeadingSlash(cleanDuplicateSlashes(StringUtils.replace(path, StrPool.BACKSLASH, "/")));
    }

    private String cleanDuplicateSlashes(String path) {
        char c;
        StringBuilder sb = null;
        char prev = 0;
        for (int i = 0; i < path.length(); i++) {
            char curr = path.charAt(i);
            if (curr == '/' && prev == '/') {
                if (sb == null) {
                    sb = new StringBuilder(path.substring(0, i));
                }
                c = curr;
            } else {
                if (sb != null) {
                    sb.append(path.charAt(i));
                }
                c = curr;
            }
            prev = c;
        }
        return sb != null ? sb.toString() : path;
    }

    private String cleanLeadingSlash(String path) {
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

    private boolean isInvalidEncodedPath(String path) {
        if (path.contains(QuickTargetSourceCreator.PREFIX_THREAD_LOCAL)) {
            try {
                String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
                if (isInvalidPath(decodedPath)) {
                    return true;
                }
                if (isInvalidPath(processPath(decodedPath))) {
                    return true;
                }
                return false;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }

    protected boolean isInvalidPath(String path) {
        if (path.contains("WEB-INF") || path.contains("META-INF")) {
            if (logger.isWarnEnabled()) {
                logger.warn(LogFormatUtils.formatValue("Path with \"WEB-INF\" or \"META-INF\": [" + path + "]", -1, true));
                return true;
            }
            return true;
        }
        if (path.contains(":/")) {
            String relativePath = path.charAt(0) == '/' ? path.substring(1) : path;
            if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
                if (logger.isWarnEnabled()) {
                    logger.warn(LogFormatUtils.formatValue("Path represents URL or has \"url:\" prefix: [" + path + "]", -1, true));
                    return true;
                }
                return true;
            }
        }
        if (path.contains("..") && StringUtils.cleanPath(path).contains("../")) {
            if (logger.isWarnEnabled()) {
                logger.warn(LogFormatUtils.formatValue("Path contains \"../\" after call to StringUtils#cleanPath: [" + path + "]", -1, true));
                return true;
            }
            return true;
        }
        return false;
    }

    @Nullable
    protected MediaType getMediaType(HttpServletRequest request, Resource resource) {
        MediaType result = null;
        String mimeType = request.getServletContext().getMimeType(resource.getFilename());
        if (StringUtils.hasText(mimeType)) {
            result = MediaType.parseMediaType(mimeType);
        }
        if (result == null || MediaType.APPLICATION_OCTET_STREAM.equals(result)) {
            MediaType mediaType = null;
            String filename = resource.getFilename();
            String ext = StringUtils.getFilenameExtension(filename);
            if (ext != null) {
                mediaType = this.mediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
            }
            if (mediaType == null) {
                List<MediaType> mediaTypes = MediaTypeFactory.getMediaTypes(filename);
                if (!CollectionUtils.isEmpty(mediaTypes)) {
                    mediaType = mediaTypes.get(0);
                }
            }
            if (mediaType != null) {
                result = mediaType;
            }
        }
        return result;
    }

    protected void setHeaders(HttpServletResponse response, Resource resource, @Nullable MediaType mediaType) throws IOException {
        if (mediaType != null) {
            response.setContentType(mediaType.toString());
        }
        if (resource instanceof HttpResource) {
            HttpResource httpResource = (HttpResource) resource;
            HttpHeaders resourceHeaders = httpResource.getResponseHeaders();
            resourceHeaders.forEach((headerName, headerValues) -> {
                boolean first = true;
                Iterator it = headerValues.iterator();
                while (it.hasNext()) {
                    String headerValue = (String) it.next();
                    if (first) {
                        response.setHeader(headerName, headerValue);
                    } else {
                        response.addHeader(headerName, headerValue);
                    }
                    first = false;
                }
            });
        }
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
    }

    public String toString() {
        return "ResourceHttpRequestHandler " + locationToString(getLocations());
    }

    private String locationToString(List<Resource> locations) {
        return locations.toString().replaceAll("class path resource", "classpath").replaceAll("ServletContext resource", "ServletContext");
    }
}
