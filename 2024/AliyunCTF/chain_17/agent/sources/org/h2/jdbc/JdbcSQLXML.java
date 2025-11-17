package org.h2.jdbc;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.h2.jdbc.JdbcLob;
import org.h2.message.DbException;
import org.h2.value.Value;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: agent.jar:BOOT-INF/lib/h2-2.2.224.jar:org/h2/jdbc/JdbcSQLXML.class */
public final class JdbcSQLXML extends JdbcLob implements SQLXML {
    private static final Map<String, Boolean> secureFeatureMap = new HashMap();
    private static final EntityResolver NOOP_ENTITY_RESOLVER = (str, str2) -> {
        return new InputSource(new StringReader(""));
    };
    private static final URIResolver NOOP_URI_RESOLVER = (str, str2) -> {
        return new StreamSource(new StringReader(""));
    };
    private DOMResult domResult;
    private Closeable closable;

    static {
        secureFeatureMap.put("http://javax.xml.XMLConstants/feature/secure-processing", true);
        secureFeatureMap.put("http://apache.org/xml/features/disallow-doctype-decl", true);
        secureFeatureMap.put("http://xml.org/sax/features/external-general-entities", false);
        secureFeatureMap.put("http://xml.org/sax/features/external-parameter-entities", false);
        secureFeatureMap.put("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    }

    public JdbcSQLXML(JdbcConnection jdbcConnection, Value value, JdbcLob.State state, int i) {
        super(jdbcConnection, value, state, 17, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.h2.jdbc.JdbcLob
    public void checkReadable() throws SQLException, IOException {
        checkClosed();
        if (this.state == JdbcLob.State.SET_CALLED) {
            if (this.domResult != null) {
                Node node = this.domResult.getNode();
                this.domResult = null;
                try {
                    Transformer newTransformer = TransformerFactory.newInstance().newTransformer();
                    DOMSource dOMSource = new DOMSource(node);
                    StringWriter stringWriter = new StringWriter();
                    newTransformer.transform(dOMSource, new StreamResult(stringWriter));
                    completeWrite(this.conn.createClob(new StringReader(stringWriter.toString()), -1L));
                    return;
                } catch (Exception e) {
                    throw logAndConvert(e);
                }
            }
            if (this.closable != null) {
                this.closable.close();
                this.closable = null;
                return;
            }
            throw DbException.getUnsupportedException("Stream setter is not yet closed.");
        }
    }

    @Override // org.h2.jdbc.JdbcLob, java.sql.Blob
    public InputStream getBinaryStream() throws SQLException {
        return super.getBinaryStream();
    }

    @Override // org.h2.jdbc.JdbcLob, java.sql.Clob
    public Reader getCharacterStream() throws SQLException {
        return super.getCharacterStream();
    }

    @Override // java.sql.SQLXML
    public <T extends Source> T getSource(Class<T> cls) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("getSource(" + (cls != null ? cls.getSimpleName() + ".class" : "null") + ')');
            }
            checkReadable();
            if (cls == null || cls == DOMSource.class) {
                DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
                for (Map.Entry<String, Boolean> entry : secureFeatureMap.entrySet()) {
                    try {
                        newInstance.setFeature(entry.getKey(), entry.getValue().booleanValue());
                    } catch (Exception e) {
                    }
                }
                newInstance.setXIncludeAware(false);
                newInstance.setExpandEntityReferences(false);
                newInstance.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
                DocumentBuilder newDocumentBuilder = newInstance.newDocumentBuilder();
                newDocumentBuilder.setEntityResolver(NOOP_ENTITY_RESOLVER);
                return new DOMSource(newDocumentBuilder.parse(new InputSource(this.value.getInputStream())));
            }
            if (cls == SAXSource.class) {
                SAXParserFactory newInstance2 = SAXParserFactory.newInstance();
                for (Map.Entry<String, Boolean> entry2 : secureFeatureMap.entrySet()) {
                    try {
                        newInstance2.setFeature(entry2.getKey(), entry2.getValue().booleanValue());
                    } catch (Exception e2) {
                    }
                }
                XMLReader xMLReader = newInstance2.newSAXParser().getXMLReader();
                xMLReader.setEntityResolver(NOOP_ENTITY_RESOLVER);
                return new SAXSource(xMLReader, new InputSource(this.value.getInputStream()));
            }
            if (cls == StAXSource.class) {
                XMLInputFactory newInstance3 = XMLInputFactory.newInstance();
                newInstance3.setProperty("javax.xml.stream.supportDTD", false);
                newInstance3.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                newInstance3.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
                return new StAXSource(newInstance3.createXMLStreamReader(this.value.getInputStream()));
            }
            if (cls == StreamSource.class) {
                TransformerFactory newInstance4 = TransformerFactory.newInstance();
                newInstance4.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                newInstance4.setAttribute("http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
                newInstance4.setURIResolver(NOOP_URI_RESOLVER);
                newInstance4.newTransformer().transform(new StreamSource(this.value.getInputStream()), new SAXResult(new DefaultHandler()));
                return new StreamSource(this.value.getInputStream());
            }
            throw unsupported(cls.getName());
        } catch (Exception e3) {
            throw logAndConvert(e3);
        }
    }

    @Override // java.sql.SQLXML
    public String getString() throws SQLException {
        try {
            debugCodeCall("getString");
            checkReadable();
            return this.value.getString();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.SQLXML
    public OutputStream setBinaryStream() throws SQLException {
        try {
            debugCodeCall("setBinaryStream");
            checkEditable();
            this.state = JdbcLob.State.SET_CALLED;
            return new BufferedOutputStream(setClobOutputStreamImpl());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.SQLXML
    public Writer setCharacterStream() throws SQLException {
        try {
            debugCodeCall("setCharacterStream");
            checkEditable();
            this.state = JdbcLob.State.SET_CALLED;
            return setCharacterStreamImpl();
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.SQLXML
    public <T extends Result> T setResult(Class<T> cls) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode("setResult(" + (cls != null ? cls.getSimpleName() + ".class" : "null") + ')');
            }
            checkEditable();
            if (cls == null || cls == DOMResult.class) {
                this.domResult = new DOMResult();
                this.state = JdbcLob.State.SET_CALLED;
                return this.domResult;
            }
            if (cls == SAXResult.class) {
                TransformerHandler newTransformerHandler = ((SAXTransformerFactory) TransformerFactory.newInstance()).newTransformerHandler();
                Writer characterStreamImpl = setCharacterStreamImpl();
                newTransformerHandler.setResult(new StreamResult(characterStreamImpl));
                SAXResult sAXResult = new SAXResult(newTransformerHandler);
                this.closable = characterStreamImpl;
                this.state = JdbcLob.State.SET_CALLED;
                return sAXResult;
            }
            if (cls == StAXResult.class) {
                XMLOutputFactory newInstance = XMLOutputFactory.newInstance();
                Writer characterStreamImpl2 = setCharacterStreamImpl();
                StAXResult stAXResult = new StAXResult(newInstance.createXMLStreamWriter(characterStreamImpl2));
                this.closable = characterStreamImpl2;
                this.state = JdbcLob.State.SET_CALLED;
                return stAXResult;
            }
            if (StreamResult.class.equals(cls)) {
                Writer characterStreamImpl3 = setCharacterStreamImpl();
                StreamResult streamResult = new StreamResult(characterStreamImpl3);
                this.closable = characterStreamImpl3;
                this.state = JdbcLob.State.SET_CALLED;
                return streamResult;
            }
            throw unsupported(cls.getName());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }

    @Override // java.sql.SQLXML
    public void setString(String str) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCodeCall("getSource", str);
            }
            checkEditable();
            completeWrite(this.conn.createClob(new StringReader(str), -1L));
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }
}
