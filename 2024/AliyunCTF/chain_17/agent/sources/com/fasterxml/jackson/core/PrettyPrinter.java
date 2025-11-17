package com.fasterxml.jackson.core;

import cn.hutool.core.text.CharSequenceUtil;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.util.Separators;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-core-2.15.3.jar:com/fasterxml/jackson/core/PrettyPrinter.class */
public interface PrettyPrinter {
    public static final Separators DEFAULT_SEPARATORS = Separators.createDefaultInstance();
    public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(CharSequenceUtil.SPACE);

    void writeRootValueSeparator(JsonGenerator jsonGenerator) throws IOException;

    void writeStartObject(JsonGenerator jsonGenerator) throws IOException;

    void writeEndObject(JsonGenerator jsonGenerator, int i) throws IOException;

    void writeObjectEntrySeparator(JsonGenerator jsonGenerator) throws IOException;

    void writeObjectFieldValueSeparator(JsonGenerator jsonGenerator) throws IOException;

    void writeStartArray(JsonGenerator jsonGenerator) throws IOException;

    void writeEndArray(JsonGenerator jsonGenerator, int i) throws IOException;

    void writeArrayValueSeparator(JsonGenerator jsonGenerator) throws IOException;

    void beforeArrayValues(JsonGenerator jsonGenerator) throws IOException;

    void beforeObjectEntries(JsonGenerator jsonGenerator) throws IOException;
}
