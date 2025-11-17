package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import java.io.Serializable;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8Deserializers.class */
public class Jdk8Deserializers extends Deserializers.Base implements Serializable {
    private static final long serialVersionUID = 1;
    protected final boolean _cfgReadAbsentAsNull;

    public Jdk8Deserializers() {
        this(false);
    }

    public Jdk8Deserializers(boolean cfgReadAbsentAsNull) {
        this._cfgReadAbsentAsNull = cfgReadAbsentAsNull;
    }

    @Override // com.fasterxml.jackson.databind.deser.Deserializers.Base, com.fasterxml.jackson.databind.deser.Deserializers
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) {
        if (refType.hasRawClass(Optional.class)) {
            return new OptionalDeserializer(refType, null, contentTypeDeserializer, contentDeserializer, this._cfgReadAbsentAsNull);
        }
        if (refType.hasRawClass(OptionalInt.class)) {
            return OptionalIntDeserializer.INSTANCE;
        }
        if (refType.hasRawClass(OptionalLong.class)) {
            return OptionalLongDeserializer.INSTANCE;
        }
        if (refType.hasRawClass(OptionalDouble.class)) {
            return OptionalDoubleDeserializer.INSTANCE;
        }
        return null;
    }
}
