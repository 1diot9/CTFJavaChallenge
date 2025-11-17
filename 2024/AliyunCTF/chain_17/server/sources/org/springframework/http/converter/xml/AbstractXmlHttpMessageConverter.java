package org.springframework.http.converter.xml;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

/* loaded from: server.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/xml/AbstractXmlHttpMessageConverter.class */
public abstract class AbstractXmlHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> {
    private final TransformerFactory transformerFactory;

    protected abstract T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source) throws Exception;

    protected abstract void writeToResult(T t, HttpHeaders headers, Result result) throws Exception;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractXmlHttpMessageConverter() {
        super(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
        this.transformerFactory = TransformerFactory.newInstance();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public final T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            InputStream inputStream = StreamUtils.nonClosing(inputMessage.getBody());
            return readFromSource(clazz, inputMessage.getHeaders(), new StreamSource(inputStream));
        } catch (IOException | HttpMessageConversionException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new HttpMessageNotReadableException("Could not unmarshal to [" + clazz + "]: " + ex2, ex2, inputMessage);
        }
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected final void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            writeToResult(t, outputMessage.getHeaders(), new StreamResult(outputMessage.getBody()));
        } catch (IOException | HttpMessageConversionException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new HttpMessageNotWritableException("Could not marshal [" + t + "]: " + ex2.getMessage(), ex2);
        }
    }

    protected void transform(Source source, Result result) throws TransformerException {
        this.transformerFactory.newTransformer().transform(source, result);
    }
}
