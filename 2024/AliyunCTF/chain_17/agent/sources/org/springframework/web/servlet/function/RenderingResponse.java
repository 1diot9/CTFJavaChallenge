package org.springframework.web.servlet.function;

import jakarta.servlet.http.Cookie;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RenderingResponse.class */
public interface RenderingResponse extends ServerResponse {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/RenderingResponse$Builder.class */
    public interface Builder {
        Builder modelAttribute(Object attribute);

        Builder modelAttribute(String name, @Nullable Object value);

        Builder modelAttributes(Object... attributes);

        Builder modelAttributes(Collection<?> attributes);

        Builder modelAttributes(Map<String, ?> attributes);

        Builder header(String headerName, String... headerValues);

        Builder headers(Consumer<HttpHeaders> headersConsumer);

        Builder status(HttpStatusCode status);

        Builder status(int status);

        Builder cookie(Cookie cookie);

        Builder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer);

        RenderingResponse build();
    }

    String name();

    Map<String, Object> model();

    static Builder from(RenderingResponse other) {
        return new DefaultRenderingResponseBuilder(other);
    }

    static Builder create(String name) {
        return new DefaultRenderingResponseBuilder(name);
    }
}
