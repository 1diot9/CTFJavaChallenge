package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import java.io.IOException;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/util/MinimalPrettyPrinter.class */
public class MinimalPrettyPrinter implements PrettyPrinter, Serializable {
    private static final long serialVersionUID = 1;
    protected String _rootValueSeparator;
    protected Separators _separators;

    public MinimalPrettyPrinter() {
        this(DEFAULT_ROOT_VALUE_SEPARATOR.toString());
    }

    public MinimalPrettyPrinter(String rootValueSeparator) {
        this._rootValueSeparator = rootValueSeparator;
        this._separators = DEFAULT_SEPARATORS;
    }

    public void setRootValueSeparator(String sep) {
        this._rootValueSeparator = sep;
    }

    public MinimalPrettyPrinter setSeparators(Separators separators) {
        this._separators = separators;
        return this;
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeRootValueSeparator(JsonGenerator g) throws IOException {
        if (this._rootValueSeparator != null) {
            g.writeRaw(this._rootValueSeparator);
        }
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeStartObject(JsonGenerator g) throws IOException {
        g.writeRaw('{');
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void beforeObjectEntries(JsonGenerator g) throws IOException {
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._separators.getObjectFieldValueSeparator());
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._separators.getObjectEntrySeparator());
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
        g.writeRaw('}');
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeStartArray(JsonGenerator g) throws IOException {
        g.writeRaw('[');
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void beforeArrayValues(JsonGenerator g) throws IOException {
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._separators.getArrayValueSeparator());
    }

    @Override // com.fasterxml.jackson.core.PrettyPrinter
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
        g.writeRaw(']');
    }
}
