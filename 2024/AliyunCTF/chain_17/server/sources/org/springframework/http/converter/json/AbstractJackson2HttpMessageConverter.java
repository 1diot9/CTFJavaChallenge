package org.springframework.http.converter.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.TypeUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/AbstractJackson2HttpMessageConverter.class */
public abstract class AbstractJackson2HttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    private static final Map<String, JsonEncoding> ENCODINGS = CollectionUtils.newHashMap(JsonEncoding.values().length);
    protected ObjectMapper defaultObjectMapper;

    @Nullable
    private Map<Class<?>, Map<MediaType, ObjectMapper>> objectMapperRegistrations;

    @Nullable
    private Boolean prettyPrint;

    @Nullable
    private final PrettyPrinter ssePrettyPrinter;

    static {
        for (JsonEncoding encoding : JsonEncoding.values()) {
            ENCODINGS.put(encoding.getJavaName(), encoding);
        }
        ENCODINGS.put("US-ASCII", JsonEncoding.UTF8);
    }

    protected AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        this.defaultObjectMapper = objectMapper;
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType supportedMediaType) {
        this(objectMapper);
        setSupportedMediaTypes(Collections.singletonList(supportedMediaType));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractJackson2HttpMessageConverter(ObjectMapper objectMapper, MediaType... supportedMediaTypes) {
        this(objectMapper);
        setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        super.setSupportedMediaTypes(supportedMediaTypes);
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.defaultObjectMapper = objectMapper;
        configurePrettyPrint();
    }

    public ObjectMapper getObjectMapper() {
        return this.defaultObjectMapper;
    }

    public void registerObjectMappersForType(Class<?> clazz, Consumer<Map<MediaType, ObjectMapper>> registrar) {
        if (this.objectMapperRegistrations == null) {
            this.objectMapperRegistrations = new LinkedHashMap();
        }
        Map<MediaType, ObjectMapper> registrations = this.objectMapperRegistrations.computeIfAbsent(clazz, c -> {
            return new LinkedHashMap();
        });
        registrar.accept(registrations);
    }

    @Nullable
    public Map<MediaType, ObjectMapper> getObjectMappersForType(Class<?> clazz) {
        for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue();
            }
        }
        return Collections.emptyMap();
    }

    @Override // org.springframework.http.converter.HttpMessageConverter
    public List<MediaType> getSupportedMediaTypes(Class<?> clazz) {
        List<MediaType> result = null;
        for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> entry : getObjectMapperRegistrations().entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                result = result != null ? result : new ArrayList<>(entry.getValue().size());
                result.addAll(entry.getValue().keySet());
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            return ProblemDetail.class.isAssignableFrom(clazz) ? getMediaTypesForProblemDetail() : getSupportedMediaTypes();
        }
        return result;
    }

    private Map<Class<?>, Map<MediaType, ObjectMapper>> getObjectMapperRegistrations() {
        return this.objectMapperRegistrations != null ? this.objectMapperRegistrations : Collections.emptyMap();
    }

    protected List<MediaType> getMediaTypesForProblemDetail() {
        return Collections.emptyList();
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = Boolean.valueOf(prettyPrint);
        configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.defaultObjectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint.booleanValue());
        }
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return canRead(clazz, null, mediaType);
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!canRead(mediaType)) {
            return false;
        }
        JavaType javaType = getJavaType(type, contextClass);
        ObjectMapper objectMapper = selectObjectMapper(javaType.getRawClass(), mediaType);
        if (objectMapper == null) {
            return false;
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (objectMapper.canDeserialize(javaType, causeRef)) {
            return true;
        }
        logWarningIfNecessary(javaType, causeRef.get());
        return false;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!canWrite(mediaType)) {
            return false;
        }
        if (mediaType != null && mediaType.getCharset() != null) {
            Charset charset = mediaType.getCharset();
            if (!ENCODINGS.containsKey(charset.name())) {
                return false;
            }
        }
        ObjectMapper objectMapper = selectObjectMapper(clazz, mediaType);
        if (objectMapper == null) {
            return false;
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (objectMapper.canSerialize(clazz, causeRef)) {
            return true;
        }
        logWarningIfNecessary(clazz, causeRef.get());
        return false;
    }

    @Nullable
    private ObjectMapper selectObjectMapper(Class<?> targetType, @Nullable MediaType targetMediaType) {
        if (targetMediaType == null || CollectionUtils.isEmpty(this.objectMapperRegistrations)) {
            return this.defaultObjectMapper;
        }
        for (Map.Entry<Class<?>, Map<MediaType, ObjectMapper>> typeEntry : getObjectMapperRegistrations().entrySet()) {
            if (typeEntry.getKey().isAssignableFrom(targetType)) {
                for (Map.Entry<MediaType, ObjectMapper> objectMapperEntry : typeEntry.getValue().entrySet()) {
                    if (objectMapperEntry.getKey().includes(targetMediaType)) {
                        return objectMapperEntry.getValue();
                    }
                }
                return null;
            }
        }
        return this.defaultObjectMapper;
    }

    protected void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
        if (cause == null) {
            return;
        }
        boolean debugLevel = (cause instanceof JsonMappingException) && cause.getMessage().startsWith("Cannot find");
        if (debugLevel) {
            if (!this.logger.isDebugEnabled()) {
                return;
            }
        } else if (!this.logger.isWarnEnabled()) {
            return;
        }
        String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") + "serialization for type [" + type + "]";
        if (debugLevel) {
            this.logger.debug(msg, cause);
        } else if (this.logger.isDebugEnabled()) {
            this.logger.warn(msg, cause);
        } else {
            this.logger.warn(msg + ": " + cause);
        }
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(type, contextClass);
        return readJavaType(javaType, inputMessage);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = getJavaType(clazz, null);
        return readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = getCharset(contentType);
        ObjectMapper objectMapper = selectObjectMapper(javaType.getRawClass(), contentType);
        Assert.state(objectMapper != null, (Supplier<String>) () -> {
            return "No ObjectMapper for " + javaType;
        });
        boolean isUnicode = ENCODINGS.containsKey(charset.name()) || "UTF-16".equals(charset.name()) || "UTF-32".equals(charset.name());
        try {
            InputStream inputStream = StreamUtils.nonClosing(inputMessage.getBody());
            if (inputMessage instanceof MappingJacksonInputMessage) {
                MappingJacksonInputMessage mappingJacksonInputMessage = (MappingJacksonInputMessage) inputMessage;
                Class<?> deserializationView = mappingJacksonInputMessage.getDeserializationView();
                if (deserializationView != null) {
                    ObjectReader objectReader = customizeReader(objectMapper.readerWithView(deserializationView).forType(javaType), javaType);
                    if (isUnicode) {
                        return objectReader.readValue(inputStream);
                    }
                    Reader reader = new InputStreamReader(inputStream, charset);
                    return objectReader.readValue(reader);
                }
            }
            ObjectReader objectReader2 = customizeReader(objectMapper.reader().forType(javaType), javaType);
            if (isUnicode) {
                return objectReader2.readValue(inputStream);
            }
            Reader reader2 = new InputStreamReader(inputStream, charset);
            return objectReader2.readValue(reader2);
        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex2) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex2.getOriginalMessage(), ex2, inputMessage);
        }
    }

    protected ObjectReader customizeReader(ObjectReader reader, JavaType javaType) {
        return reader;
    }

    protected Charset getCharset(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = getJsonEncoding(contentType);
        Class<?> clazz = object instanceof MappingJacksonValue ? ((MappingJacksonValue) object).getValue().getClass() : object.getClass();
        ObjectMapper objectMapper = selectObjectMapper(clazz, contentType);
        Assert.state(objectMapper != null, (Supplier<String>) () -> {
            return "No ObjectMapper for " + clazz.getName();
        });
        OutputStream outputStream = StreamUtils.nonClosing(outputMessage.getBody());
        try {
            JsonGenerator generator = objectMapper.getFactory().createGenerator(outputStream, encoding);
            try {
                writePrefix(generator, object);
                Object value = object;
                Class<?> serializationView = null;
                FilterProvider filters = null;
                JavaType javaType = null;
                if (object instanceof MappingJacksonValue) {
                    MappingJacksonValue mappingJacksonValue = (MappingJacksonValue) object;
                    value = mappingJacksonValue.getValue();
                    serializationView = mappingJacksonValue.getSerializationView();
                    filters = mappingJacksonValue.getFilters();
                }
                if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
                    javaType = getJavaType(type, null);
                }
                ObjectWriter objectWriter = serializationView != null ? objectMapper.writerWithView(serializationView) : objectMapper.writer();
                if (filters != null) {
                    objectWriter = objectWriter.with(filters);
                }
                if (javaType != null && (javaType.isContainerType() || javaType.isTypeOrSubTypeOf(Optional.class))) {
                    objectWriter = objectWriter.forType(javaType);
                }
                SerializationConfig config = objectWriter.getConfig();
                if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) && config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                    objectWriter = objectWriter.with(this.ssePrettyPrinter);
                }
                customizeWriter(objectWriter, javaType, contentType).writeValue(generator, value);
                writeSuffix(generator, object);
                generator.flush();
                if (generator != null) {
                    generator.close();
                }
            } finally {
            }
        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex2) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex2.getOriginalMessage(), ex2);
        }
    }

    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable JavaType javaType, @Nullable MediaType contentType) {
        return writer;
    }

    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
    }

    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
    }

    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        return this.defaultObjectMapper.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    protected JsonEncoding getJsonEncoding(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            Charset charset = contentType.getCharset();
            JsonEncoding encoding = ENCODINGS.get(charset.name());
            if (encoding != null) {
                return encoding;
            }
        }
        return JsonEncoding.UTF8;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    @Nullable
    public MediaType getDefaultContentType(Object object) throws IOException {
        if (object instanceof MappingJacksonValue) {
            MappingJacksonValue mappingJacksonValue = (MappingJacksonValue) object;
            object = mappingJacksonValue.getValue();
        }
        return super.getDefaultContentType(object);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(Object object, @Nullable MediaType contentType) throws IOException {
        if (object instanceof MappingJacksonValue) {
            MappingJacksonValue mappingJacksonValue = (MappingJacksonValue) object;
            object = mappingJacksonValue.getValue();
        }
        return super.getContentLength(object, contentType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Object o) {
        return true;
    }
}
