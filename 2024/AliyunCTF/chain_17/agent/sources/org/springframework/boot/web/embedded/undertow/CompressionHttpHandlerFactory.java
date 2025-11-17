package org.springframework.boot.web.embedded.undertow;

import io.undertow.attribute.RequestHeaderAttribute;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.web.server.Compression;
import org.springframework.http.HttpHeaders;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/CompressionHttpHandlerFactory.class */
class CompressionHttpHandlerFactory implements HttpHandlerFactory {
    private final Compression compression;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CompressionHttpHandlerFactory(Compression compression) {
        this.compression = compression;
    }

    @Override // org.springframework.boot.web.embedded.undertow.HttpHandlerFactory
    public HttpHandler getHandler(HttpHandler next) {
        if (!this.compression.getEnabled()) {
            return next;
        }
        ContentEncodingRepository repository = new ContentEncodingRepository();
        repository.addEncodingHandler("gzip", new GzipEncodingProvider(), 50, Predicates.and(getCompressionPredicates(this.compression)));
        return new EncodingHandler(repository).setNext(next);
    }

    private static Predicate[] getCompressionPredicates(Compression compression) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(new MaxSizePredicate((int) compression.getMinResponseSize().toBytes()));
        predicates.add(new CompressibleMimeTypePredicate(compression.getMimeTypes()));
        if (compression.getExcludedUserAgents() != null) {
            for (String agent : compression.getExcludedUserAgents()) {
                RequestHeaderAttribute agentHeader = new RequestHeaderAttribute(new HttpString(HttpHeaders.USER_AGENT));
                predicates.add(Predicates.not(Predicates.regex(agentHeader, agent)));
            }
        }
        return (Predicate[]) predicates.toArray(new Predicate[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/CompressionHttpHandlerFactory$CompressibleMimeTypePredicate.class */
    public static class CompressibleMimeTypePredicate implements Predicate {
        private final List<MimeType> mimeTypes;

        CompressibleMimeTypePredicate(String[] mimeTypes) {
            this.mimeTypes = new ArrayList(mimeTypes.length);
            for (String mimeTypeString : mimeTypes) {
                this.mimeTypes.add(MimeTypeUtils.parseMimeType(mimeTypeString));
            }
        }

        public boolean resolve(HttpServerExchange value) {
            String contentType = value.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            if (contentType != null) {
                try {
                    MimeType parsed = MimeTypeUtils.parseMimeType(contentType);
                    for (MimeType mimeType : this.mimeTypes) {
                        if (mimeType.isCompatibleWith(parsed)) {
                            return true;
                        }
                    }
                    return false;
                } catch (InvalidMimeTypeException e) {
                    return false;
                }
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-boot-3.2.2.jar:org/springframework/boot/web/embedded/undertow/CompressionHttpHandlerFactory$MaxSizePredicate.class */
    public static class MaxSizePredicate implements Predicate {
        private final Predicate maxContentSize;

        MaxSizePredicate(int size) {
            this.maxContentSize = Predicates.requestLargerThan(size);
        }

        public boolean resolve(HttpServerExchange value) {
            if (value.getResponseHeaders().contains(Headers.CONTENT_LENGTH)) {
                return this.maxContentSize.resolve(value);
            }
            return true;
        }
    }
}
