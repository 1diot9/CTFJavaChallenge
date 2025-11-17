package org.jooq.impl;

import jakarta.xml.bind.JAXB;
import java.io.StringReader;
import java.io.StringWriter;
import org.jooq.XML;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/XMLtoJAXBConverter.class */
public class XMLtoJAXBConverter<U> extends AbstractConverter<XML, U> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.jooq.Converter
    public /* bridge */ /* synthetic */ Object to(Object obj) {
        return to((XMLtoJAXBConverter<U>) obj);
    }

    public XMLtoJAXBConverter(Class<U> toType) {
        super(XML.class, toType);
    }

    @Override // org.jooq.Converter
    public U from(XML xml) {
        if (xml == null) {
            return null;
        }
        return (U) JAXB.unmarshal(new StringReader(xml.data()), toType());
    }

    @Override // org.jooq.Converter
    public XML to(U u) {
        if (u == null) {
            return null;
        }
        StringWriter w = new StringWriter();
        JAXB.marshal(u, w);
        return XML.xml(w.toString());
    }
}
