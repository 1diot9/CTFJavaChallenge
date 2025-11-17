package org.jooq.impl;

import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.beans.Introspector;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.namespace.QName;
import org.jooq.Converter;
import org.jooq.ConverterContext;
import org.jooq.XML;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractXMLasObjectBinding.class */
public class AbstractXMLasObjectBinding<T> extends AbstractXMLBinding<T> {
    private final Converter<XML, T> converter;

    protected AbstractXMLasObjectBinding(Class<T> theType) {
        this.converter = new XMLasObjectConverter(theType);
    }

    @Override // org.jooq.Binding
    public final Converter<XML, T> converter() {
        return this.converter;
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/AbstractXMLasObjectBinding$XMLasObjectConverter.class */
    private static final class XMLasObjectConverter<T> extends AbstractContextConverter<XML, T> {
        XmlRootElement root;
        transient JAXBContext ctx;

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.jooq.ContextConverter
        public /* bridge */ /* synthetic */ Object to(Object obj, ConverterContext converterContext) {
            return to((XMLasObjectConverter<T>) obj, converterContext);
        }

        private XMLasObjectConverter(Class<T> type) {
            super(XML.class, type);
            this.root = type.getAnnotation(XmlRootElement.class);
            this.ctx = initCtx();
        }

        private final JAXBContext initCtx() {
            try {
                return JAXBContext.newInstance(new Class[]{toType()});
            } catch (JAXBException e) {
                throw new DataBindingException(e);
            }
        }

        @Override // org.jooq.ContextConverter
        public T from(XML xml, ConverterContext converterContext) {
            if (xml == null) {
                return null;
            }
            return (T) JAXB.unmarshal(new StringReader(String.valueOf(xml)), toType());
        }

        @Override // org.jooq.ContextConverter
        public XML to(T u, ConverterContext scope) {
            if (u == null) {
                return null;
            }
            try {
                StringWriter s = new StringWriter();
                Object o = u;
                if (this.root == null) {
                    o = new JAXBElement(new QName(Introspector.decapitalize(toType().getSimpleName())), toType(), u);
                }
                Marshaller m = this.ctx.createMarshaller();
                m.setProperty("jaxb.fragment", true);
                m.marshal(o, s);
                return XML.xml(s.toString());
            } catch (JAXBException e) {
                throw new DataBindingException(e);
            }
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();
            this.ctx = initCtx();
        }
    }
}
