package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.util.Collections;
import java.util.Iterator;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/util/SimpleBeanPropertyDefinition.class */
public class SimpleBeanPropertyDefinition extends BeanPropertyDefinition {
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final AnnotatedMember _member;
    protected final PropertyMetadata _metadata;
    protected final PropertyName _fullName;
    protected final JsonInclude.Value _inclusion;

    protected SimpleBeanPropertyDefinition(AnnotationIntrospector intr, AnnotatedMember member, PropertyName fullName, PropertyMetadata metadata, JsonInclude.Value inclusion) {
        this._annotationIntrospector = intr;
        this._member = member;
        this._fullName = fullName;
        this._metadata = metadata == null ? PropertyMetadata.STD_OPTIONAL : metadata;
        this._inclusion = inclusion;
    }

    public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member) {
        return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, PropertyName.construct(member.getName()), null, EMPTY_INCLUDE);
    }

    public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name) {
        return construct(config, member, name, (PropertyMetadata) null, EMPTY_INCLUDE);
    }

    public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, JsonInclude.Include inclusion) {
        JsonInclude.Value inclValue = (inclusion == null || inclusion == JsonInclude.Include.USE_DEFAULTS) ? EMPTY_INCLUDE : JsonInclude.Value.construct(inclusion, null);
        return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclValue);
    }

    public static SimpleBeanPropertyDefinition construct(MapperConfig<?> config, AnnotatedMember member, PropertyName name, PropertyMetadata metadata, JsonInclude.Value inclusion) {
        return new SimpleBeanPropertyDefinition(config.getAnnotationIntrospector(), member, name, metadata, inclusion);
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public BeanPropertyDefinition withSimpleName(String newName) {
        if (this._fullName.hasSimpleName(newName) && !this._fullName.hasNamespace()) {
            return this;
        }
        return new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, new PropertyName(newName), this._metadata, this._inclusion);
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public BeanPropertyDefinition withName(PropertyName newName) {
        if (this._fullName.equals(newName)) {
            return this;
        }
        return new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, newName, this._metadata, this._inclusion);
    }

    public BeanPropertyDefinition withMetadata(PropertyMetadata metadata) {
        if (metadata.equals(this._metadata)) {
            return this;
        }
        return new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, this._fullName, metadata, this._inclusion);
    }

    public BeanPropertyDefinition withInclusion(JsonInclude.Value inclusion) {
        if (this._inclusion == inclusion) {
            return this;
        }
        return new SimpleBeanPropertyDefinition(this._annotationIntrospector, this._member, this._fullName, this._metadata, inclusion);
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition, com.fasterxml.jackson.databind.util.Named
    public String getName() {
        return this._fullName.getSimpleName();
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public PropertyName getFullName() {
        return this._fullName;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public boolean hasName(PropertyName name) {
        return this._fullName.equals(name);
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public String getInternalName() {
        return getName();
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public PropertyName getWrapperName() {
        if (this._annotationIntrospector == null || this._member == null) {
            return null;
        }
        return this._annotationIntrospector.findWrapperName(this._member);
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public boolean isExplicitlyIncluded() {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public boolean isExplicitlyNamed() {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public PropertyMetadata getMetadata() {
        return this._metadata;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public JavaType getPrimaryType() {
        if (this._member == null) {
            return TypeFactory.unknownType();
        }
        return this._member.getType();
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public Class<?> getRawPrimaryType() {
        if (this._member == null) {
            return Object.class;
        }
        return this._member.getRawType();
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public JsonInclude.Value findInclusion() {
        return this._inclusion;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public boolean hasGetter() {
        return getGetter() != null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public boolean hasSetter() {
        return getSetter() != null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public boolean hasField() {
        return this._member instanceof AnnotatedField;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public boolean hasConstructorParameter() {
        return this._member instanceof AnnotatedParameter;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public AnnotatedMethod getGetter() {
        if ((this._member instanceof AnnotatedMethod) && ((AnnotatedMethod) this._member).getParameterCount() == 0) {
            return (AnnotatedMethod) this._member;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public AnnotatedMethod getSetter() {
        if ((this._member instanceof AnnotatedMethod) && ((AnnotatedMethod) this._member).getParameterCount() == 1) {
            return (AnnotatedMethod) this._member;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public AnnotatedField getField() {
        if (this._member instanceof AnnotatedField) {
            return (AnnotatedField) this._member;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public AnnotatedParameter getConstructorParameter() {
        if (this._member instanceof AnnotatedParameter) {
            return (AnnotatedParameter) this._member;
        }
        return null;
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public Iterator<AnnotatedParameter> getConstructorParameters() {
        AnnotatedParameter param = getConstructorParameter();
        if (param == null) {
            return ClassUtil.emptyIterator();
        }
        return Collections.singleton(param).iterator();
    }

    @Override // com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
    public AnnotatedMember getPrimaryMember() {
        return this._member;
    }
}
