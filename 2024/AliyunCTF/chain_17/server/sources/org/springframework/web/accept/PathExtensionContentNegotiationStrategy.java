package org.springframework.web.accept;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/accept/PathExtensionContentNegotiationStrategy.class */
public class PathExtensionContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {
    private UrlPathHelper urlPathHelper;

    public PathExtensionContentNegotiationStrategy() {
        this(null);
    }

    public PathExtensionContentNegotiationStrategy(@Nullable Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        this.urlPathHelper = new UrlPathHelper();
        setUseRegisteredExtensionsOnly(false);
        setIgnoreUnknownExtensions(true);
        this.urlPathHelper.setUrlDecode(false);
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Deprecated
    public void setUseJaf(boolean useJaf) {
        setUseRegisteredExtensionsOnly(!useJaf);
    }

    @Override // org.springframework.web.accept.AbstractMappingContentNegotiationStrategy
    @Nullable
    protected String getMediaTypeKey(NativeWebRequest webRequest) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }
        String path = this.urlPathHelper.getLookupPathForRequest(request);
        String extension = UriUtils.extractFileExtension(path);
        if (StringUtils.hasText(extension)) {
            return extension.toLowerCase(Locale.ENGLISH);
        }
        return null;
    }

    @Nullable
    public MediaType getMediaTypeForResource(Resource resource) {
        Assert.notNull(resource, "Resource must not be null");
        MediaType mediaType = null;
        String filename = resource.getFilename();
        String extension = StringUtils.getFilenameExtension(filename);
        if (extension != null) {
            mediaType = lookupMediaType(extension);
        }
        if (mediaType == null) {
            mediaType = MediaTypeFactory.getMediaType(filename).orElse(null);
        }
        return mediaType;
    }
}
