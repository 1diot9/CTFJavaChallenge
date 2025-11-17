package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Parameter;

/* loaded from: server.jar:BOOT-INF/lib/jackson-module-parameter-names-2.15.3.jar:com/fasterxml/jackson/module/paramnames/ParameterNamesAnnotationIntrospector.class */
public class ParameterNamesAnnotationIntrospector extends NopAnnotationIntrospector {
    private static final long serialVersionUID = 1;
    private final JsonCreator.Mode creatorBinding;
    private final ParameterExtractor parameterExtractor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ParameterNamesAnnotationIntrospector(JsonCreator.Mode creatorBinding, ParameterExtractor parameterExtractor) {
        this.creatorBinding = creatorBinding;
        this.parameterExtractor = parameterExtractor;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public String findImplicitPropertyName(AnnotatedMember m) {
        if (m instanceof AnnotatedParameter) {
            return findParameterName((AnnotatedParameter) m);
        }
        return null;
    }

    private String findParameterName(AnnotatedParameter annotatedParameter) {
        try {
            Parameter[] params = getParameters(annotatedParameter.getOwner());
            Parameter p = params[annotatedParameter.getIndex()];
            if (p.isNamePresent()) {
                return p.getName();
            }
            return null;
        } catch (MalformedParametersException e) {
            return null;
        }
    }

    private Parameter[] getParameters(AnnotatedWithParams owner) {
        if (owner instanceof AnnotatedConstructor) {
            return this.parameterExtractor.getParameters(((AnnotatedConstructor) owner).getAnnotated());
        }
        if (owner instanceof AnnotatedMethod) {
            return this.parameterExtractor.getParameters(((AnnotatedMethod) owner).getAnnotated());
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.AnnotationIntrospector
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        JsonCreator ann = (JsonCreator) _findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            JsonCreator.Mode mode = ann.mode();
            if (this.creatorBinding != null && mode == JsonCreator.Mode.DEFAULT) {
                mode = this.creatorBinding;
            }
            return mode;
        }
        return null;
    }
}
