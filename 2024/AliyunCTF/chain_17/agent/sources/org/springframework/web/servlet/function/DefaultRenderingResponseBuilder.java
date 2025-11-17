package org.springframework.web.servlet.function;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.core.Conventions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.RenderingResponse;
import org.springframework.web.servlet.function.ServerResponse;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultRenderingResponseBuilder.class */
final class DefaultRenderingResponseBuilder implements RenderingResponse.Builder {
    private final String name;
    private HttpStatusCode status;
    private final HttpHeaders headers;
    private final MultiValueMap<String, Cookie> cookies;
    private final Map<String, Object> model;

    public DefaultRenderingResponseBuilder(RenderingResponse other) {
        this.status = HttpStatus.OK;
        this.headers = new HttpHeaders();
        this.cookies = new LinkedMultiValueMap();
        this.model = new LinkedHashMap();
        Assert.notNull(other, "RenderingResponse must not be null");
        this.name = other.name();
        this.status = other.statusCode();
        this.headers.putAll(other.headers());
        this.model.putAll(other.model());
    }

    public DefaultRenderingResponseBuilder(String name) {
        this.status = HttpStatus.OK;
        this.headers = new HttpHeaders();
        this.cookies = new LinkedMultiValueMap();
        this.model = new LinkedHashMap();
        Assert.notNull(name, "Name must not be null");
        this.name = name;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder status(HttpStatusCode status) {
        Assert.notNull(status, "HttpStatusCode must not be null");
        this.status = status;
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder status(int status) {
        return status(HttpStatusCode.valueOf(status));
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder cookie(Cookie cookie) {
        Assert.notNull(cookie, "Cookie must not be null");
        this.cookies.add(cookie.getName(), cookie);
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder cookies(Consumer<MultiValueMap<String, Cookie>> cookiesConsumer) {
        cookiesConsumer.accept(this.cookies);
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder modelAttribute(Object attribute) {
        Assert.notNull(attribute, "Attribute must not be null");
        if (attribute instanceof Collection) {
            Collection<?> collection = (Collection) attribute;
            if (collection.isEmpty()) {
                return this;
            }
        }
        return modelAttribute(Conventions.getVariableName(attribute), attribute);
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder modelAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        this.model.put(name, value);
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder modelAttributes(Object... attributes) {
        modelAttributes(Arrays.asList(attributes));
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder modelAttributes(Collection<?> attributes) {
        attributes.forEach(this::modelAttribute);
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder modelAttributes(Map<String, ?> attributes) {
        this.model.putAll(attributes);
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder header(String headerName, String... headerValues) {
        for (String headerValue : headerValues) {
            this.headers.add(headerName, headerValue);
        }
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse.Builder headers(Consumer<HttpHeaders> headersConsumer) {
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override // org.springframework.web.servlet.function.RenderingResponse.Builder
    public RenderingResponse build() {
        return new DefaultRenderingResponse(this.status, this.headers, this.cookies, this.name, this.model);
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/DefaultRenderingResponseBuilder$DefaultRenderingResponse.class */
    private static final class DefaultRenderingResponse extends AbstractServerResponse implements RenderingResponse {
        private final String name;
        private final Map<String, Object> model;

        public DefaultRenderingResponse(HttpStatusCode statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies, String name, Map<String, Object> model) {
            super(statusCode, headers, cookies);
            this.name = name;
            this.model = Collections.unmodifiableMap(new LinkedHashMap(model));
        }

        @Override // org.springframework.web.servlet.function.RenderingResponse
        public String name() {
            return this.name;
        }

        @Override // org.springframework.web.servlet.function.RenderingResponse
        public Map<String, Object> model() {
            return this.model;
        }

        @Override // org.springframework.web.servlet.function.AbstractServerResponse
        protected ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) {
            ModelAndView mav = new ModelAndView(this.name, statusCode());
            mav.addAllObjects(this.model);
            return mav;
        }
    }
}
