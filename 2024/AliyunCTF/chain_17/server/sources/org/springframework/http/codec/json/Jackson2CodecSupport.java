package org.springframework.http.codec.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.logging.Log;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpLogging;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeType;
import org.springframework.util.ObjectUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/Jackson2CodecSupport.class */
public abstract class Jackson2CodecSupport {
    private static final String JSON_VIEW_HINT_ERROR = "@JsonView only supported for write hints with exactly 1 class argument: ";
    protected final Log logger = HttpLogging.forLogName(getClass());
    private ObjectMapper defaultObjectMapper;

    @Nullable
    private Map<Class<?>, Map<MimeType, ObjectMapper>> objectMapperRegistrations;
    private final List<MimeType> mimeTypes;
    public static final String JSON_VIEW_HINT = Jackson2CodecSupport.class.getName() + ".jsonView";
    static final String ACTUAL_TYPE_HINT = Jackson2CodecSupport.class.getName() + ".actualType";
    private static final List<MimeType> defaultMimeTypes = List.of(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"), MediaType.APPLICATION_NDJSON);

    @Nullable
    protected abstract <A extends Annotation> A getAnnotation(MethodParameter parameter, Class<A> annotType);

    /* JADX INFO: Access modifiers changed from: protected */
    public Jackson2CodecSupport(ObjectMapper objectMapper, MimeType... mimeTypes) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.defaultObjectMapper = objectMapper;
        this.mimeTypes = !ObjectUtils.isEmpty((Object[]) mimeTypes) ? List.of((Object[]) mimeTypes) : defaultMimeTypes;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.defaultObjectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return this.defaultObjectMapper;
    }

    public void registerObjectMappersForType(Class<?> clazz, Consumer<Map<MimeType, ObjectMapper>> registrar) {
        if (this.objectMapperRegistrations == null) {
            this.objectMapperRegistrations = new LinkedHashMap();
        }
        Map<MimeType, ObjectMapper> registrations = this.objectMapperRegistrations.computeIfAbsent(clazz, c -> {
            return new LinkedHashMap();
        });
        registrar.accept(registrations);
    }

    @Nullable
    public Map<MimeType, ObjectMapper> getObjectMappersForType(Class<?> clazz) {
        for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }
        return Collections.emptyMap();
    }

    protected Map<Class<?>, Map<MimeType, ObjectMapper>> getObjectMapperRegistrations() {
        return this.objectMapperRegistrations != null ? this.objectMapperRegistrations : Collections.emptyMap();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MimeType> getMimeTypes() {
        return this.mimeTypes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MimeType> getMimeTypes(ResolvableType elementType) {
        Class<?> elementClass = elementType.toClass();
        List<MimeType> result = null;
        for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
            if (entry.getKey().isAssignableFrom(elementClass)) {
                result = result != null ? result : new ArrayList<>(entry.getValue().size());
                result.addAll(entry.getValue().keySet());
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            return ProblemDetail.class.isAssignableFrom(elementClass) ? getMediaTypesForProblemDetail() : getMimeTypes();
        }
        return result;
    }

    protected List<MimeType> getMediaTypesForProblemDetail() {
        return Collections.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean supportsMimeType(@Nullable MimeType mimeType) {
        if (mimeType == null) {
            return true;
        }
        for (MimeType supportedMimeType : this.mimeTypes) {
            if (supportedMimeType.isCompatibleWith(mimeType)) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
        if (cause != null && this.logger.isDebugEnabled()) {
            String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") + "serialization for type [" + type + "]";
            this.logger.debug(msg, cause);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        return this.defaultObjectMapper.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, Object> getHints(ResolvableType resolvableType) {
        MethodParameter param = getParameter(resolvableType);
        if (param != null) {
            Map<String, Object> hints = null;
            if (resolvableType.hasGenerics()) {
                hints = new HashMap<>(2);
                hints.put(ACTUAL_TYPE_HINT, resolvableType);
            }
            JsonView annotation = (JsonView) getAnnotation(param, JsonView.class);
            if (annotation != null) {
                Class<?>[] classes = annotation.value();
                Assert.isTrue(classes.length == 1, (Supplier<String>) () -> {
                    return "@JsonView only supported for write hints with exactly 1 class argument: " + param;
                });
                hints = hints != null ? hints : new HashMap<>(1);
                hints.put(JSON_VIEW_HINT, classes[0]);
            }
            if (hints != null) {
                return hints;
            }
        }
        return Hints.none();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public MethodParameter getParameter(ResolvableType type) {
        Object source = type.getSource();
        if (!(source instanceof MethodParameter)) {
            return null;
        }
        MethodParameter methodParameter = (MethodParameter) source;
        return methodParameter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public ObjectMapper selectObjectMapper(ResolvableType targetType, @Nullable MimeType targetMimeType) {
        if (targetMimeType == null || CollectionUtils.isEmpty(this.objectMapperRegistrations)) {
            return this.defaultObjectMapper;
        }
        Class<?> targetClass = targetType.toClass();
        for (Map.Entry<Class<?>, Map<MimeType, ObjectMapper>> typeEntry : getObjectMapperRegistrations().entrySet()) {
            if (typeEntry.getKey().isAssignableFrom(targetClass)) {
                for (Map.Entry<MimeType, ObjectMapper> objectMapperEntry : typeEntry.getValue().entrySet()) {
                    if (objectMapperEntry.getKey().includes(targetMimeType)) {
                        return objectMapperEntry.getValue();
                    }
                }
                return null;
            }
        }
        return this.defaultObjectMapper;
    }
}
