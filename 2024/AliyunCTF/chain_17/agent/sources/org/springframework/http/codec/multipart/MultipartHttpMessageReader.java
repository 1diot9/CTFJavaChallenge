package org.springframework.http.codec.multipart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/MultipartHttpMessageReader.class */
public class MultipartHttpMessageReader extends LoggingCodecSupport implements HttpMessageReader<MultiValueMap<String, Part>> {
    private static final ResolvableType MULTIPART_VALUE_TYPE = ResolvableType.forClassWithGenerics((Class<?>) MultiValueMap.class, (Class<?>[]) new Class[]{String.class, Part.class});
    static final List<MediaType> MIME_TYPES = List.of(MediaType.MULTIPART_FORM_DATA, MediaType.MULTIPART_MIXED, MediaType.MULTIPART_RELATED);
    private final HttpMessageReader<Part> partReader;

    public MultipartHttpMessageReader(HttpMessageReader<Part> partReader) {
        Assert.notNull(partReader, "'partReader' is required");
        this.partReader = partReader;
    }

    public HttpMessageReader<Part> getPartReader() {
        return this.partReader;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public List<MediaType> getReadableMediaTypes() {
        return MIME_TYPES;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public boolean canRead(ResolvableType elementType, @Nullable MediaType mediaType) {
        return supportsMediaType(mediaType) && MULTIPART_VALUE_TYPE.isAssignableFrom(elementType);
    }

    private boolean supportsMediaType(@Nullable MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType supportedMediaType : MIME_TYPES) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Flux<MultiValueMap<String, Part>> read(ResolvableType elementType, ReactiveHttpInputMessage message, Map<String, Object> hints) {
        return Flux.from(readMono(elementType, message, hints));
    }

    @Override // org.springframework.http.codec.HttpMessageReader
    public Mono<MultiValueMap<String, Part>> readMono(ResolvableType elementType, ReactiveHttpInputMessage inputMessage, Map<String, Object> hints) {
        Map<String, Object> allHints = Hints.merge(hints, Hints.SUPPRESS_LOGGING_HINT, true);
        return this.partReader.read(elementType, inputMessage, allHints).collectMultimap((v0) -> {
            return v0.name();
        }).doOnNext(map -> {
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String str;
                String logPrefix = Hints.getLogPrefix(hints);
                if (isEnableLoggingRequestDetails()) {
                    str = LogFormatUtils.formatValue(map, !traceOn.booleanValue());
                } else {
                    str = "parts " + map.keySet() + " (content masked)";
                }
                return logPrefix + "Parsed " + str;
            });
        }).map(this::toMultiValueMap);
    }

    private LinkedMultiValueMap<String, Part> toMultiValueMap(Map<String, Collection<Part>> map) {
        return new LinkedMultiValueMap<>((Map) map.entrySet().stream().collect(Collectors.toMap((v0) -> {
            return v0.getKey();
        }, e -> {
            return toList((Collection) e.getValue());
        })));
    }

    private List<Part> toList(Collection<Part> collection) {
        if (!(collection instanceof List)) {
            return new ArrayList(collection);
        }
        List<Part> list = (List) collection;
        return list;
    }
}
