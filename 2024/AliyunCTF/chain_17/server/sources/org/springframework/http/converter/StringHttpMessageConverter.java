package org.springframework.http.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/StringHttpMessageConverter.class */
public class StringHttpMessageConverter extends AbstractHttpMessageConverter<String> {
    private static final MediaType APPLICATION_PLUS_JSON = new MediaType("application", "*+json");
    public static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;

    @Nullable
    private volatile List<Charset> availableCharsets;
    private boolean writeAcceptCharset;

    public StringHttpMessageConverter() {
        this(DEFAULT_CHARSET);
    }

    public StringHttpMessageConverter(Charset defaultCharset) {
        super(defaultCharset, MediaType.TEXT_PLAIN, MediaType.ALL);
        this.writeAcceptCharset = false;
    }

    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.writeAcceptCharset = writeAcceptCharset;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supports(Class<?> clazz) {
        return String.class == clazz;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public String readInternal(Class<? extends String> clazz, HttpInputMessage inputMessage) throws IOException {
        byte[] readAllBytes;
        Charset charset = getContentTypeCharset(inputMessage.getHeaders().getContentType());
        long length = inputMessage.getHeaders().getContentLength();
        if (length >= 0 && length <= 2147483647L) {
            readAllBytes = inputMessage.getBody().readNBytes((int) length);
        } else {
            readAllBytes = inputMessage.getBody().readAllBytes();
        }
        byte[] bytes = readAllBytes;
        return new String(bytes, charset);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(String str, @Nullable MediaType contentType) {
        Charset charset = getContentTypeCharset(contentType);
        return Long.valueOf(str.getBytes(charset).length);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void addDefaultHeaders(HttpHeaders headers, String s, @Nullable MediaType type) throws IOException {
        if (headers.getContentType() == null && type != null && type.isConcrete() && (type.isCompatibleWith(MediaType.APPLICATION_JSON) || type.isCompatibleWith(APPLICATION_PLUS_JSON))) {
            headers.setContentType(type);
        }
        super.addDefaultHeaders(headers, (HttpHeaders) s, type);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void writeInternal(String str, HttpOutputMessage outputMessage) throws IOException {
        HttpHeaders headers = outputMessage.getHeaders();
        if (this.writeAcceptCharset && headers.get(HttpHeaders.ACCEPT_CHARSET) == null) {
            headers.setAcceptCharset(getAcceptedCharsets());
        }
        Charset charset = getContentTypeCharset(headers.getContentType());
        StreamUtils.copy(str, charset, outputMessage.getBody());
    }

    protected List<Charset> getAcceptedCharsets() {
        List<Charset> charsets = this.availableCharsets;
        if (charsets == null) {
            charsets = new ArrayList(Charset.availableCharsets().values());
            this.availableCharsets = charsets;
        }
        return charsets;
    }

    private Charset getContentTypeCharset(@Nullable MediaType contentType) {
        if (contentType != null) {
            Charset charset = contentType.getCharset();
            if (charset != null) {
                return charset;
            }
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON) || contentType.isCompatibleWith(APPLICATION_PLUS_JSON)) {
                return StandardCharsets.UTF_8;
            }
        }
        Charset charset2 = getDefaultCharset();
        Assert.state(charset2 != null, "No default charset");
        return charset2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(String s) {
        return true;
    }
}
