package org.springframework.util.xml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import org.springframework.lang.Nullable;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/util/xml/StaxSource.class */
class StaxSource extends SAXSource {

    @Nullable
    private XMLEventReader eventReader;

    @Nullable
    private XMLStreamReader streamReader;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StaxSource(XMLEventReader eventReader) {
        super(new StaxEventXMLReader(eventReader), new InputSource());
        this.eventReader = eventReader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StaxSource(XMLStreamReader streamReader) {
        super(new StaxStreamXMLReader(streamReader), new InputSource());
        this.streamReader = streamReader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public XMLEventReader getXMLEventReader() {
        return this.eventReader;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public XMLStreamReader getXMLStreamReader() {
        return this.streamReader;
    }

    @Override // javax.xml.transform.sax.SAXSource
    public void setInputSource(InputSource inputSource) {
        throw new UnsupportedOperationException("setInputSource is not supported");
    }

    @Override // javax.xml.transform.sax.SAXSource
    public void setXMLReader(XMLReader reader) {
        throw new UnsupportedOperationException("setXMLReader is not supported");
    }
}
