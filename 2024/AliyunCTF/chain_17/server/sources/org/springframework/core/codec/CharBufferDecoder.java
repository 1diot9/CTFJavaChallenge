package org.springframework.core.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/core/codec/CharBufferDecoder.class */
public final class CharBufferDecoder extends AbstractCharSequenceDecoder<CharBuffer> {
    public CharBufferDecoder(List<String> delimiters, boolean stripDelimiter, MimeType... mimeTypes) {
        super(delimiters, stripDelimiter, mimeTypes);
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return elementType.resolve() == CharBuffer.class && super.canDecode(elementType, mimeType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.core.codec.AbstractCharSequenceDecoder
    public CharBuffer decodeInternal(DataBuffer dataBuffer, Charset charset) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(dataBuffer.readableByteCount());
        dataBuffer.toByteBuffer(byteBuffer);
        return charset.decode(byteBuffer);
    }

    public static CharBufferDecoder textPlainOnly() {
        return textPlainOnly(DEFAULT_DELIMITERS, true);
    }

    public static CharBufferDecoder textPlainOnly(List<String> delimiters, boolean stripDelimiter) {
        MimeType textPlain = new MimeType("text", "plain", DEFAULT_CHARSET);
        return new CharBufferDecoder(delimiters, stripDelimiter, textPlain);
    }

    public static CharBufferDecoder allMimeTypes() {
        return allMimeTypes(DEFAULT_DELIMITERS, true);
    }

    public static CharBufferDecoder allMimeTypes(List<String> delimiters, boolean stripDelimiter) {
        MimeType textPlain = new MimeType("text", "plain", DEFAULT_CHARSET);
        return new CharBufferDecoder(delimiters, stripDelimiter, textPlain, MimeTypeUtils.ALL);
    }
}
