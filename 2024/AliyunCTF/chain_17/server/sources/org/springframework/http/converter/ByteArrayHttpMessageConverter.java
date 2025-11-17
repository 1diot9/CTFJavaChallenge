package org.springframework.http.converter;

import java.io.IOException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/ByteArrayHttpMessageConverter.class */
public class ByteArrayHttpMessageConverter extends AbstractHttpMessageConverter<byte[]> {
    public ByteArrayHttpMessageConverter() {
        super(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supports(Class<?> clazz) {
        return byte[].class == clazz;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public byte[] readInternal(Class<? extends byte[]> clazz, HttpInputMessage message) throws IOException {
        long length = message.getHeaders().getContentLength();
        return (length < 0 || length >= 2147483647L) ? message.getBody().readAllBytes() : message.getBody().readNBytes((int) length);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(byte[] bytes, @Nullable MediaType contentType) {
        return Long.valueOf(bytes.length);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void writeInternal(byte[] bytes, HttpOutputMessage outputMessage) throws IOException {
        StreamUtils.copy(bytes, outputMessage.getBody());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(byte[] bytes) {
        return true;
    }
}
