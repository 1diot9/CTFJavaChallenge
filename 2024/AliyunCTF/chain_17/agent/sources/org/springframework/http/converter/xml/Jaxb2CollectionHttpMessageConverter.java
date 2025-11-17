package org.springframework.http.converter.xml;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.xml.StaxUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/xml/Jaxb2CollectionHttpMessageConverter.class */
public class Jaxb2CollectionHttpMessageConverter<T extends Collection> extends AbstractJaxb2HttpMessageConverter<T> implements GenericHttpMessageConverter<T> {
    private final XMLInputFactory inputFactory = createXmlInputFactory();

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public /* bridge */ /* synthetic */ Object read(Type type, @Nullable Class contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return read(type, (Class<?>) contextClass, inputMessage);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type rawType = parameterizedType.getRawType();
        if (!(rawType instanceof Class)) {
            return false;
        }
        Class<?> rawType2 = (Class) rawType;
        if (!Collection.class.isAssignableFrom(rawType2) || parameterizedType.getActualTypeArguments().length != 1) {
            return false;
        }
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        if (!(typeArgument instanceof Class)) {
            return false;
        }
        Class<?> typeArgumentClass = (Class) typeArgument;
        return (typeArgumentClass.isAnnotationPresent(XmlRootElement.class) || typeArgumentClass.isAnnotationPresent(XmlType.class)) && canRead(mediaType);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, @Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    public T readFromSource(Class<? extends T> clazz, HttpHeaders headers, Source source) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public T read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        T result = createCollection((Class) parameterizedType.getRawType());
        Class<?> elementClass = (Class) parameterizedType.getActualTypeArguments()[0];
        try {
            Unmarshaller unmarshaller = createUnmarshaller(elementClass);
            XMLStreamReader streamReader = this.inputFactory.createXMLStreamReader(inputMessage.getBody());
            int event = moveToFirstChildOfRootElement(streamReader);
            while (event != 8) {
                if (elementClass.isAnnotationPresent(XmlRootElement.class)) {
                    result.add(unmarshaller.unmarshal(streamReader));
                } else if (elementClass.isAnnotationPresent(XmlType.class)) {
                    result.add(unmarshaller.unmarshal(streamReader, elementClass).getValue());
                } else {
                    throw new HttpMessageNotReadableException("Cannot unmarshal to [" + elementClass + "]", inputMessage);
                }
                event = moveToNextElement(streamReader);
            }
            return result;
        } catch (JAXBException ex) {
            throw new HttpMessageConversionException("Invalid JAXB setup: " + ex.getMessage(), ex);
        } catch (XMLStreamException ex2) {
            throw new HttpMessageNotReadableException("Failed to read XML stream: " + ex2.getMessage(), ex2, inputMessage);
        } catch (UnmarshalException ex3) {
            throw new HttpMessageNotReadableException("Could not unmarshal to [" + elementClass + "]: " + ex3, ex3, inputMessage);
        }
    }

    protected T createCollection(Class<?> collectionClass) {
        if (!collectionClass.isInterface()) {
            try {
                return (T) ReflectionUtils.accessibleConstructor(collectionClass, new Class[0]).newInstance(new Object[0]);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Could not instantiate collection class: " + collectionClass.getName(), ex);
            }
        }
        if (List.class == collectionClass) {
            return new ArrayList();
        }
        if (SortedSet.class == collectionClass) {
            return new TreeSet();
        }
        return new LinkedHashSet();
    }

    private int moveToFirstChildOfRootElement(XMLStreamReader streamReader) throws XMLStreamException {
        int event;
        int event2 = streamReader.next();
        while (event2 != 1) {
            event2 = streamReader.next();
        }
        int next = streamReader.next();
        while (true) {
            event = next;
            if (event == 1 || event == 8) {
                break;
            }
            next = streamReader.next();
        }
        return event;
    }

    private int moveToNextElement(XMLStreamReader streamReader) throws XMLStreamException {
        int event;
        int eventType = streamReader.getEventType();
        while (true) {
            event = eventType;
            if (event == 1 || event == 8) {
                break;
            }
            eventType = streamReader.next();
        }
        return event;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public void write(T t, @Nullable Type type, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    public void writeToResult(T t, HttpHeaders headers, Result result) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected XMLInputFactory createXmlInputFactory() {
        return StaxUtils.createDefensiveInputFactory();
    }
}
