package org.springframework.http.converter;

import java.io.IOException;
import kotlinx.serialization.BinaryFormat;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerialFormat;
import kotlinx.serialization.SerializationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/KotlinSerializationBinaryHttpMessageConverter.class */
public abstract class KotlinSerializationBinaryHttpMessageConverter<T extends BinaryFormat> extends AbstractKotlinSerializationHttpMessageConverter<T> {
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
    public KotlinSerializationBinaryHttpMessageConverter(T format, MediaType... supportedMediaTypes) {
        super(format, supportedMediaTypes);
    }

    protected Object readInternal(KSerializer<Object> serializer, T format, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        byte[] bytes = StreamUtils.copyToByteArray(inputMessage.getBody());
        try {
            return format.decodeFromByteArray(serializer, bytes);
        } catch (SerializationException ex) {
            throw new HttpMessageNotReadableException("Could not read " + format + ": " + ex.getMessage(), ex, inputMessage);
        }
    }

    protected void writeInternal(Object object, KSerializer<Object> serializer, T format, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            byte[] bytes = format.encodeToByteArray(serializer, object);
            outputMessage.getBody().write(bytes);
            outputMessage.getBody().flush();
        } catch (SerializationException ex) {
            throw new HttpMessageNotWritableException("Could not write " + format + ": " + ex.getMessage(), ex);
        }
    }
}
