package org.springframework.http.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/GenericHttpMessageConverter.class */
public interface GenericHttpMessageConverter<T> extends HttpMessageConverter<T> {
    boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType);

    T read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;

    boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType);

    void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException;
}
