package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Named;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/introspect/BeanPropertyDefinition.class */
public abstract class BeanPropertyDefinition implements Named {
    protected static final JsonInclude.Value EMPTY_INCLUDE = JsonInclude.Value.empty();

    public abstract BeanPropertyDefinition withName(PropertyName propertyName);

    public abstract BeanPropertyDefinition withSimpleName(String str);

    @Override // com.fasterxml.jackson.databind.util.Named
    public abstract String getName();

    public abstract PropertyName getFullName();

    public abstract String getInternalName();

    public abstract PropertyName getWrapperName();

    public abstract boolean isExplicitlyIncluded();

    public abstract JavaType getPrimaryType();

    public abstract Class<?> getRawPrimaryType();

    public abstract PropertyMetadata getMetadata();

    public abstract boolean hasGetter();

    public abstract boolean hasSetter();

    public abstract boolean hasField();

    public abstract boolean hasConstructorParameter();

    public abstract AnnotatedMethod getGetter();

    public abstract AnnotatedMethod getSetter();

    public abstract AnnotatedField getField();

    public abstract AnnotatedParameter getConstructorParameter();

    public abstract AnnotatedMember getPrimaryMember();

    public abstract JsonInclude.Value findInclusion();

    public boolean hasName(PropertyName name) {
        return getFullName().equals(name);
    }

    public boolean isExplicitlyNamed() {
        return isExplicitlyIncluded();
    }

    public boolean isRequired() {
        return getMetadata().isRequired();
    }

    public boolean couldDeserialize() {
        return getMutator() != null;
    }

    public boolean couldSerialize() {
        return getAccessor() != null;
    }

    public Iterator<AnnotatedParameter> getConstructorParameters() {
        return ClassUtil.emptyIterator();
    }

    public AnnotatedMember getAccessor() {
        AnnotatedMember m = getGetter();
        if (m == null) {
            m = getField();
        }
        return m;
    }

    public AnnotatedMember getMutator() {
        AnnotatedMember acc = getConstructorParameter();
        if (acc == null) {
            acc = getSetter();
            if (acc == null) {
                acc = getField();
            }
        }
        return acc;
    }

    public AnnotatedMember getNonConstructorMutator() {
        AnnotatedMember m = getSetter();
        if (m == null) {
            m = getField();
        }
        return m;
    }

    public Class<?>[] findViews() {
        return null;
    }

    public AnnotationIntrospector.ReferenceProperty findReferenceType() {
        return null;
    }

    public String findReferenceName() {
        AnnotationIntrospector.ReferenceProperty ref = findReferenceType();
        if (ref == null) {
            return null;
        }
        return ref.getName();
    }

    public boolean isTypeId() {
        return false;
    }

    public ObjectIdInfo findObjectIdInfo() {
        return null;
    }
}
