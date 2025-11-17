package org.springframework.boot.web.embedded.netty;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import org.springframework.boot.web.server.Compression;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/netty/CompressionCustomizer.class */
final class CompressionCustomizer implements NettyServerCustomizer {
    private static final CompressionPredicate ALWAYS_COMPRESS = (request, response) -> {
        return true;
    };
    private final Compression compression;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/netty/CompressionCustomizer$CompressionPredicate.class */
    public interface CompressionPredicate extends BiPredicate<HttpServerRequest, HttpServerResponse> {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompressionCustomizer(Compression compression) {
        this.compression = compression;
    }

    @Override // java.util.function.Function
    public HttpServer apply(HttpServer server) {
        if (!this.compression.getMinResponseSize().isNegative()) {
            server = server.compress((int) this.compression.getMinResponseSize().toBytes());
        }
        CompressionPredicate mimeTypes = getMimeTypesPredicate(this.compression.getMimeTypes());
        CompressionPredicate excludedUserAgents = getExcludedUserAgentsPredicate(this.compression.getExcludedUserAgents());
        return server.compress(mimeTypes.and(excludedUserAgents));
    }

    private CompressionPredicate getMimeTypesPredicate(String[] mimeTypeValues) {
        if (ObjectUtils.isEmpty((Object[]) mimeTypeValues)) {
            return ALWAYS_COMPRESS;
        }
        List<MimeType> mimeTypes = Arrays.stream(mimeTypeValues).map(MimeTypeUtils::parseMimeType).toList();
        return (request, response) -> {
            String contentType = response.responseHeaders().get(HttpHeaderNames.CONTENT_TYPE);
            if (!StringUtils.hasLength(contentType)) {
                return false;
            }
            try {
                MimeType contentMimeType = MimeTypeUtils.parseMimeType(contentType);
                return mimeTypes.stream().anyMatch(candidate -> {
                    return candidate.isCompatibleWith(contentMimeType);
                });
            } catch (InvalidMimeTypeException e) {
                return false;
            }
        };
    }

    private CompressionPredicate getExcludedUserAgentsPredicate(String[] excludedUserAgents) {
        if (ObjectUtils.isEmpty((Object[]) excludedUserAgents)) {
            return ALWAYS_COMPRESS;
        }
        return (request, response) -> {
            HttpHeaders headers = request.requestHeaders();
            return Arrays.stream(excludedUserAgents).noneMatch(candidate -> {
                return headers.contains(HttpHeaderNames.USER_AGENT, candidate, true);
            });
        };
    }
}
