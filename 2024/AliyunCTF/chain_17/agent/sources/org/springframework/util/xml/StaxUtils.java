package org.springframework.util.xml;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import org.springframework.lang.Nullable;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/StaxUtils.class */
public abstract class StaxUtils {
    private static final XMLResolver NO_OP_XML_RESOLVER = (publicID, systemID, base, ns) -> {
        return InputStream.nullInputStream();
    };

    public static XMLInputFactory createDefensiveInputFactory() {
        return createDefensiveInputFactory(XMLInputFactory::newInstance);
    }

    public static <T extends XMLInputFactory> T createDefensiveInputFactory(Supplier<T> instanceSupplier) {
        T inputFactory = instanceSupplier.get();
        inputFactory.setProperty("javax.xml.stream.supportDTD", false);
        inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        inputFactory.setXMLResolver(NO_OP_XML_RESOLVER);
        return inputFactory;
    }

    public static Source createStaxSource(XMLStreamReader streamReader) {
        return new StAXSource(streamReader);
    }

    public static Source createStaxSource(XMLEventReader eventReader) throws XMLStreamException {
        return new StAXSource(eventReader);
    }

    public static Source createCustomStaxSource(XMLStreamReader streamReader) {
        return new StaxSource(streamReader);
    }

    public static Source createCustomStaxSource(XMLEventReader eventReader) {
        return new StaxSource(eventReader);
    }

    public static boolean isStaxSource(Source source) {
        return (source instanceof StAXSource) || (source instanceof StaxSource);
    }

    @Nullable
    public static XMLStreamReader getXMLStreamReader(Source source) {
        if (source instanceof StAXSource) {
            StAXSource stAXSource = (StAXSource) source;
            return stAXSource.getXMLStreamReader();
        }
        if (source instanceof StaxSource) {
            StaxSource staxSource = (StaxSource) source;
            return staxSource.getXMLStreamReader();
        }
        throw new IllegalArgumentException("Source '" + source + "' is neither StaxSource nor StAXSource");
    }

    @Nullable
    public static XMLEventReader getXMLEventReader(Source source) {
        if (source instanceof StAXSource) {
            StAXSource stAXSource = (StAXSource) source;
            return stAXSource.getXMLEventReader();
        }
        if (source instanceof StaxSource) {
            StaxSource staxSource = (StaxSource) source;
            return staxSource.getXMLEventReader();
        }
        throw new IllegalArgumentException("Source '" + source + "' is neither StaxSource nor StAXSource");
    }

    public static Result createStaxResult(XMLStreamWriter streamWriter) {
        return new StAXResult(streamWriter);
    }

    public static Result createStaxResult(XMLEventWriter eventWriter) {
        return new StAXResult(eventWriter);
    }

    public static Result createCustomStaxResult(XMLStreamWriter streamWriter) {
        return new StaxResult(streamWriter);
    }

    public static Result createCustomStaxResult(XMLEventWriter eventWriter) {
        return new StaxResult(eventWriter);
    }

    public static boolean isStaxResult(Result result) {
        return (result instanceof StAXResult) || (result instanceof StaxResult);
    }

    @Nullable
    public static XMLStreamWriter getXMLStreamWriter(Result result) {
        if (result instanceof StAXResult) {
            StAXResult stAXResult = (StAXResult) result;
            return stAXResult.getXMLStreamWriter();
        }
        if (result instanceof StaxResult) {
            StaxResult staxResult = (StaxResult) result;
            return staxResult.getXMLStreamWriter();
        }
        throw new IllegalArgumentException("Result '" + result + "' is neither StaxResult nor StAXResult");
    }

    @Nullable
    public static XMLEventWriter getXMLEventWriter(Result result) {
        if (result instanceof StAXResult) {
            StAXResult stAXResult = (StAXResult) result;
            return stAXResult.getXMLEventWriter();
        }
        if (result instanceof StaxResult) {
            StaxResult staxResult = (StaxResult) result;
            return staxResult.getXMLEventWriter();
        }
        throw new IllegalArgumentException("Result '" + result + "' is neither StaxResult nor StAXResult");
    }

    public static XMLEventReader createXMLEventReader(List<XMLEvent> events) {
        return new ListBasedXMLEventReader(events);
    }

    public static ContentHandler createContentHandler(XMLStreamWriter streamWriter) {
        return new StaxStreamHandler(streamWriter);
    }

    public static ContentHandler createContentHandler(XMLEventWriter eventWriter) {
        return new StaxEventHandler(eventWriter);
    }

    public static XMLReader createXMLReader(XMLStreamReader streamReader) {
        return new StaxStreamXMLReader(streamReader);
    }

    public static XMLReader createXMLReader(XMLEventReader eventReader) {
        return new StaxEventXMLReader(eventReader);
    }

    public static XMLStreamReader createEventStreamReader(XMLEventReader eventReader) throws XMLStreamException {
        return new XMLEventStreamReader(eventReader);
    }

    public static XMLStreamWriter createEventStreamWriter(XMLEventWriter eventWriter) {
        return new XMLEventStreamWriter(eventWriter, XMLEventFactory.newFactory());
    }

    public static XMLStreamWriter createEventStreamWriter(XMLEventWriter eventWriter, XMLEventFactory eventFactory) {
        return new XMLEventStreamWriter(eventWriter, eventFactory);
    }
}
