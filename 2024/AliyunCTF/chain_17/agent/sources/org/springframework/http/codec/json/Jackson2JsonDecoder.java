package org.springframework.http.codec.json;

import cn.hutool.core.text.StrPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.CharBufferDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/Jackson2JsonDecoder.class */
public class Jackson2JsonDecoder extends AbstractJackson2Decoder {
    private static final CharBufferDecoder CHAR_BUFFER_DECODER = CharBufferDecoder.textPlainOnly(Arrays.asList(",", StrPool.LF), false);
    private static final ResolvableType CHAR_BUFFER_TYPE = ResolvableType.forClass(CharBuffer.class);

    public Jackson2JsonDecoder() {
        super(Jackson2ObjectMapperBuilder.json().build(), new MimeType[0]);
    }

    public Jackson2JsonDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
    }

    @Override // org.springframework.http.codec.json.AbstractJackson2Decoder
    protected Flux<DataBuffer> processInput(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux<DataBuffer> flux = Flux.from(input);
        if (mimeType == null) {
            return flux;
        }
        Charset charset = mimeType.getCharset();
        if (charset == null || StandardCharsets.UTF_8.equals(charset) || StandardCharsets.US_ASCII.equals(charset)) {
            return flux;
        }
        MimeType textMimeType = new MimeType(MimeTypeUtils.TEXT_PLAIN, charset);
        Flux<CharBuffer> decoded = CHAR_BUFFER_DECODER.decode(input, CHAR_BUFFER_TYPE, textMimeType, (Map<String, Object>) null);
        return decoded.map(charBuffer -> {
            return DefaultDataBufferFactory.sharedInstance.wrap(StandardCharsets.UTF_8.encode(charBuffer));
        });
    }
}
