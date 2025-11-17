package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.io.ContentReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/JsonLocationInstantiator.class */
public class JsonLocationInstantiator extends ValueInstantiator.Base {
    private static final long serialVersionUID = 1;

    public JsonLocationInstantiator() {
        super((Class<?>) JsonLocation.class);
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
    public boolean canCreateFromObjectWith() {
        return true;
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
        JavaType intType = config.constructType(Integer.TYPE);
        JavaType longType = config.constructType(Long.TYPE);
        return new SettableBeanProperty[]{creatorProp("sourceRef", config.constructType(Object.class), 0), creatorProp("byteOffset", longType, 1), creatorProp("charOffset", longType, 2), creatorProp("lineNr", intType, 3), creatorProp("columnNr", intType, 4)};
    }

    private static CreatorProperty creatorProp(String name, JavaType type, int index) {
        return CreatorProperty.construct(PropertyName.construct(name), type, null, null, null, null, index, null, PropertyMetadata.STD_REQUIRED);
    }

    @Override // com.fasterxml.jackson.databind.deser.ValueInstantiator
    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
        ContentReference srcRef = ContentReference.rawReference(args[0]);
        return new JsonLocation(srcRef, _long(args[1]), _long(args[2]), _int(args[3]), _int(args[4]));
    }

    private static final long _long(Object o) {
        if (o == null) {
            return 0L;
        }
        return ((Number) o).longValue();
    }

    private static final int _int(Object o) {
        if (o == null) {
            return 0;
        }
        return ((Number) o).intValue();
    }
}
