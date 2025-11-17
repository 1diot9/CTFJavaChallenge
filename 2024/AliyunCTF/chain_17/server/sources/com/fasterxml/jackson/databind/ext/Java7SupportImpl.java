package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import java.beans.ConstructorProperties;
import java.beans.Transient;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ext/Java7SupportImpl.class */
public class Java7SupportImpl extends Java7Support {
    private final Class<?> _bogus = ConstructorProperties.class;

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public Boolean findTransient(Annotated a) {
        Transient t = a.getAnnotation(Transient.class);
        if (t != null) {
            return Boolean.valueOf(t.value());
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public Boolean hasCreatorAnnotation(Annotated a) {
        ConstructorProperties props = a.getAnnotation(ConstructorProperties.class);
        if (props != null) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.ext.Java7Support
    public PropertyName findConstructorName(AnnotatedParameter p) {
        ConstructorProperties props;
        AnnotatedWithParams ctor = p.getOwner();
        if (ctor != null && (props = ctor.getAnnotation(ConstructorProperties.class)) != null) {
            String[] names = props.value();
            int ix = p.getIndex();
            if (ix < names.length) {
                return PropertyName.construct(names[ix]);
            }
            return null;
        }
        return null;
    }
}
