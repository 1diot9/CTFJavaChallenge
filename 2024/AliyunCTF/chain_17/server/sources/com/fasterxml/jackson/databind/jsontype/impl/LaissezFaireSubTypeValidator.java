package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/jsontype/impl/LaissezFaireSubTypeValidator.class */
public final class LaissezFaireSubTypeValidator extends PolymorphicTypeValidator.Base {
    private static final long serialVersionUID = 1;
    public static final LaissezFaireSubTypeValidator instance = new LaissezFaireSubTypeValidator();

    @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Base, com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
    public PolymorphicTypeValidator.Validity validateBaseType(MapperConfig<?> ctxt, JavaType baseType) {
        return PolymorphicTypeValidator.Validity.INDETERMINATE;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Base, com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
    public PolymorphicTypeValidator.Validity validateSubClassName(MapperConfig<?> ctxt, JavaType baseType, String subClassName) {
        return PolymorphicTypeValidator.Validity.ALLOWED;
    }

    @Override // com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Base, com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
    public PolymorphicTypeValidator.Validity validateSubType(MapperConfig<?> ctxt, JavaType baseType, JavaType subType) {
        return PolymorphicTypeValidator.Validity.ALLOWED;
    }
}
