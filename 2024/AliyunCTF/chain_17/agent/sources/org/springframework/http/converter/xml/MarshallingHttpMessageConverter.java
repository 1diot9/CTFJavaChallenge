package org.springframework.http.converter.xml;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/xml/MarshallingHttpMessageConverter.class */
public class MarshallingHttpMessageConverter extends AbstractXmlHttpMessageConverter<Object> {

    @Nullable
    private Marshaller marshaller;

    @Nullable
    private Unmarshaller unmarshaller;

    public MarshallingHttpMessageConverter() {
    }

    public MarshallingHttpMessageConverter(Marshaller marshaller) {
        Assert.notNull(marshaller, "Marshaller must not be null");
        this.marshaller = marshaller;
        if (marshaller instanceof Unmarshaller) {
            Unmarshaller _unmarshaller = (Unmarshaller) marshaller;
            this.unmarshaller = _unmarshaller;
        }
    }

    public MarshallingHttpMessageConverter(Marshaller marshaller, Unmarshaller unmarshaller) {
        Assert.notNull(marshaller, "Marshaller must not be null");
        Assert.notNull(unmarshaller, "Unmarshaller must not be null");
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return canRead(mediaType) && this.unmarshaller != null && this.unmarshaller.supports(clazz);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return canWrite(mediaType) && this.marshaller != null && this.marshaller.supports(clazz);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    protected Object readFromSource(Class<? extends Object> clazz, HttpHeaders headers, Source source) throws Exception {
        Assert.state(this.unmarshaller != null, "Property 'unmarshaller' is required");
        Object result = this.unmarshaller.unmarshal(source);
        if (!clazz.isInstance(result)) {
            throw new TypeMismatchException(result, clazz);
        }
        return result;
    }

    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
        Assert.state(this.marshaller != null, "Property 'marshaller' is required");
        this.marshaller.marshal(o, result);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Object o) {
        return true;
    }
}
