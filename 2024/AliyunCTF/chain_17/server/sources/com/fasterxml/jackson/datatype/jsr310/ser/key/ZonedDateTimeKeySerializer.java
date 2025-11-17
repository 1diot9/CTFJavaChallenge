package com.fasterxml.jackson.datatype.jsr310.ser.key;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.15.3.jar:com/fasterxml/jackson/datatype/jsr310/ser/key/ZonedDateTimeKeySerializer.class */
public class ZonedDateTimeKeySerializer extends JsonSerializer<ZonedDateTime> {
    public static final ZonedDateTimeKeySerializer INSTANCE = new ZonedDateTimeKeySerializer();

    private ZonedDateTimeKeySerializer() {
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (serializers.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)) {
            gen.writeFieldName(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value));
            return;
        }
        if (useTimestamps(serializers)) {
            if (useNanos(serializers)) {
                gen.writeFieldName(DecimalUtils.toBigDecimal(value.toEpochSecond(), value.getNano()).toString());
                return;
            } else {
                gen.writeFieldName(String.valueOf(value.toInstant().toEpochMilli()));
                return;
            }
        }
        gen.writeFieldName(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
    }

    private static boolean useNanos(SerializerProvider serializers) {
        return serializers.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    private static boolean useTimestamps(SerializerProvider serializers) {
        return serializers.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
    }
}
