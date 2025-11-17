package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collections;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/AnnotatedMember.class */
public abstract class AnnotatedMember extends Annotated implements Serializable {
    private static final long serialVersionUID = 1;
    protected final transient TypeResolutionContext _typeContext;
    protected final transient AnnotationMap _annotations;

    public abstract Annotated withAnnotations(AnnotationMap annotationMap);

    public abstract Class<?> getDeclaringClass();

    public abstract Member getMember();

    public abstract void setValue(Object obj, Object obj2) throws UnsupportedOperationException, IllegalArgumentException;

    public abstract Object getValue(Object obj) throws UnsupportedOperationException, IllegalArgumentException;

    /* JADX INFO: Access modifiers changed from: protected */
    public AnnotatedMember(TypeResolutionContext ctxt, AnnotationMap annotations) {
        this._typeContext = ctxt;
        this._annotations = annotations;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AnnotatedMember(AnnotatedMember base) {
        this._typeContext = base._typeContext;
        this._annotations = base._annotations;
    }

    public String getFullName() {
        return getDeclaringClass().getName() + "#" + getName();
    }

    @Deprecated
    public TypeResolutionContext getTypeContext() {
        return this._typeContext;
    }

    @Override // com.fasterxml.jackson.databind.introspect.Annotated
    public final <A extends Annotation> A getAnnotation(Class<A> cls) {
        if (this._annotations == null) {
            return null;
        }
        return (A) this._annotations.get(cls);
    }

    @Override // com.fasterxml.jackson.databind.introspect.Annotated
    public final boolean hasAnnotation(Class<?> acls) {
        if (this._annotations == null) {
            return false;
        }
        return this._annotations.has(acls);
    }

    @Override // com.fasterxml.jackson.databind.introspect.Annotated
    public boolean hasOneOf(Class<? extends Annotation>[] annoClasses) {
        if (this._annotations == null) {
            return false;
        }
        return this._annotations.hasOneOf(annoClasses);
    }

    @Override // com.fasterxml.jackson.databind.introspect.Annotated
    @Deprecated
    public Iterable<Annotation> annotations() {
        if (this._annotations == null) {
            return Collections.emptyList();
        }
        return this._annotations.annotations();
    }

    public AnnotationMap getAllAnnotations() {
        return this._annotations;
    }

    public final void fixAccess(boolean force) {
        Member m = getMember();
        if (m != null) {
            ClassUtil.checkAndFixAccess(m, force);
        }
    }
}
