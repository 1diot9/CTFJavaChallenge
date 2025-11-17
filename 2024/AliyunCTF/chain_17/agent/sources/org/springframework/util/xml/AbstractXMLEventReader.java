package org.springframework.util.xml;

import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import org.springframework.util.ClassUtils;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/AbstractXMLEventReader.class */
abstract class AbstractXMLEventReader implements XMLEventReader {
    private boolean closed;

    public Object next() {
        try {
            return nextEvent();
        } catch (XMLStreamException ex) {
            throw new NoSuchElementException(ex.getMessage());
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("remove not supported on " + ClassUtils.getShortName(getClass()));
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        throw new IllegalArgumentException("Property not supported: [" + name + "]");
    }

    public void close() {
        this.closed = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkIfClosed() throws XMLStreamException {
        if (this.closed) {
            throw new XMLStreamException("XMLEventReader has been closed");
        }
    }
}
