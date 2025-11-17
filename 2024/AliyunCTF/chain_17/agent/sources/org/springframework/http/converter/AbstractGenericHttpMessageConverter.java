package org.springframework.http.converter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.lang.Nullable;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/AbstractGenericHttpMessageConverter.class */
public abstract class AbstractGenericHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> implements GenericHttpMessageConverter<T> {
    protected abstract void writeInternal(T t, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractGenericHttpMessageConverter() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractGenericHttpMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractGenericHttpMessageConverter(MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!(type instanceof Class)) {
            return canRead(mediaType);
        }
        Class<?> clazz = (Class) type;
        return canRead(clazz, mediaType);
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
        return canWrite(clazz, mediaType);
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public final void write(final T t, @Nullable final Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final HttpHeaders headers = outputMessage.getHeaders();
        addDefaultHeaders(headers, t, contentType);
        if (outputMessage instanceof StreamingHttpOutputMessage) {
            StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
            streamingOutputMessage.setBody(new StreamingHttpOutputMessage.Body() { // from class: org.springframework.http.converter.AbstractGenericHttpMessageConverter.1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public void writeTo(final OutputStream outputStream) throws IOException {
                    AbstractGenericHttpMessageConverter.this.writeInternal(t, type, new HttpOutputMessage() { // from class: org.springframework.http.converter.AbstractGenericHttpMessageConverter.1.1
                        @Override // org.springframework.http.HttpOutputMessage
                        public OutputStream getBody() {
                            return outputStream;
                        }

                        @Override // org.springframework.http.HttpMessage
                        public HttpHeaders getHeaders() {
                            return headers;
                        }
                    });
                }

                /* JADX WARN: Multi-variable type inference failed */
                @Override // org.springframework.http.StreamingHttpOutputMessage.Body
                public boolean repeatable() {
                    return AbstractGenericHttpMessageConverter.this.supportsRepeatableWrites(t);
                }
            });
        } else {
            writeInternal(t, type, outputMessage);
            outputMessage.getBody().flush();
        }
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal(t, null, outputMessage);
    }
}
