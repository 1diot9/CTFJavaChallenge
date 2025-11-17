package com.fasterxml.jackson.databind.jsontype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/PolymorphicTypeValidator.class */
public abstract class PolymorphicTypeValidator implements Serializable {
    private static final long serialVersionUID = 1;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/PolymorphicTypeValidator$Validity.class */
    public enum Validity {
        ALLOWED,
        DENIED,
        INDETERMINATE
    }

    public abstract Validity validateBaseType(MapperConfig<?> mapperConfig, JavaType javaType);

    public abstract Validity validateSubClassName(MapperConfig<?> mapperConfig, JavaType javaType, String str) throws JsonMappingException;

    public abstract Validity validateSubType(MapperConfig<?> mapperConfig, JavaType javaType, JavaType javaType2) throws JsonMappingException;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/PolymorphicTypeValidator$Base.class */
    public static abstract class Base extends PolymorphicTypeValidator implements Serializable {
        private static final long serialVersionUID = 1;

        @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
        public Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
            return Validity.INDETERMINATE;
        }

        @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
        public Validity validateSubClassName(MapperConfig<?> config, JavaType baseType, String subClassName) throws JsonMappingException {
            return Validity.INDETERMINATE;
        }

        @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
        public Validity validateSubType(MapperConfig<?> config, JavaType baseType, JavaType subType) throws JsonMappingException {
            return Validity.INDETERMINATE;
        }
    }
}
