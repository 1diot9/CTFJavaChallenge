package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/UUIDSerializer.class */
public class UUIDSerializer extends StdScalarSerializer<UUID> implements ContextualSerializer {
    static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    protected final Boolean _asBinary;

    public UUIDSerializer() {
        this(null);
    }

    protected UUIDSerializer(Boolean asBinary) {
        super(UUID.class);
        this._asBinary = asBinary;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider prov, UUID value) {
        if (value.getLeastSignificantBits() == 0 && value.getMostSignificantBits() == 0) {
            return true;
        }
        return false;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider serializers, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value format = findFormatOverrides(serializers, property, handledType());
        Boolean asBinary = null;
        if (format != null) {
            JsonFormat.Shape shape = format.getShape();
            if (shape == JsonFormat.Shape.BINARY) {
                asBinary = true;
            } else if (shape == JsonFormat.Shape.STRING) {
                asBinary = false;
            }
        }
        if (!Objects.equals(asBinary, this._asBinary)) {
            return new UUIDSerializer(asBinary);
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(UUID value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (_writeAsBinary(gen)) {
            gen.writeBinary(_asBytes(value));
            return;
        }
        char[] ch2 = new char[36];
        long msb = value.getMostSignificantBits();
        _appendInt((int) (msb >> 32), ch2, 0);
        ch2[8] = '-';
        int i = (int) msb;
        _appendShort(i >>> 16, ch2, 9);
        ch2[13] = '-';
        _appendShort(i, ch2, 14);
        ch2[18] = '-';
        long lsb = value.getLeastSignificantBits();
        _appendShort((int) (lsb >>> 48), ch2, 19);
        ch2[23] = '-';
        _appendShort((int) (lsb >>> 32), ch2, 24);
        _appendInt((int) lsb, ch2, 28);
        gen.writeString(ch2, 0, 36);
    }

    protected boolean _writeAsBinary(JsonGenerator g) {
        if (this._asBinary != null) {
            return this._asBinary.booleanValue();
        }
        return !(g instanceof TokenBuffer) && g.canWriteBinaryNatively();
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdScalarSerializer, com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        visitStringFormat(visitor, typeHint, JsonValueFormat.UUID);
    }

    private static void _appendInt(int bits, char[] ch2, int offset) {
        _appendShort(bits >> 16, ch2, offset);
        _appendShort(bits, ch2, offset + 4);
    }

    private static void _appendShort(int bits, char[] ch2, int offset) {
        ch2[offset] = HEX_CHARS[(bits >> 12) & 15];
        int offset2 = offset + 1;
        ch2[offset2] = HEX_CHARS[(bits >> 8) & 15];
        int offset3 = offset2 + 1;
        ch2[offset3] = HEX_CHARS[(bits >> 4) & 15];
        ch2[offset3 + 1] = HEX_CHARS[bits & 15];
    }

    private static final byte[] _asBytes(UUID uuid) {
        byte[] buffer = new byte[16];
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();
        _appendInt((int) (hi >> 32), buffer, 0);
        _appendInt((int) hi, buffer, 4);
        _appendInt((int) (lo >> 32), buffer, 8);
        _appendInt((int) lo, buffer, 12);
        return buffer;
    }

    private static final void _appendInt(int value, byte[] buffer, int offset) {
        buffer[offset] = (byte) (value >> 24);
        int offset2 = offset + 1;
        buffer[offset2] = (byte) (value >> 16);
        int offset3 = offset2 + 1;
        buffer[offset3] = (byte) (value >> 8);
        buffer[offset3 + 1] = (byte) value;
    }
}
