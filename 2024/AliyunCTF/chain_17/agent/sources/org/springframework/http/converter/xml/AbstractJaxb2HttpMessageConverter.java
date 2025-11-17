package org.springframework.http.converter.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.http.converter.HttpMessageConversionException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/xml/AbstractJaxb2HttpMessageConverter.class */
public abstract class AbstractJaxb2HttpMessageConverter<T> extends AbstractXmlHttpMessageConverter<T> {
    private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap(64);

    /* JADX INFO: Access modifiers changed from: protected */
    public final Marshaller createMarshaller(Class<?> clazz) {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            Marshaller marshaller = jaxbContext.createMarshaller();
            customizeMarshaller(marshaller);
            return marshaller;
        } catch (JAXBException ex) {
            throw new HttpMessageConversionException("Could not create Marshaller for class [" + clazz + "]: " + ex.getMessage(), ex);
        }
    }

    protected void customizeMarshaller(Marshaller marshaller) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Unmarshaller createUnmarshaller(Class<?> clazz) {
        try {
            JAXBContext jaxbContext = getJaxbContext(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            customizeUnmarshaller(unmarshaller);
            return unmarshaller;
        } catch (JAXBException ex) {
            throw new HttpMessageConversionException("Could not create Unmarshaller for class [" + clazz + "]: " + ex.getMessage(), ex);
        }
    }

    protected void customizeUnmarshaller(Unmarshaller unmarshaller) {
    }

    protected final JAXBContext getJaxbContext(Class<?> clazz) {
        return this.jaxbContexts.computeIfAbsent(clazz, key -> {
            try {
                return JAXBContext.newInstance(new Class[]{clazz});
            } catch (JAXBException ex) {
                throw new HttpMessageConversionException("Could not create JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
            }
        });
    }
}
