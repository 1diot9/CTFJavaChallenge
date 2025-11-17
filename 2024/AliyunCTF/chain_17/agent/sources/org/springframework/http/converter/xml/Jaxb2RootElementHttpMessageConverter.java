package org.springframework.http.converter.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.MarshalException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/converter/xml/Jaxb2RootElementHttpMessageConverter.class */
public class Jaxb2RootElementHttpMessageConverter extends AbstractJaxb2HttpMessageConverter<Object> {
    private boolean supportDtd = false;
    private boolean processExternalEntities = false;
    private static final EntityResolver NO_OP_ENTITY_RESOLVER = (publicId, systemId) -> {
        return new InputSource(new StringReader(""));
    };

    public void setSupportDtd(boolean supportDtd) {
        this.supportDtd = supportDtd;
    }

    public boolean isSupportDtd() {
        return this.supportDtd;
    }

    public void setProcessExternalEntities(boolean processExternalEntities) {
        this.processExternalEntities = processExternalEntities;
        if (processExternalEntities) {
            this.supportDtd = true;
        }
    }

    public boolean isProcessExternalEntities() {
        return this.processExternalEntities;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return (clazz.isAnnotationPresent(XmlRootElement.class) || clazz.isAnnotationPresent(XmlType.class)) && canRead(mediaType);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null && canWrite(mediaType);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    protected Object readFromSource(Class<?> clazz, HttpHeaders headers, Source source) throws Exception {
        try {
            Source source2 = processSource(source);
            Unmarshaller unmarshaller = createUnmarshaller(clazz);
            if (clazz.isAnnotationPresent(XmlRootElement.class)) {
                return unmarshaller.unmarshal(source2);
            }
            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(source2, clazz);
            return jaxbElement.getValue();
        } catch (UnmarshalException ex) {
            throw ex;
        } catch (NullPointerException ex2) {
            if (!isSupportDtd()) {
                throw new IllegalStateException("NPE while unmarshalling. This can happen due to the presence of DTD declarations which are disabled.", ex2);
            }
            throw ex2;
        } catch (JAXBException ex3) {
            throw new HttpMessageConversionException("Invalid JAXB setup: " + ex3.getMessage(), ex3);
        }
    }

    protected Source processSource(Source source) {
        if (source instanceof StreamSource) {
            StreamSource streamSource = (StreamSource) source;
            InputSource inputSource = new InputSource(streamSource.getInputStream());
            try {
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                saxParserFactory.setNamespaceAware(true);
                saxParserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !isSupportDtd());
                saxParserFactory.setFeature("http://xml.org/sax/features/external-general-entities", isProcessExternalEntities());
                SAXParser saxParser = saxParserFactory.newSAXParser();
                XMLReader xmlReader = saxParser.getXMLReader();
                if (!isProcessExternalEntities()) {
                    xmlReader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
                }
                return new SAXSource(xmlReader, inputSource);
            } catch (ParserConfigurationException | SAXException ex) {
                this.logger.warn("Processing of external entities could not be disabled", ex);
                return source;
            }
        }
        return source;
    }

    @Override // org.springframework.http.converter.xml.AbstractXmlHttpMessageConverter
    protected void writeToResult(Object o, HttpHeaders headers, Result result) throws Exception {
        try {
            Class<?> clazz = ClassUtils.getUserClass(o);
            Marshaller marshaller = createMarshaller(clazz);
            setCharset(headers.getContentType(), marshaller);
            marshaller.marshal(o, result);
        } catch (MarshalException ex) {
            throw ex;
        } catch (JAXBException ex2) {
            throw new HttpMessageConversionException("Invalid JAXB setup: " + ex2.getMessage(), ex2);
        }
    }

    private void setCharset(@Nullable MediaType contentType, Marshaller marshaller) throws PropertyException {
        if (contentType != null && contentType.getCharset() != null) {
            marshaller.setProperty("jaxb.encoding", contentType.getCharset().name());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supportsRepeatableWrites(Object o) {
        return true;
    }
}
