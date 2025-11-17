package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/* loaded from: server.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8TypeModifier.class */
public class Jdk8TypeModifier extends TypeModifier implements Serializable {
    private static final long serialVersionUID = 1;

    @Override // com.fasterxml.jackson.databind.type.TypeModifier
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory) {
        JavaType refType;
        if (type.isReferenceType() || type.isContainerType()) {
            return type;
        }
        Class<?> raw = type.getRawClass();
        if (raw == Optional.class) {
            refType = type.containedTypeOrUnknown(0);
        } else if (raw == OptionalInt.class) {
            refType = typeFactory.constructType(Integer.TYPE);
        } else if (raw == OptionalLong.class) {
            refType = typeFactory.constructType(Long.TYPE);
        } else if (raw == OptionalDouble.class) {
            refType = typeFactory.constructType(Double.TYPE);
        } else {
            return type;
        }
        return ReferenceType.upgradeFrom(type, refType);
    }
}
