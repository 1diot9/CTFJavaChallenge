package org.springframework.web.servlet.mvc.method.annotation;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: agent.jar:BOOT-INF/lib/spring-webmvc-6.1.3.jar:org/springframework/web/servlet/mvc/method/annotation/AbstractMessageConverterMethodProcessor.class */
public abstract class AbstractMessageConverterMethodProcessor extends AbstractMessageConverterMethodArgumentResolver implements HandlerMethodReturnValueHandler {
    private static final Set<String> SAFE_EXTENSIONS = Set.of((Object[]) new String[]{QrCodeUtil.QR_TYPE_TXT, "text", "yml", "properties", "csv", "json", "xml", "atom", "rss", ImgUtil.IMAGE_TYPE_PNG, "jpe", ImgUtil.IMAGE_TYPE_JPEG, ImgUtil.IMAGE_TYPE_JPG, ImgUtil.IMAGE_TYPE_GIF, "wbmp", ImgUtil.IMAGE_TYPE_BMP});
    private static final Set<String> SAFE_MEDIA_BASE_TYPES = Set.of("audio", "image", "video");
    private static final List<MediaType> ALL_APPLICATION_MEDIA_TYPES = List.of(MediaType.ALL, new MediaType("application"));
    private static final Type RESOURCE_REGION_LIST_TYPE = new ParameterizedTypeReference<List<ResourceRegion>>() { // from class: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor.1
    }.getType();
    private final ContentNegotiationManager contentNegotiationManager;
    private final List<MediaType> problemMediaTypes;
    private final Set<String> safeExtensions;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters) {
        this(converters, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager contentNegotiationManager) {
        this(converters, contentNegotiationManager, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMessageConverterMethodProcessor(List<HttpMessageConverter<?>> converters, @Nullable ContentNegotiationManager manager, @Nullable List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
        this.problemMediaTypes = Arrays.asList(MediaType.APPLICATION_PROBLEM_JSON, MediaType.APPLICATION_PROBLEM_XML);
        this.safeExtensions = new HashSet();
        this.contentNegotiationManager = manager != null ? manager : new ContentNegotiationManager();
        this.safeExtensions.addAll(this.contentNegotiationManager.getAllFileExtensions());
        this.safeExtensions.addAll(SAFE_EXTENSIONS);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ServletServerHttpResponse createOutputMessage(NativeWebRequest webRequest) {
        HttpServletResponse response = (HttpServletResponse) webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.state(response != null, "No HttpServletResponse");
        return new ServletServerHttpResponse(response);
    }

    protected <T> void writeWithMessageConverters(T value, MethodParameter returnType, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        ServletServerHttpRequest inputMessage = createInputMessage(webRequest);
        ServletServerHttpResponse outputMessage = createOutputMessage(webRequest);
        writeWithMessageConverters(value, returnType, inputMessage, outputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:45:0x02e9  */
    /* JADX WARN: Removed duplicated region for block: B:52:0x0329  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public <T> void writeWithMessageConverters(@org.springframework.lang.Nullable T r9, org.springframework.core.MethodParameter r10, org.springframework.http.server.ServletServerHttpRequest r11, org.springframework.http.server.ServletServerHttpResponse r12) throws java.io.IOException, org.springframework.web.HttpMediaTypeNotAcceptableException, org.springframework.http.converter.HttpMessageNotWritableException {
        /*
            Method dump skipped, instructions count: 907
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor.writeWithMessageConverters(java.lang.Object, org.springframework.core.MethodParameter, org.springframework.http.server.ServletServerHttpRequest, org.springframework.http.server.ServletServerHttpResponse):void");
    }

    protected Class<?> getReturnValueType(@Nullable Object value, MethodParameter returnType) {
        return value != null ? value.getClass() : returnType.getParameterType();
    }

    protected boolean isResourceType(@Nullable Object value, MethodParameter returnType) {
        Class<?> clazz = getReturnValueType(value, returnType);
        return clazz != InputStreamResource.class && Resource.class.isAssignableFrom(clazz);
    }

    private Type getGenericType(MethodParameter returnType) {
        if (HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return ResolvableType.forType(returnType.getGenericParameterType()).getGeneric(new int[0]).getType();
        }
        return returnType.getGenericParameterType();
    }

    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass) {
        return getProducibleMediaTypes(request, valueClass, null);
    }

    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass, @Nullable Type targetType) {
        Set<MediaType> mediaTypes = (Set) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList(mediaTypes);
        }
        Set<MediaType> result = new LinkedHashSet<>();
        for (HttpMessageConverter<?> converter : this.messageConverters) {
            if (converter instanceof GenericHttpMessageConverter) {
                GenericHttpMessageConverter<?> ghmc = (GenericHttpMessageConverter) converter;
                if (targetType != null) {
                    if (ghmc.canWrite(targetType, valueClass, null)) {
                        result.addAll(converter.getSupportedMediaTypes(valueClass));
                    }
                }
            }
            if (converter.canWrite(valueClass, null)) {
                result.addAll(converter.getSupportedMediaTypes(valueClass));
            }
        }
        return result.isEmpty() ? Collections.singletonList(MediaType.ALL) : new ArrayList(result);
    }

    private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
    }

    private void determineCompatibleMediaTypes(List<MediaType> acceptableTypes, List<MediaType> producibleTypes, List<MediaType> mediaTypesToUse) {
        for (MediaType requestedType : acceptableTypes) {
            for (MediaType producibleType : producibleTypes) {
                if (requestedType.isCompatibleWith(producibleType)) {
                    mediaTypesToUse.add(getMostSpecificMediaType(requestedType, producibleType));
                }
            }
        }
    }

    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceTypeToUse = produceType.copyQualityValue(acceptType);
        if (acceptType.isLessSpecific(produceTypeToUse)) {
            return produceTypeToUse;
        }
        return acceptType;
    }

    private void addContentDispositionHeader(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        int status;
        HttpHeaders headers = response.getHeaders();
        if (headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            return;
        }
        try {
            status = response.getServletResponse().getStatus();
        } catch (Throwable th) {
        }
        if (status < 200) {
            return;
        }
        if (status > 299 && status < 400) {
            return;
        }
        HttpServletRequest servletRequest = request.getServletRequest();
        String requestUri = UrlPathHelper.rawPathInstance.getOriginatingRequestUri(servletRequest);
        String filename = requestUri.substring(requestUri.lastIndexOf(47) + 1);
        String pathParams = "";
        int index = filename.indexOf(59);
        if (index != -1) {
            pathParams = filename.substring(index);
            filename = filename.substring(0, index);
        }
        String ext = StringUtils.getFilenameExtension(UrlPathHelper.defaultInstance.decodeRequestString(servletRequest, filename));
        String extInPathParams = StringUtils.getFilenameExtension(UrlPathHelper.defaultInstance.decodeRequestString(servletRequest, pathParams));
        if (!safeExtension(servletRequest, ext) || !safeExtension(servletRequest, extInPathParams)) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=f.txt");
        }
    }

    private boolean safeExtension(HttpServletRequest request, @Nullable String extension) {
        if (!StringUtils.hasText(extension)) {
            return true;
        }
        String extension2 = extension.toLowerCase(Locale.ENGLISH);
        if (this.safeExtensions.contains(extension2)) {
            return true;
        }
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern != null && pattern.endsWith("." + extension2)) {
            return true;
        }
        if (extension2.equals("html")) {
            String name = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
            Set<MediaType> mediaTypes = (Set) request.getAttribute(name);
            if (!CollectionUtils.isEmpty(mediaTypes) && mediaTypes.contains(MediaType.TEXT_HTML)) {
                return true;
            }
        }
        MediaType mediaType = resolveMediaType(request, extension2);
        return mediaType != null && safeMediaType(mediaType);
    }

    @Nullable
    private MediaType resolveMediaType(ServletRequest request, String extension) {
        MediaType result = null;
        String rawMimeType = request.getServletContext().getMimeType("file." + extension);
        if (StringUtils.hasText(rawMimeType)) {
            result = MediaType.parseMediaType(rawMimeType);
        }
        if (result == null || MediaType.APPLICATION_OCTET_STREAM.equals(result)) {
            result = MediaTypeFactory.getMediaType("file." + extension).orElse(null);
        }
        return result;
    }

    private boolean safeMediaType(MediaType mediaType) {
        return SAFE_MEDIA_BASE_TYPES.contains(mediaType.getType()) || mediaType.getSubtype().endsWith("+xml");
    }
}
