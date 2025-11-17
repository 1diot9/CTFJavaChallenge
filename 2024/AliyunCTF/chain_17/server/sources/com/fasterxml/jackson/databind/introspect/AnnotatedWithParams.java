package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.JavaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotatedWithParams.class */
public abstract class AnnotatedWithParams extends AnnotatedMember {
    private static final long serialVersionUID = 1;
    protected final AnnotationMap[] _paramAnnotations;

    public abstract int getParameterCount();

    public abstract Class<?> getRawParameterType(int i);

    public abstract JavaType getParameterType(int i);

    @Deprecated
    public abstract Type getGenericParameterType(int i);

    public abstract Object call() throws Exception;

    public abstract Object call(Object[] objArr) throws Exception;

    public abstract Object call1(Object obj) throws Exception;

    /* JADX INFO: Access modifiers changed from: protected */
    public AnnotatedWithParams(TypeResolutionContext ctxt, AnnotationMap annotations, AnnotationMap[] paramAnnotations) {
        super(ctxt, annotations);
        this._paramAnnotations = paramAnnotations;
    }

    protected AnnotatedWithParams(AnnotatedWithParams base, AnnotationMap[] paramAnnotations) {
        super(base);
        this._paramAnnotations = paramAnnotations;
    }

    public final void addOrOverrideParam(int paramIndex, Annotation a) {
        AnnotationMap old = this._paramAnnotations[paramIndex];
        if (old == null) {
            old = new AnnotationMap();
            this._paramAnnotations[paramIndex] = old;
        }
        old.add(a);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AnnotatedParameter replaceParameterAnnotations(int index, AnnotationMap ann) {
        this._paramAnnotations[index] = ann;
        return getParameter(index);
    }

    public final AnnotationMap getParameterAnnotations(int index) {
        if (this._paramAnnotations != null && index >= 0 && index < this._paramAnnotations.length) {
            return this._paramAnnotations[index];
        }
        return null;
    }

    public final AnnotatedParameter getParameter(int index) {
        return new AnnotatedParameter(this, getParameterType(index), this._typeContext, getParameterAnnotations(index), index);
    }

    public final int getAnnotationCount() {
        return this._annotations.size();
    }
}
