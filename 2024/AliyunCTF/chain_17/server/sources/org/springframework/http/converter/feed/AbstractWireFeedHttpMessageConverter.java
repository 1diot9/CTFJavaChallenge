package org.springframework.http.converter.feed;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import com.rometools.rome.io.WireFeedOutput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/feed/AbstractWireFeedHttpMessageConverter.class */
public abstract class AbstractWireFeedHttpMessageConverter<T extends WireFeed> extends AbstractHttpMessageConverter<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractWireFeedHttpMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public T readInternal(Class<? extends T> cls, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        WireFeedInput wireFeedInput = new WireFeedInput();
        MediaType contentType = httpInputMessage.getHeaders().getContentType();
        try {
            return (T) wireFeedInput.build(new InputStreamReader(StreamUtils.nonClosing(httpInputMessage.getBody()), (contentType == null || contentType.getCharset() == null) ? DEFAULT_CHARSET : contentType.getCharset()));
        } catch (FeedException e) {
            throw new HttpMessageNotReadableException("Could not read WireFeed: " + e.getMessage(), e, httpInputMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public void writeInternal(T wireFeed, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Charset charset = StringUtils.hasLength(wireFeed.getEncoding()) ? Charset.forName(wireFeed.getEncoding()) : DEFAULT_CHARSET;
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType != null) {
            outputMessage.getHeaders().setContentType(new MediaType(contentType, charset));
        }
        WireFeedOutput feedOutput = new WireFeedOutput();
        try {
            Writer writer = new OutputStreamWriter(outputMessage.getBody(), charset);
            feedOutput.output(wireFeed, writer);
        } catch (FeedException ex) {
            throw new HttpMessageNotWritableException("Could not write WireFeed: " + ex.getMessage(), ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(T t) {
        return true;
    }
}
