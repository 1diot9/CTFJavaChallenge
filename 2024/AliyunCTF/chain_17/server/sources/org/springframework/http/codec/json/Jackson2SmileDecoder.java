package org.springframework.http.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/json/Jackson2SmileDecoder.class */
public class Jackson2SmileDecoder extends AbstractJackson2Decoder {
    private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = {new MimeType("application", "x-jackson-smile"), new MimeType("application", "*+x-jackson-smile")};

    public Jackson2SmileDecoder() {
        this(Jackson2ObjectMapperBuilder.smile().build(), DEFAULT_SMILE_MIME_TYPES);
    }

    public Jackson2SmileDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
        Assert.isAssignable(SmileFactory.class, mapper.getFactory().getClass());
    }
}
