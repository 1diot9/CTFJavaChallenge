package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/web/accept/MappingMediaTypeFileExtensionResolver.class */
public class MappingMediaTypeFileExtensionResolver implements MediaTypeFileExtensionResolver {
    private final ConcurrentMap<String, MediaType> mediaTypes = new ConcurrentHashMap(64);
    private final ConcurrentMap<MediaType, List<String>> fileExtensions = new ConcurrentHashMap(64);
    private final List<String> allFileExtensions = new CopyOnWriteArrayList();

    public MappingMediaTypeFileExtensionResolver(@Nullable Map<String, MediaType> mediaTypes) {
        if (mediaTypes != null) {
            Set<String> allFileExtensions = new HashSet<>(mediaTypes.size());
            mediaTypes.forEach((extension, mediaType) -> {
                String lowerCaseExtension = extension.toLowerCase(Locale.ENGLISH);
                this.mediaTypes.put(lowerCaseExtension, mediaType);
                addFileExtension(mediaType, lowerCaseExtension);
                allFileExtensions.add(lowerCaseExtension);
            });
            this.allFileExtensions.addAll(allFileExtensions);
        }
    }

    public Map<String, MediaType> getMediaTypes() {
        return this.mediaTypes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MediaType> getAllMediaTypes() {
        return new ArrayList(this.mediaTypes.values());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addMapping(String extension, MediaType mediaType) {
        MediaType previous = this.mediaTypes.putIfAbsent(extension, mediaType);
        if (previous == null) {
            addFileExtension(mediaType, extension);
            this.allFileExtensions.add(extension);
        }
    }

    private void addFileExtension(MediaType mediaType, String extension) {
        this.fileExtensions.computeIfAbsent(mediaType, key -> {
            return new CopyOnWriteArrayList();
        }).add(extension);
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> resolveFileExtensions(MediaType mediaType) {
        List<String> fileExtensions = this.fileExtensions.get(mediaType);
        return fileExtensions != null ? fileExtensions : Collections.emptyList();
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> getAllFileExtensions() {
        return Collections.unmodifiableList(this.allFileExtensions);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public MediaType lookupMediaType(String extension) {
        return this.mediaTypes.get(extension.toLowerCase(Locale.ENGLISH));
    }
}
