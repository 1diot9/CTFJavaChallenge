package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.w3c.dom.Node;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/DOMSerializer.class */
public class DOMSerializer extends StdSerializer<Node> {
    protected final TransformerFactory transformerFactory;

    public DOMSerializer() {
        super(Node.class);
        try {
            this.transformerFactory = TransformerFactory.newInstance();
            this.transformerFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            setTransformerFactoryAttribute(this.transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalDTD", "");
            setTransformerFactoryAttribute(this.transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalStylesheet", "");
        } catch (Exception e) {
            throw new IllegalStateException("Could not instantiate `TransformerFactory`: " + e.getMessage(), e);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Node value, JsonGenerator g, SerializerProvider provider) throws IOException {
        try {
            Transformer transformer = this.transformerFactory.newTransformer();
            transformer.setOutputProperty("omit-xml-declaration", CustomBooleanEditor.VALUE_YES);
            transformer.setOutputProperty("indent", "no");
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(new DOMSource(value), result);
            g.writeString(result.getWriter().toString());
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Could not create XML Transformer for writing DOM `Node` value: " + e.getMessage(), e);
        } catch (TransformerException e2) {
            provider.reportMappingProblem(e2, "DOM `Node` value serialization failed: %s", e2.getMessage());
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("string", true);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        if (visitor != null) {
            visitor.expectAnyFormat(typeHint);
        }
    }

    private static void setTransformerFactoryAttribute(TransformerFactory transformerFactory, String name, Object value) {
        try {
            transformerFactory.setAttribute(name, value);
        } catch (Exception e) {
            System.err.println("[DOMSerializer] Failed to set TransformerFactory attribute: " + name);
        }
    }
}
