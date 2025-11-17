package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;

@JacksonStdImpl
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/SqlDateSerializer.class */
public class SqlDateSerializer extends DateTimeSerializerBase<Date> {
    public SqlDateSerializer() {
        this(null, null);
    }

    protected SqlDateSerializer(Boolean useTimestamp, DateFormat customFormat) {
        super(Date.class, useTimestamp, customFormat);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase
    /* renamed from: withFormat, reason: merged with bridge method [inline-methods] */
    public DateTimeSerializerBase<Date> withFormat2(Boolean timestamp, DateFormat customFormat) {
        return new SqlDateSerializer(timestamp, customFormat);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase
    public long _timestamp(Date value) {
        if (value == null) {
            return 0L;
        }
        return value.getTime();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Date value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (_asTimestamp(provider)) {
            g.writeNumber(_timestamp(value));
        } else if (this._customFormat == null) {
            g.writeString(value.toString());
        } else {
            _serializeAsString(value, g, provider);
        }
    }
}
