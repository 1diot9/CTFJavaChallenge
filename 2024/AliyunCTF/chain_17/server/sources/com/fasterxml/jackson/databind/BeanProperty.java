package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.Named;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/BeanProperty.class */
public interface BeanProperty extends Named {
    public static final JsonFormat.Value EMPTY_FORMAT = new JsonFormat.Value();
    public static final JsonInclude.Value EMPTY_INCLUDE = JsonInclude.Value.empty();

    @Override // com.fasterxml.jackson.databind.util.Named
    String getName();

    PropertyName getFullName();

    JavaType getType();

    PropertyName getWrapperName();

    PropertyMetadata getMetadata();

    boolean isRequired();

    boolean isVirtual();

    <A extends Annotation> A getAnnotation(Class<A> cls);

    <A extends Annotation> A getContextAnnotation(Class<A> cls);

    AnnotatedMember getMember();

    @Deprecated
    JsonFormat.Value findFormatOverrides(AnnotationIntrospector annotationIntrospector);

    JsonFormat.Value findPropertyFormat(MapperConfig<?> mapperConfig, Class<?> cls);

    JsonInclude.Value findPropertyInclusion(MapperConfig<?> mapperConfig, Class<?> cls);

    List<PropertyName> findAliases(MapperConfig<?> mapperConfig);

    void depositSchemaProperty(JsonObjectFormatVisitor jsonObjectFormatVisitor, SerializerProvider serializerProvider) throws JsonMappingException;

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/BeanProperty$Std.class */
    public static class Std implements BeanProperty, Serializable {
        private static final long serialVersionUID = 1;
        protected final PropertyName _name;
        protected final JavaType _type;
        protected final PropertyName _wrapperName;
        protected final PropertyMetadata _metadata;
        protected final AnnotatedMember _member;

        public Std(PropertyName name, JavaType type, PropertyName wrapperName, AnnotatedMember member, PropertyMetadata metadata) {
            this._name = name;
            this._type = type;
            this._wrapperName = wrapperName;
            this._metadata = metadata;
            this._member = member;
        }

        @Deprecated
        public Std(PropertyName name, JavaType type, PropertyName wrapperName, Annotations contextAnnotations, AnnotatedMember member, PropertyMetadata metadata) {
            this(name, type, wrapperName, member, metadata);
        }

        public Std(Std base, JavaType newType) {
            this(base._name, newType, base._wrapperName, base._member, base._metadata);
        }

        public Std withType(JavaType type) {
            return new Std(this, type);
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public <A extends Annotation> A getAnnotation(Class<A> cls) {
            if (this._member == null) {
                return null;
            }
            return (A) this._member.getAnnotation(cls);
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        @Deprecated
        public JsonFormat.Value findFormatOverrides(AnnotationIntrospector intr) {
            JsonFormat.Value v;
            if (this._member != null && intr != null && (v = intr.findFormat(this._member)) != null) {
                return v;
            }
            return EMPTY_FORMAT;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public JsonFormat.Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType) {
            JsonFormat.Value v0 = config.getDefaultPropertyFormat(baseType);
            AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr == null || this._member == null) {
                return v0;
            }
            JsonFormat.Value v = intr.findFormat(this._member);
            if (v == null) {
                return v0;
            }
            return v0.withOverrides(v);
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType) {
            JsonInclude.Value v0 = config.getDefaultInclusion(baseType, this._type.getRawClass());
            AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr == null || this._member == null) {
                return v0;
            }
            JsonInclude.Value v = intr.findPropertyInclusion(this._member);
            if (v == null) {
                return v0;
            }
            return v0.withOverrides(v);
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public List<PropertyName> findAliases(MapperConfig<?> config) {
            return Collections.emptyList();
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty, com.fasterxml.jackson.databind.util.Named
        public String getName() {
            return this._name.getSimpleName();
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public PropertyName getFullName() {
            return this._name;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public JavaType getType() {
            return this._type;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public PropertyName getWrapperName() {
            return this._wrapperName;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public boolean isRequired() {
            return this._metadata.isRequired();
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public PropertyMetadata getMetadata() {
            return this._metadata;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public AnnotatedMember getMember() {
            return this._member;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public boolean isVirtual() {
            return false;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) {
            throw new UnsupportedOperationException("Instances of " + getClass().getName() + " should not get visited");
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/BeanProperty$Bogus.class */
    public static class Bogus implements BeanProperty {
        @Override // com.fasterxml.jackson.databind.BeanProperty, com.fasterxml.jackson.databind.util.Named
        public String getName() {
            return "";
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public PropertyName getFullName() {
            return PropertyName.NO_NAME;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public JavaType getType() {
            return TypeFactory.unknownType();
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public PropertyName getWrapperName() {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public PropertyMetadata getMetadata() {
            return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public boolean isRequired() {
            return false;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public boolean isVirtual() {
            return false;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public <A extends Annotation> A getAnnotation(Class<A> acls) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public <A extends Annotation> A getContextAnnotation(Class<A> acls) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public AnnotatedMember getMember() {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        @Deprecated
        public JsonFormat.Value findFormatOverrides(AnnotationIntrospector intr) {
            return JsonFormat.Value.empty();
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public JsonFormat.Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType) {
            return JsonFormat.Value.empty();
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType) {
            return null;
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public List<PropertyName> findAliases(MapperConfig<?> config) {
            return Collections.emptyList();
        }

        @Override // com.fasterxml.jackson.databind.BeanProperty
        public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
        }
    }
}
