package com.fasterxml.jackson.databind.node;

import com.fasterxml.jackson.databind.util.RawValue;
import java.math.BigDecimal;
import java.math.BigInteger;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/node/JsonNodeCreator.class */
public interface JsonNodeCreator {
    ValueNode booleanNode(boolean z);

    ValueNode nullNode();

    ValueNode numberNode(byte b);

    ValueNode numberNode(Byte b);

    ValueNode numberNode(short s);

    ValueNode numberNode(Short sh);

    ValueNode numberNode(int i);

    ValueNode numberNode(Integer num);

    ValueNode numberNode(long j);

    ValueNode numberNode(Long l);

    ValueNode numberNode(BigInteger bigInteger);

    ValueNode numberNode(float f);

    ValueNode numberNode(Float f);

    ValueNode numberNode(double d);

    ValueNode numberNode(Double d);

    ValueNode numberNode(BigDecimal bigDecimal);

    ValueNode textNode(String str);

    ValueNode binaryNode(byte[] bArr);

    ValueNode binaryNode(byte[] bArr, int i, int i2);

    ValueNode pojoNode(Object obj);

    ValueNode rawValueNode(RawValue rawValue);

    ArrayNode arrayNode();

    ArrayNode arrayNode(int i);

    ObjectNode objectNode();
}
