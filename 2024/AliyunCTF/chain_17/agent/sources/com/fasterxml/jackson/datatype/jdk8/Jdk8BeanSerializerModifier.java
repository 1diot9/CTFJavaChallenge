package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8BeanSerializerModifier.class */
public class Jdk8BeanSerializerModifier extends BeanSerializerModifier {
    @Override // com.fasterxml.jackson.databind.ser.BeanSerializerModifier
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        Object empty;
        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);
            JavaType type = writer.getType();
            if (type.isTypeOrSubTypeOf(Optional.class)) {
                empty = Optional.empty();
            } else if (type.hasRawClass(OptionalLong.class)) {
                empty = OptionalLong.empty();
            } else if (type.hasRawClass(OptionalInt.class)) {
                empty = OptionalInt.empty();
            } else if (type.hasRawClass(OptionalDouble.class)) {
                empty = OptionalDouble.empty();
            }
            beanProperties.set(i, new Jdk8OptionalBeanPropertyWriter(writer, empty));
        }
        return beanProperties;
    }
}
