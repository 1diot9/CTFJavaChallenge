package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/ZonedDateTimeSerializer.class */
public class ZonedDateTimeSerializer extends InstantSerializerBase<ZonedDateTime> {
    private static final long serialVersionUID = 1;
    public static final ZonedDateTimeSerializer INSTANCE = new ZonedDateTimeSerializer();
    protected final Boolean _writeZoneId;

    protected ZonedDateTimeSerializer() {
        this(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public ZonedDateTimeSerializer(DateTimeFormatter formatter) {
        super(ZonedDateTime.class, dt -> {
            return dt.toInstant().toEpochMilli();
        }, (v0) -> {
            return v0.toEpochSecond();
        }, (v0) -> {
            return v0.getNano();
        }, formatter);
        this._writeZoneId = null;
    }

    protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter, Boolean writeZoneId) {
        this(base, useTimestamp, base._useNanoseconds, formatter, base._shape, writeZoneId);
    }

    @Deprecated
    protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter, Boolean writeZoneId) {
        this(base, useTimestamp, useNanoseconds, formatter, base._shape, writeZoneId);
    }

    protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter, JsonFormat.Shape shape, Boolean writeZoneId) {
        super(base, useTimestamp, useNanoseconds, formatter, shape);
        this._writeZoneId = writeZoneId;
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase, com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new ZonedDateTimeSerializer(this, useTimestamp, this._useNanoseconds, formatter, shape, this._writeZoneId);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    @Deprecated
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId) {
        return new ZonedDateTimeSerializer(this, this._useTimestamp, this._formatter, writeZoneId);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new ZonedDateTimeSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter, writeZoneId);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider) throws IOException {
        if (!useTimestamp(provider) && shouldWriteWithZoneId(provider)) {
            g.writeString(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value));
        } else {
            super.serialize((ZonedDateTimeSerializer) value, g, provider);
        }
    }

    public boolean shouldWriteWithZoneId(SerializerProvider ctxt) {
        return this._writeZoneId != null ? this._writeZoneId.booleanValue() : ctxt.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase, com.fasterxml.jackson.datatype.jsr310.ser.JSR310SerializerBase
    public JsonToken serializationShape(SerializerProvider provider) {
        if (!useTimestamp(provider) && shouldWriteWithZoneId(provider)) {
            return JsonToken.VALUE_STRING;
        }
        return super.serializationShape(provider);
    }
}
