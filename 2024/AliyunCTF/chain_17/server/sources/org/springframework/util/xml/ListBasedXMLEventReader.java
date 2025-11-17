package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/ListBasedXMLEventReader.class */
class ListBasedXMLEventReader extends AbstractXMLEventReader {
    private final List<XMLEvent> events;

    @Nullable
    private XMLEvent currentEvent;
    private int cursor = 0;

    public ListBasedXMLEventReader(List<XMLEvent> events) {
        Assert.notNull(events, "XMLEvent List must not be null");
        this.events = new ArrayList(events);
    }

    public boolean hasNext() {
        return this.cursor < this.events.size();
    }

    public XMLEvent nextEvent() {
        if (hasNext()) {
            this.currentEvent = this.events.get(this.cursor);
            this.cursor++;
            return this.currentEvent;
        }
        throw new NoSuchElementException();
    }

    @Nullable
    public XMLEvent peek() {
        if (hasNext()) {
            return this.events.get(this.cursor);
        }
        return null;
    }

    public String getElementText() throws XMLStreamException {
        checkIfClosed();
        if (this.currentEvent == null || !this.currentEvent.isStartElement()) {
            throw new XMLStreamException("Not at START_ELEMENT: " + this.currentEvent);
        }
        StringBuilder builder = new StringBuilder();
        while (true) {
            XMLEvent event = nextEvent();
            if (!event.isEndElement()) {
                if (!event.isCharacters()) {
                    throw new XMLStreamException("Unexpected non-text event: " + event);
                }
                Characters characters = event.asCharacters();
                if (!characters.isIgnorableWhiteSpace()) {
                    builder.append(event.asCharacters().getData());
                }
            } else {
                return builder.toString();
            }
        }
    }

    @Nullable
    public XMLEvent nextTag() throws XMLStreamException {
        checkIfClosed();
        while (true) {
            XMLEvent event = nextEvent();
            switch (event.getEventType()) {
                case 1:
                case 2:
                    return event;
                case 3:
                case 5:
                case 6:
                    break;
                case 4:
                case 12:
                    if (!event.asCharacters().isWhiteSpace()) {
                        throw new XMLStreamException("Non-ignorable whitespace CDATA or CHARACTERS event: " + event);
                    }
                    break;
                case 7:
                case 9:
                case 10:
                case 11:
                default:
                    throw new XMLStreamException("Expected START_ELEMENT or END_ELEMENT: " + event);
                case 8:
                    return null;
            }
        }
    }

    @Override // org.springframework.util.xml.AbstractXMLEventReader
    public void close() {
        super.close();
        this.events.clear();
    }
}
