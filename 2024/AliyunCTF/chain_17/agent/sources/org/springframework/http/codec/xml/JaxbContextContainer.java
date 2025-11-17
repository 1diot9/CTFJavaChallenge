package org.springframework.http.codec.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.core.codec.CodecException;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/xml/JaxbContextContainer.class */
final class JaxbContextContainer {
    private final ConcurrentMap<Class<?>, JAXBContext> jaxbContexts = new ConcurrentHashMap(64);

    public Marshaller createMarshaller(Class<?> clazz) throws CodecException, JAXBException {
        JAXBContext jaxbContext = getJaxbContext(clazz);
        return jaxbContext.createMarshaller();
    }

    public Unmarshaller createUnmarshaller(Class<?> clazz) throws CodecException, JAXBException {
        JAXBContext jaxbContext = getJaxbContext(clazz);
        return jaxbContext.createUnmarshaller();
    }

    private JAXBContext getJaxbContext(Class<?> clazz) throws CodecException {
        return this.jaxbContexts.computeIfAbsent(clazz, key -> {
            try {
                return JAXBContext.newInstance(new Class[]{clazz});
            } catch (JAXBException ex) {
                throw new CodecException("Could not create JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
            }
        });
    }
}
