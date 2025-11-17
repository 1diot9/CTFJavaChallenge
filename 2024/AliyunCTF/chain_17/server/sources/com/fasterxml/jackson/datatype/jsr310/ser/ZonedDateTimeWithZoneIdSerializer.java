package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Deprecated
/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/ZonedDateTimeWithZoneIdSerializer.class */
public class ZonedDateTimeWithZoneIdSerializer extends InstantSerializerBase<ZonedDateTime> {
    private static final long serialVersionUID = 1;
    public static final ZonedDateTimeWithZoneIdSerializer INSTANCE = new ZonedDateTimeWithZoneIdSerializer();

    protected ZonedDateTimeWithZoneIdSerializer() {
        super(ZonedDateTime.class, dt -> {
            return dt.toInstant().toEpochMilli();
        }, (v0) -> {
            return v0.toEpochSecond();
        }, (v0) -> {
            return v0.getNano();
        }, (DateTimeFormatter) null);
    }

    protected ZonedDateTimeWithZoneIdSerializer(ZonedDateTimeWithZoneIdSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
        this(base, useTimestamp, null, formatter);
    }

    protected ZonedDateTimeWithZoneIdSerializer(ZonedDateTimeWithZoneIdSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase, com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new ZonedDateTimeWithZoneIdSerializer(this, useTimestamp, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new ZonedDateTimeWithZoneIdSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter);
    }
}
