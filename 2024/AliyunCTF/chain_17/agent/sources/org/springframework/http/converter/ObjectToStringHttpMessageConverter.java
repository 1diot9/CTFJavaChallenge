package org.springframework.http.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/ObjectToStringHttpMessageConverter.class */
public class ObjectToStringHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    private final ConversionService conversionService;
    private final StringHttpMessageConverter stringHttpMessageConverter;

    public ObjectToStringHttpMessageConverter(ConversionService conversionService) {
        this(conversionService, StringHttpMessageConverter.DEFAULT_CHARSET);
    }

    public ObjectToStringHttpMessageConverter(ConversionService conversionService, Charset defaultCharset) {
        super(defaultCharset, MediaType.TEXT_PLAIN);
        Assert.notNull(conversionService, "ConversionService is required");
        this.conversionService = conversionService;
        this.stringHttpMessageConverter = new StringHttpMessageConverter(defaultCharset);
    }

    public void setWriteAcceptCharset(boolean writeAcceptCharset) {
        this.stringHttpMessageConverter.setWriteAcceptCharset(writeAcceptCharset);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return canRead(mediaType) && this.conversionService.canConvert(String.class, clazz);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return canWrite(mediaType) && this.conversionService.canConvert(clazz, String.class);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        String value = this.stringHttpMessageConverter.readInternal(String.class, inputMessage);
        Object result = this.conversionService.convert(value, clazz);
        if (result == null) {
            throw new HttpMessageNotReadableException("Unexpected null conversion result for '" + value + "' to " + clazz, inputMessage);
        }
        return result;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException {
        String value = (String) this.conversionService.convert(obj, String.class);
        if (value != null) {
            this.stringHttpMessageConverter.writeInternal(value, outputMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public Long getContentLength(Object obj, @Nullable MediaType contentType) {
        String value = (String) this.conversionService.convert(obj, String.class);
        if (value == null) {
            return 0L;
        }
        return this.stringHttpMessageConverter.getContentLength(value, contentType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Object o) {
        return true;
    }
}
