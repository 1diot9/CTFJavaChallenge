package org.springframework.web.servlet.mvc.method.annotation;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.ValidationAnnotationUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodArgumentResolver.class */
public abstract class AbstractMessageConverterMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Set<HttpMethod> SUPPORTED_METHODS = Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);
    private static final Object NO_VALUE = new Object();
    protected final Log logger;
    protected final List<HttpMessageConverter<?>> messageConverters;
    private final RequestResponseBodyAdviceChain advice;

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters) {
        this(converters, null);
    }

    public AbstractMessageConverterMethodArgumentResolver(List<HttpMessageConverter<?>> converters, @Nullable List<Object> requestResponseBodyAdvice) {
        this.logger = LogFactory.getLog(getClass());
        Assert.notEmpty(converters, "'messageConverters' must not be empty");
        this.messageConverters = converters;
        this.advice = new RequestResponseBodyAdviceChain(requestResponseBodyAdvice);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RequestResponseBodyAdviceChain getAdvice() {
        return this.advice;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public <T> Object readWithMessageConverters(NativeWebRequest webRequest, MethodParameter parameter, Type paramType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        HttpInputMessage inputMessage = createInputMessage(webRequest);
        return readWithMessageConverters(inputMessage, parameter, paramType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0107 A[Catch: IOException -> 0x019f, all -> 0x01ae, TryCatch #0 {IOException -> 0x019f, blocks: (B:18:0x008c, B:19:0x00a1, B:21:0x00ab, B:23:0x00c6, B:26:0x00da, B:30:0x00ff, B:32:0x0107, B:34:0x011b, B:35:0x0135, B:71:0x012a, B:72:0x014b, B:77:0x00f1, B:36:0x0161, B:40:0x016e, B:42:0x0176), top: B:17:0x008c, outer: #2 }] */
    /* JADX WARN: Removed duplicated region for block: B:51:0x01d1  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x020e  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x014b A[Catch: IOException -> 0x019f, all -> 0x01ae, TryCatch #0 {IOException -> 0x019f, blocks: (B:18:0x008c, B:19:0x00a1, B:21:0x00ab, B:23:0x00c6, B:26:0x00da, B:30:0x00ff, B:32:0x0107, B:34:0x011b, B:35:0x0135, B:71:0x012a, B:72:0x014b, B:77:0x00f1, B:36:0x0161, B:40:0x016e, B:42:0x0176), top: B:17:0x008c, outer: #2 }] */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public <T> java.lang.Object readWithMessageConverters(org.springframework.http.HttpInputMessage r8, org.springframework.core.MethodParameter r9, java.lang.reflect.Type r10) throws java.io.IOException, org.springframework.web.HttpMediaTypeNotSupportedException, org.springframework.http.converter.HttpMessageNotReadableException {
        /*
            Method dump skipped, instructions count: 553
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.readWithMessageConverters(org.springframework.http.HttpInputMessage, org.springframework.core.MethodParameter, java.lang.reflect.Type):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletServerHttpRequest createInputMessage(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");
        return new ServletServerHttpRequest(servletRequest);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation ann : annotations) {
            Object[] validationHints = ValidationAnnotationUtils.determineValidationHints(ann);
            if (validationHints != null) {
                binder.validate(validationHints);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getExecutable().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > i + 1 && Errors.class.isAssignableFrom(paramTypes[i + 1]);
        return !hasBindingResult;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        Set<MediaType> mediaTypeSet = new LinkedHashSet<>();
        for (HttpMessageConverter<?> converter : this.messageConverters) {
            mediaTypeSet.addAll(converter.getSupportedMediaTypes(clazz));
        }
        List<MediaType> result = new ArrayList<>(mediaTypeSet);
        MimeTypeUtils.sortBySpecificity(result);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0033, code lost:            if (r0.length == 0) goto L14;     */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0020, code lost:            if (r0.isEmpty() == false) goto L10;     */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.Object adaptArgumentIfNecessary(@org.springframework.lang.Nullable java.lang.Object r4, org.springframework.core.MethodParameter r5) {
        /*
            r3 = this;
            r0 = r5
            java.lang.Class r0 = r0.getParameterType()
            java.lang.Class<java.util.Optional> r1 = java.util.Optional.class
            if (r0 != r1) goto L3f
            r0 = r4
            if (r0 == 0) goto L36
            r0 = r4
            boolean r0 = r0 instanceof java.util.Collection
            if (r0 == 0) goto L23
            r0 = r4
            java.util.Collection r0 = (java.util.Collection) r0
            r6 = r0
            r0 = r6
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L36
        L23:
            r0 = r4
            boolean r0 = r0 instanceof java.lang.Object[]
            if (r0 == 0) goto L3a
            r0 = r4
            java.lang.Object[] r0 = (java.lang.Object[]) r0
            r7 = r0
            r0 = r7
            int r0 = r0.length
            if (r0 != 0) goto L3a
        L36:
            java.util.Optional r0 = java.util.Optional.empty()
            return r0
        L3a:
            r0 = r4
            java.util.Optional r0 = java.util.Optional.of(r0)
            return r0
        L3f:
            r0 = r4
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver.adaptArgumentIfNecessary(java.lang.Object, org.springframework.core.MethodParameter):java.lang.Object");
    }

    void closeStreamIfNecessary(InputStream body) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodArgumentResolver$EmptyBodyCheckingHttpInputMessage.class */
    public static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {
        private final HttpHeaders headers;

        @Nullable
        private final InputStream body;

        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = inputStream.read() != -1 ? inputStream : null;
                inputStream.reset();
                return;
            }
            PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
            int b = pushbackInputStream.read();
            if (b == -1) {
                this.body = null;
            } else {
                this.body = pushbackInputStream;
                pushbackInputStream.unread(b);
            }
        }

        @Override // org.springframework.http.HttpMessage
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override // org.springframework.http.HttpInputMessage
        public InputStream getBody() {
            return this.body != null ? this.body : InputStream.nullInputStream();
        }

        public boolean hasBody() {
            return this.body != null;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodArgumentResolver$NoContentTypeHttpMessageConverter.class */
    private static class NoContentTypeHttpMessageConverter implements HttpMessageConverter<String> {
        private NoContentTypeHttpMessageConverter() {
        }

        @Override // org.springframework.http.converter.HttpMessageConverter
        public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
            return false;
        }

        @Override // org.springframework.http.converter.HttpMessageConverter
        public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
            return false;
        }

        @Override // org.springframework.http.converter.HttpMessageConverter
        public List<MediaType> getSupportedMediaTypes() {
            return Collections.emptyList();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // org.springframework.http.converter.HttpMessageConverter
        /* renamed from: read */
        public String read2(Class<? extends String> clazz, HttpInputMessage inputMessage) {
            throw new UnsupportedOperationException();
        }

        @Override // org.springframework.http.converter.HttpMessageConverter
        public void write(String s, @Nullable MediaType contentType, HttpOutputMessage outputMessage) {
            throw new UnsupportedOperationException();
        }
    }
}
