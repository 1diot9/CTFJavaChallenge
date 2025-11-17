package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

/* loaded from: server.jar:BOOT-INF/lib/jackson-module-parameter-names-2.15.3.jar:com/fasterxml/jackson/module/paramnames/ParameterNamesModule.class */
public class ParameterNamesModule extends SimpleModule {
    private static final long serialVersionUID = 1;
    private final JsonCreator.Mode creatorBinding;

    public ParameterNamesModule(JsonCreator.Mode creatorBinding) {
        super(PackageVersion.VERSION);
        this.creatorBinding = creatorBinding;
    }

    public ParameterNamesModule() {
        super(PackageVersion.VERSION);
        this.creatorBinding = null;
    }

    @Override // com.fasterxml.jackson.databind.module.SimpleModule, com.fasterxml.jackson.databind.Module
    public void setupModule(Module.SetupContext context) {
        super.setupModule(context);
        context.insertAnnotationIntrospector(new ParameterNamesAnnotationIntrospector(this.creatorBinding, new ParameterExtractor()));
    }

    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean equals(Object o) {
        return this == o;
    }
}
