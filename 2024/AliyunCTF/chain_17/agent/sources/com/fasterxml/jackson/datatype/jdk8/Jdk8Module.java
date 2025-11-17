package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.15.3.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8Module.class */
public class Jdk8Module extends Module {
    public static final boolean DEFAULT_READ_ABSENT_AS_NULL = false;
    protected boolean _cfgWriteAbsentAsNull = false;
    protected boolean _cfgReadAbsentAsNull = false;

    @Override // com.fasterxml.jackson.databind.Module
    public void setupModule(Module.SetupContext context) {
        context.addSerializers(new Jdk8Serializers());
        context.addDeserializers(new Jdk8Deserializers(this._cfgReadAbsentAsNull));
        context.addTypeModifier(new Jdk8TypeModifier());
        if (this._cfgWriteAbsentAsNull) {
            context.addBeanSerializerModifier(new Jdk8BeanSerializerModifier());
        }
    }

    @Override // com.fasterxml.jackson.databind.Module, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Deprecated
    public Jdk8Module configureAbsentsAsNulls(boolean state) {
        this._cfgWriteAbsentAsNull = state;
        return this;
    }

    public Jdk8Module configureReadAbsentAsNull(boolean state) {
        this._cfgReadAbsentAsNull = state;
        return this;
    }

    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean equals(Object o) {
        return this == o;
    }

    @Override // com.fasterxml.jackson.databind.Module
    public String getModuleName() {
        return "Jdk8Module";
    }
}
