package org.springframework.web.servlet.function;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ServerResponse;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/function/AbstractServerResponse.class */
public abstract class AbstractServerResponse extends ErrorHandlingServerResponse {
    private static final Set<HttpMethod> SAFE_METHODS = Set.of(HttpMethod.GET, HttpMethod.HEAD);
    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private final MultiValueMap<String, Cookie> cookies;

    @Nullable
    protected abstract ModelAndView writeToInternal(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws Exception;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractServerResponse(HttpStatusCode statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies) {
        this.statusCode = statusCode;
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
        this.cookies = CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(cookies));
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public final HttpStatusCode statusCode() {
        return this.statusCode;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    @Deprecated
    public int rawStatusCode() {
        return this.statusCode.value();
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public final HttpHeaders headers() {
        return this.headers;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public MultiValueMap<String, Cookie> cookies() {
        return this.cookies;
    }

    @Override // org.springframework.web.servlet.function.ServerResponse
    public ModelAndView writeTo(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
        try {
            writeStatusAndHeaders(response);
            long lastModified = headers().getLastModified();
            ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
            HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
            if (SAFE_METHODS.contains(httpMethod) && servletWebRequest.checkNotModified(headers().getETag(), lastModified)) {
                return null;
            }
            return writeToInternal(request, response, context);
        } catch (Throwable throwable) {
            return handleError(throwable, request, response, context);
        }
    }

    private void writeStatusAndHeaders(HttpServletResponse response) {
        response.setStatus(this.statusCode.value());
        writeHeaders(response);
        writeCookies(response);
    }

    private void writeHeaders(HttpServletResponse servletResponse) {
        this.headers.forEach((headerName, headerValues) -> {
            Iterator it = headerValues.iterator();
            while (it.hasNext()) {
                String headerValue = (String) it.next();
                servletResponse.addHeader(headerName, headerValue);
            }
        });
        if (servletResponse.getContentType() == null && this.headers.getContentType() != null) {
            servletResponse.setContentType(this.headers.getContentType().toString());
        }
        if (servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers.getContentType().getCharset() != null) {
            servletResponse.setCharacterEncoding(this.headers.getContentType().getCharset().name());
        }
    }

    private void writeCookies(HttpServletResponse servletResponse) {
        Stream<R> flatMap = this.cookies.values().stream().flatMap((v0) -> {
            return v0.stream();
        });
        Objects.requireNonNull(servletResponse);
        flatMap.forEach(servletResponse::addCookie);
    }
}
