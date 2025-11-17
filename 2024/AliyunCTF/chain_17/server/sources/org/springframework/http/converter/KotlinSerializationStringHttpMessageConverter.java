package org.springframework.http.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerialFormat;
import kotlinx.serialization.SerializationException;
import kotlinx.serialization.StringFormat;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/KotlinSerializationStringHttpMessageConverter.class */
public abstract class KotlinSerializationStringHttpMessageConverter<T extends StringFormat> extends AbstractKotlinSerializationHttpMessageConverter<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.http.converter.AbstractKotlinSerializationHttpMessageConverter
    protected /* bridge */ /* synthetic */ void writeInternal(Object object, KSerializer serializer, SerialFormat format, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal(object, (KSerializer<Object>) serializer, (KSerializer) format, outputMessage);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.http.converter.AbstractKotlinSerializationHttpMessageConverter
    protected /* bridge */ /* synthetic */ Object readInternal(KSerializer serializer, SerialFormat format, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return readInternal((KSerializer<Object>) serializer, (KSerializer) format, inputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public KotlinSerializationStringHttpMessageConverter(T format, MediaType... supportedMediaTypes) {
        super(format, supportedMediaTypes);
    }

    protected Object readInternal(KSerializer<Object> serializer, T format, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Charset charset = charset(inputMessage.getHeaders().getContentType());
        String s = StreamUtils.copyToString(inputMessage.getBody(), charset);
        try {
            return format.decodeFromString(serializer, s);
        } catch (SerializationException ex) {
            throw new HttpMessageNotReadableException("Could not read " + format + ": " + ex.getMessage(), ex, inputMessage);
        }
    }

    protected void writeInternal(Object object, KSerializer<Object> serializer, T format, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            String s = format.encodeToString(serializer, object);
            Charset charset = charset(outputMessage.getHeaders().getContentType());
            outputMessage.getBody().write(s.getBytes(charset));
            outputMessage.getBody().flush();
        } catch (SerializationException ex) {
            throw new HttpMessageNotWritableException("Could not write " + format + ": " + ex.getMessage(), ex);
        }
    }

    private static Charset charset(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return StandardCharsets.UTF_8;
    }
}
