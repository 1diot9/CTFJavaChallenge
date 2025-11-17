package org.springframework.http.codec.multipart;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/multipart/FormPartEvent.class */
public interface FormPartEvent extends PartEvent {
    String value();

    static Mono<FormPartEvent> create(String name, String value) {
        return create(name, value, null);
    }

    static Mono<FormPartEvent> create(String name, String value, @Nullable Consumer<HttpHeaders> headersConsumer) {
        Assert.hasLength(name, "Name must not be empty");
        Assert.notNull(value, "Value must not be null");
        return Mono.fromCallable(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8));
            headers.setContentDisposition(ContentDisposition.formData().name(name).build());
            if (headersConsumer != null) {
                headersConsumer.accept(headers);
            }
            return DefaultPartEvents.form(headers, value);
        });
    }
}
