package org.springframework.core.codec;

import java.nio.charset.Charset;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/StringDecoder.class */
public final class StringDecoder extends AbstractCharSequenceDecoder<String> {
    private StringDecoder(List<String> delimiters, boolean stripDelimiter, MimeType... mimeTypes) {
        super(delimiters, stripDelimiter, mimeTypes);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return elementType.resolve() == String.class && super.canDecode(elementType, mimeType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.AbstractCharSequenceDecoder
    public String decodeInternal(DataBuffer dataBuffer, Charset charset) {
        return dataBuffer.toString(charset);
    }

    public static StringDecoder textPlainOnly() {
        return textPlainOnly(DEFAULT_DELIMITERS, true);
    }

    public static StringDecoder textPlainOnly(List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET));
    }

    public static StringDecoder allMimeTypes() {
        return allMimeTypes(DEFAULT_DELIMITERS, true);
    }

    public static StringDecoder allMimeTypes(List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET), MimeTypeUtils.ALL);
    }
}
