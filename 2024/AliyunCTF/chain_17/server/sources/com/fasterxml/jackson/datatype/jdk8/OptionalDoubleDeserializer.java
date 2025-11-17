package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.util.OptionalDouble;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/OptionalDoubleDeserializer.class */
class OptionalDoubleDeserializer extends BaseScalarOptionalDeserializer<OptionalDouble> {
    private static final long serialVersionUID = 1;
    static final OptionalDoubleDeserializer INSTANCE = new OptionalDoubleDeserializer();

    public OptionalDoubleDeserializer() {
        super(OptionalDouble.class, OptionalDouble.empty());
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Float;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public OptionalDouble deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return OptionalDouble.of(p.getDoubleValue());
        }
        switch (p.currentTokenId()) {
            case 3:
                return _deserializeFromArray(p, ctxt);
            case 4:
            case 5:
            case 8:
            case 9:
            case 10:
            default:
                return (OptionalDouble) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            case 6:
                String text = p.getText();
                Double specialValue = _checkDoubleSpecialValue(text);
                if (specialValue != null) {
                    return OptionalDouble.of(specialValue.doubleValue());
                }
                CoercionAction act = _checkFromStringCoercion(ctxt, text);
                if (act == CoercionAction.AsNull || act == CoercionAction.AsEmpty) {
                    return (OptionalDouble) this._empty;
                }
                return OptionalDouble.of(_parseDoublePrimitive(p, ctxt, text.trim()));
            case 7:
                return OptionalDouble.of(p.getDoubleValue());
            case 11:
                return getNullValue(ctxt);
        }
    }
}
