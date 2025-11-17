package org.springframework.http.codec.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: agent.jar:BOOT-INF/lib/spring-web-6.1.3.jar:org/springframework/http/codec/xml/Jaxb2XmlDecoder.class */
public class Jaxb2XmlDecoder extends AbstractDecoder<Object> {
    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
    private final XmlEventDecoder xmlEventDecoder;
    private final JaxbContextContainer jaxbContexts;
    private Function<Unmarshaller, Unmarshaller> unmarshallerProcessor;
    private int maxInMemorySize;

    public Jaxb2XmlDecoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML, new MediaType("application", "*+xml"));
        this.xmlEventDecoder = new XmlEventDecoder();
        this.jaxbContexts = new JaxbContextContainer();
        this.unmarshallerProcessor = Function.identity();
        this.maxInMemorySize = 262144;
    }

    public Jaxb2XmlDecoder(MimeType... supportedMimeTypes) {
        super(supportedMimeTypes);
        this.xmlEventDecoder = new XmlEventDecoder();
        this.jaxbContexts = new JaxbContextContainer();
        this.unmarshallerProcessor = Function.identity();
        this.maxInMemorySize = 262144;
    }

    public void setUnmarshallerProcessor(Function<Unmarshaller, Unmarshaller> processor) {
        this.unmarshallerProcessor = this.unmarshallerProcessor.andThen(processor);
    }

    public Function<Unmarshaller, Unmarshaller> getUnmarshallerProcessor() {
        return this.unmarshallerProcessor;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.maxInMemorySize = byteCount;
        this.xmlEventDecoder.setMaxInMemorySize(byteCount);
    }

    public int getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> outputClass = elementType.toClass();
        return (outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlType.class)) && super.canDecode(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux<XMLEvent> xmlEventFlux = this.xmlEventDecoder.decode(inputStream, ResolvableType.forClass(XMLEvent.class), mimeType, hints);
        Class<?> outputClass = elementType.toClass();
        Set<QName> typeNames = Jaxb2Helper.toQNames(outputClass);
        Flux<List<XMLEvent>> splitEvents = Jaxb2Helper.split(xmlEventFlux, typeNames);
        return splitEvents.map(events -> {
            Object value = unmarshal(events, outputClass);
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
                return Hints.getLogPrefix(hints) + "Decoded [" + formatted + "]";
            });
            return value;
        });
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public Mono<Object> decodeToMono(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return DataBufferUtils.join(input, this.maxInMemorySize).map(dataBuffer -> {
            return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
        });
    }

    @Override // org.springframework.core.codec.Decoder
    @NonNull
    public Object decode(DataBuffer dataBuffer, ResolvableType targetType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) throws DecodingException {
        try {
            try {
                try {
                    XMLEventReader createXMLEventReader = inputFactory.createXMLEventReader(dataBuffer.asInputStream(), encoding(mimeType));
                    List<XMLEvent> events = new ArrayList<>();
                    createXMLEventReader.forEachRemaining(event -> {
                        events.add((XMLEvent) event);
                    });
                    Object unmarshal = unmarshal(events, targetType.toClass());
                    DataBufferUtils.release(dataBuffer);
                    return unmarshal;
                } catch (XMLStreamException ex) {
                    throw new DecodingException(ex.getMessage(), ex);
                }
            } catch (Throwable ex2) {
                Throwable cause = ex2.getCause();
                if (cause instanceof XMLStreamException) {
                    throw new DecodingException(cause.getMessage(), cause);
                }
                throw Exceptions.propagate(ex2);
            }
        } catch (Throwable th) {
            DataBufferUtils.release(dataBuffer);
            throw th;
        }
    }

    @Nullable
    private static String encoding(@Nullable MimeType mimeType) {
        Charset charset;
        if (mimeType == null || (charset = mimeType.getCharset()) == null) {
            return null;
        }
        return charset.name();
    }

    private Object unmarshal(List<XMLEvent> events, Class<?> outputClass) {
        try {
            Unmarshaller unmarshaller = initUnmarshaller(outputClass);
            XMLEventReader eventReader = StaxUtils.createXMLEventReader(events);
            if (outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlSeeAlso.class)) {
                return unmarshaller.unmarshal(eventReader);
            }
            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(eventReader, outputClass);
            return jaxbElement.getValue();
        } catch (JAXBException ex) {
            throw new CodecException("Invalid JAXB configuration", ex);
        } catch (UnmarshalException ex2) {
            throw new DecodingException("Could not unmarshal XML to " + outputClass, ex2);
        }
    }

    private Unmarshaller initUnmarshaller(Class<?> outputClass) throws CodecException, JAXBException {
        Unmarshaller unmarshaller = this.jaxbContexts.createUnmarshaller(outputClass);
        return this.unmarshallerProcessor.apply(unmarshaller);
    }
}
