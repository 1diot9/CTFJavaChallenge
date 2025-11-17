package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.util.OptionalInt;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/OptionalIntDeserializer.class */
public class OptionalIntDeserializer extends BaseScalarOptionalDeserializer<OptionalInt> {
    private static final long serialVersionUID = 1;
    static final OptionalIntDeserializer INSTANCE = new OptionalIntDeserializer();

    public OptionalIntDeserializer() {
        super(OptionalInt.class, OptionalInt.empty());
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Integer;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public OptionalInt deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return OptionalInt.of(p.getIntValue());
        }
        switch (p.currentTokenId()) {
            case 3:
                return _deserializeFromArray(p, ctxt);
            case 4:
            case 5:
            case 7:
            case 9:
            case 10:
            default:
                return (OptionalInt) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            case 6:
                String text = p.getText();
                CoercionAction act = _checkFromStringCoercion(ctxt, text);
                if (act == CoercionAction.AsNull || act == CoercionAction.AsEmpty) {
                    return (OptionalInt) this._empty;
                }
                return OptionalInt.of(_parseIntPrimitive(ctxt, text.trim()));
            case 8:
                CoercionAction act2 = _checkFloatToIntCoercion(p, ctxt, this._valueClass);
                if (act2 == CoercionAction.AsNull || act2 == CoercionAction.AsEmpty) {
                    return (OptionalInt) this._empty;
                }
                return OptionalInt.of(p.getValueAsInt());
            case 11:
                return (OptionalInt) this._empty;
        }
    }
}
