package org.springframework.http.converter.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/json/AbstractJsonHttpMessageConverter.class */
public abstract class AbstractJsonHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Nullable
    private String jsonPrefix;

    protected abstract Object readInternal(Type resolvedType, Reader reader) throws Exception;

    protected abstract void writeInternal(Object object, @Nullable Type type, Writer writer) throws Exception;

    public AbstractJsonHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        setDefaultCharset(DEFAULT_CHARSET);
    }

    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = prefixJson ? ")]}', " : null;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public final Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readResolved(GenericTypeResolver.resolveType(type, contextClass), inputMessage);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readResolved(clazz, inputMessage);
    }

    private Object readResolved(Type resolvedType, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Reader reader = getReader(inputMessage);
        try {
            return readInternal(resolvedType, reader);
        } catch (Exception ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex, inputMessage);
        }
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter
    protected final void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Writer writer = getWriter(outputMessage);
        if (this.jsonPrefix != null) {
            writer.append((CharSequence) this.jsonPrefix);
        }
        try {
            writeInternal(object, type, writer);
            writer.flush();
        } catch (Exception ex) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    private static Reader getReader(HttpInputMessage inputMessage) throws IOException {
        return new InputStreamReader(inputMessage.getBody(), getCharset(inputMessage.getHeaders()));
    }

    private static Writer getWriter(HttpOutputMessage outputMessage) throws IOException {
        return new OutputStreamWriter(outputMessage.getBody(), getCharset(outputMessage.getHeaders()));
    }

    private static Charset getCharset(HttpHeaders headers) {
        Charset charset = headers.getContentType() != null ? headers.getContentType().getCharset() : null;
        return charset != null ? charset : DEFAULT_CHARSET;
    }
}
