package org.springframework.web.accept;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/accept/AbstractMappingContentNegotiationStrategy.class */
public abstract class AbstractMappingContentNegotiationStrategy extends MappingMediaTypeFileExtensionResolver implements ContentNegotiationStrategy {
    protected final Log logger;
    private boolean useRegisteredExtensionsOnly;
    private boolean ignoreUnknownExtensions;

    @Nullable
    protected abstract String getMediaTypeKey(NativeWebRequest request);

    public AbstractMappingContentNegotiationStrategy(@Nullable Map<String, MediaType> mediaTypes) {
        super(mediaTypes);
        this.logger = LogFactory.getLog(getClass());
        this.useRegisteredExtensionsOnly = false;
        this.ignoreUnknownExtensions = false;
    }

    public void setUseRegisteredExtensionsOnly(boolean useRegisteredExtensionsOnly) {
        this.useRegisteredExtensionsOnly = useRegisteredExtensionsOnly;
    }

    public boolean isUseRegisteredExtensionsOnly() {
        return this.useRegisteredExtensionsOnly;
    }

    public void setIgnoreUnknownExtensions(boolean ignoreUnknownExtensions) {
        this.ignoreUnknownExtensions = ignoreUnknownExtensions;
    }

    public boolean isIgnoreUnknownExtensions() {
        return this.ignoreUnknownExtensions;
    }

    @Override // org.springframework.web.accept.ContentNegotiationStrategy
    public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
        return resolveMediaTypeKey(webRequest, getMediaTypeKey(webRequest));
    }

    public List<MediaType> resolveMediaTypeKey(NativeWebRequest webRequest, @Nullable String key) throws HttpMediaTypeNotAcceptableException {
        if (StringUtils.hasText(key)) {
            MediaType mediaType = lookupMediaType(key);
            if (mediaType != null) {
                handleMatch(key, mediaType);
                return Collections.singletonList(mediaType);
            }
            MediaType mediaType2 = handleNoMatch(webRequest, key);
            if (mediaType2 != null) {
                addMapping(key, mediaType2);
                return Collections.singletonList(mediaType2);
            }
        }
        return MEDIA_TYPE_ALL_LIST;
    }

    protected void handleMatch(String key, MediaType mediaType) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public MediaType handleNoMatch(NativeWebRequest request, String key) throws HttpMediaTypeNotAcceptableException {
        if (!isUseRegisteredExtensionsOnly()) {
            Optional<MediaType> mediaType = MediaTypeFactory.getMediaType("file." + key);
            if (mediaType.isPresent()) {
                return mediaType.get();
            }
        }
        if (isIgnoreUnknownExtensions()) {
            return null;
        }
        throw new HttpMediaTypeNotAcceptableException(getAllMediaTypes());
    }
}
