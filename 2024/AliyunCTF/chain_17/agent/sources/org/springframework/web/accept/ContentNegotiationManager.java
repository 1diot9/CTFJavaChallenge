package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/accept/ContentNegotiationManager.class */
public class ContentNegotiationManager implements ContentNegotiationStrategy, MediaTypeFileExtensionResolver {
    private final List<ContentNegotiationStrategy> strategies;
    private final Set<MediaTypeFileExtensionResolver> resolvers;

    public ContentNegotiationManager(ContentNegotiationStrategy... strategies) {
        this(Arrays.asList(strategies));
    }

    public ContentNegotiationManager(Collection<ContentNegotiationStrategy> strategies) {
        this.strategies = new ArrayList();
        this.resolvers = new LinkedHashSet();
        Assert.notEmpty(strategies, "At least one ContentNegotiationStrategy is expected");
        this.strategies.addAll(strategies);
        for (ContentNegotiationStrategy strategy : this.strategies) {
            if (strategy instanceof MediaTypeFileExtensionResolver) {
                MediaTypeFileExtensionResolver mediaTypeFileExtensionResolver = (MediaTypeFileExtensionResolver) strategy;
                this.resolvers.add(mediaTypeFileExtensionResolver);
            }
        }
    }

    public ContentNegotiationManager() {
        this(new HeaderContentNegotiationStrategy());
    }

    public List<ContentNegotiationStrategy> getStrategies() {
        return this.strategies;
    }

    @Nullable
    public <T extends ContentNegotiationStrategy> T getStrategy(Class<T> strategyType) {
        Iterator<ContentNegotiationStrategy> it = getStrategies().iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (strategyType.isInstance(t)) {
                return t;
            }
        }
        return null;
    }

    public void addFileExtensionResolvers(MediaTypeFileExtensionResolver... resolvers) {
        Collections.addAll(this.resolvers, resolvers);
    }

    @Override // org.springframework.web.accept.ContentNegotiationStrategy
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        for (ContentNegotiationStrategy strategy : this.strategies) {
            List<MediaType> mediaTypes = strategy.resolveMediaTypes(request);
            if (!mediaTypes.equals(MEDIA_TYPE_ALL_LIST)) {
                return mediaTypes;
            }
        }
        return MEDIA_TYPE_ALL_LIST;
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> resolveFileExtensions(MediaType mediaType) {
        return doResolveExtensions(resolver -> {
            return resolver.resolveFileExtensions(mediaType);
        });
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> getAllFileExtensions() {
        return doResolveExtensions((v0) -> {
            return v0.getAllFileExtensions();
        });
    }

    private List<String> doResolveExtensions(Function<MediaTypeFileExtensionResolver, List<String>> extractor) {
        List<String> result = null;
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            List<String> extensions = extractor.apply(resolver);
            if (!CollectionUtils.isEmpty(extensions)) {
                result = result != null ? result : new ArrayList<>(4);
                for (String extension : extensions) {
                    if (!result.contains(extension)) {
                        result.add(extension);
                    }
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    public Map<String, MediaType> getMediaTypeMappings() {
        Map<String, MediaType> result = null;
        for (MediaTypeFileExtensionResolver resolver : this.resolvers) {
            if (resolver instanceof MappingMediaTypeFileExtensionResolver) {
                MappingMediaTypeFileExtensionResolver mappingResolver = (MappingMediaTypeFileExtensionResolver) resolver;
                Map<String, MediaType> map = mappingResolver.getMediaTypes();
                if (!CollectionUtils.isEmpty(map)) {
                    result = result != null ? result : new HashMap<>(4);
                    result.putAll(map);
                }
            }
        }
        return result != null ? result : Collections.emptyMap();
    }
}
