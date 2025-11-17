package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.impl.FailingDeserializer;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ConcreteBeanPropertyBase;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.ViewMatcher;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/SettableBeanProperty.class */
public abstract class SettableBeanProperty extends ConcreteBeanPropertyBase implements Serializable {
    protected static final JsonDeserializer<Object> MISSING_VALUE_DESERIALIZER = new FailingDeserializer("No _valueDeserializer assigned");
    protected final PropertyName _propName;
    protected final JavaType _type;
    protected final PropertyName _wrapperName;
    protected final transient Annotations _contextAnnotations;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final NullValueProvider _nullProvider;
    protected String _managedReferenceName;
    protected ObjectIdInfo _objectIdInfo;
    protected ViewMatcher _viewMatcher;
    protected int _propertyIndex;

    public abstract SettableBeanProperty withValueDeserializer(JsonDeserializer<?> jsonDeserializer);

    public abstract SettableBeanProperty withName(PropertyName propertyName);

    public abstract SettableBeanProperty withNullProvider(NullValueProvider nullValueProvider);

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public abstract AnnotatedMember getMember();

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public abstract <A extends Annotation> A getAnnotation(Class<A> cls);

    public abstract void deserializeAndSet(JsonParser jsonParser, DeserializationContext deserializationContext, Object obj) throws IOException;

    public abstract Object deserializeSetAndReturn(JsonParser jsonParser, DeserializationContext deserializationContext, Object obj) throws IOException;

    public abstract void set(Object obj, Object obj2) throws IOException;

    public abstract Object setAndReturn(Object obj, Object obj2) throws IOException;

    /* JADX INFO: Access modifiers changed from: protected */
    public SettableBeanProperty(BeanPropertyDefinition propDef, JavaType type, TypeDeserializer typeDeser, Annotations contextAnnotations) {
        this(propDef.getFullName(), type, propDef.getWrapperName(), typeDeser, contextAnnotations, propDef.getMetadata());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SettableBeanProperty(PropertyName propName, JavaType type, PropertyName wrapper, TypeDeserializer typeDeser, Annotations contextAnnotations, PropertyMetadata metadata) {
        super(metadata);
        this._propertyIndex = -1;
        if (propName == null) {
            this._propName = PropertyName.NO_NAME;
        } else {
            this._propName = propName.internSimpleName();
        }
        this._type = type;
        this._wrapperName = wrapper;
        this._contextAnnotations = contextAnnotations;
        this._viewMatcher = null;
        this._valueTypeDeserializer = typeDeser != null ? typeDeser.forProperty(this) : typeDeser;
        this._valueDeserializer = MISSING_VALUE_DESERIALIZER;
        this._nullProvider = MISSING_VALUE_DESERIALIZER;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SettableBeanProperty(PropertyName propName, JavaType type, PropertyMetadata metadata, JsonDeserializer<Object> valueDeser) {
        super(metadata);
        this._propertyIndex = -1;
        if (propName == null) {
            this._propName = PropertyName.NO_NAME;
        } else {
            this._propName = propName.internSimpleName();
        }
        this._type = type;
        this._wrapperName = null;
        this._contextAnnotations = null;
        this._viewMatcher = null;
        this._valueTypeDeserializer = null;
        this._valueDeserializer = valueDeser;
        this._nullProvider = valueDeser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SettableBeanProperty(SettableBeanProperty src) {
        super(src);
        this._propertyIndex = -1;
        this._propName = src._propName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._contextAnnotations = src._contextAnnotations;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        this._viewMatcher = src._viewMatcher;
        this._objectIdInfo = src._objectIdInfo;
        this._nullProvider = src._nullProvider;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SettableBeanProperty(SettableBeanProperty src, JsonDeserializer<?> deser, NullValueProvider nuller) {
        super(src);
        this._propertyIndex = -1;
        this._propName = src._propName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._contextAnnotations = src._contextAnnotations;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        if (deser == null) {
            this._valueDeserializer = MISSING_VALUE_DESERIALIZER;
        } else {
            this._valueDeserializer = deser;
        }
        this._viewMatcher = src._viewMatcher;
        this._objectIdInfo = src._objectIdInfo;
        this._nullProvider = nuller == MISSING_VALUE_DESERIALIZER ? this._valueDeserializer : nuller;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SettableBeanProperty(SettableBeanProperty src, PropertyName newName) {
        super(src);
        this._propertyIndex = -1;
        this._propName = newName;
        this._type = src._type;
        this._wrapperName = src._wrapperName;
        this._contextAnnotations = src._contextAnnotations;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._managedReferenceName = src._managedReferenceName;
        this._propertyIndex = src._propertyIndex;
        this._viewMatcher = src._viewMatcher;
        this._objectIdInfo = src._objectIdInfo;
        this._nullProvider = src._nullProvider;
    }

    public SettableBeanProperty withSimpleName(String simpleName) {
        PropertyName n = this._propName == null ? new PropertyName(simpleName) : this._propName.withSimpleName(simpleName);
        return n == this._propName ? this : withName(n);
    }

    public void setManagedReferenceName(String n) {
        this._managedReferenceName = n;
    }

    public void setObjectIdInfo(ObjectIdInfo objectIdInfo) {
        this._objectIdInfo = objectIdInfo;
    }

    public void setViews(Class<?>[] views) {
        if (views == null) {
            this._viewMatcher = null;
        } else {
            this._viewMatcher = ViewMatcher.construct(views);
        }
    }

    public void assignIndex(int index) {
        if (this._propertyIndex != -1) {
            throw new IllegalStateException("Property '" + getName() + "' already had index (" + this._propertyIndex + "), trying to assign " + index);
        }
        this._propertyIndex = index;
    }

    public void fixAccess(DeserializationConfig config) {
    }

    public void markAsIgnorable() {
    }

    public boolean isIgnorable() {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty, com.fasterxml.jackson.databind.util.Named
    public final String getName() {
        return this._propName.getSimpleName();
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public PropertyName getFullName() {
        return this._propName;
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
    public <A extends Annotation> A getContextAnnotation(Class<A> cls) {
        return (A) this._contextAnnotations.get(cls);
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public void depositSchemaProperty(JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
        if (isRequired()) {
            objectVisitor.property(this);
        } else {
            objectVisitor.optionalProperty(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Class<?> getDeclaringClass() {
        return getMember().getDeclaringClass();
    }

    public String getManagedReferenceName() {
        return this._managedReferenceName;
    }

    public ObjectIdInfo getObjectIdInfo() {
        return this._objectIdInfo;
    }

    public boolean hasValueDeserializer() {
        return (this._valueDeserializer == null || this._valueDeserializer == MISSING_VALUE_DESERIALIZER) ? false : true;
    }

    public boolean hasValueTypeDeserializer() {
        return this._valueTypeDeserializer != null;
    }

    public JsonDeserializer<Object> getValueDeserializer() {
        JsonDeserializer<Object> deser = this._valueDeserializer;
        if (deser == MISSING_VALUE_DESERIALIZER) {
            return null;
        }
        return deser;
    }

    public TypeDeserializer getValueTypeDeserializer() {
        return this._valueTypeDeserializer;
    }

    public NullValueProvider getNullValueProvider() {
        return this._nullProvider;
    }

    public boolean visibleInView(Class<?> activeView) {
        return this._viewMatcher == null || this._viewMatcher.isVisibleForView(activeView);
    }

    public boolean hasViews() {
        return this._viewMatcher != null;
    }

    public int getPropertyIndex() {
        return this._propertyIndex;
    }

    public int getCreatorIndex() {
        throw new IllegalStateException(String.format("Internal error: no creator index for property '%s' (of type %s)", getName(), getClass().getName()));
    }

    public Object getInjectableValueId() {
        return null;
    }

    public boolean isInjectionOnly() {
        return false;
    }

    public final Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return this._nullProvider.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        Object value = this._valueDeserializer.deserialize(p, ctxt);
        if (value == null) {
            value = this._nullProvider.getNullValue(ctxt);
        }
        return value;
    }

    public final Object deserializeWith(JsonParser p, DeserializationContext ctxt, Object toUpdate) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (NullsConstantProvider.isSkipper(this._nullProvider)) {
                return toUpdate;
            }
            return this._nullProvider.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            JavaType subType = ctxt.getTypeFactory().constructType(toUpdate.getClass());
            JsonDeserializer<Object> subTypeValueDeserializer = ctxt.findContextualValueDeserializer(subType, this);
            return subTypeValueDeserializer.deserialize(p, ctxt, toUpdate);
        }
        Object value = this._valueDeserializer.deserialize(p, ctxt, toUpdate);
        if (value == null) {
            if (NullsConstantProvider.isSkipper(this._nullProvider)) {
                return toUpdate;
            }
            value = this._nullProvider.getNullValue(ctxt);
        }
        return value;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _throwAsIOE(JsonParser p, Exception e, Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            String actType = ClassUtil.classNameOf(value);
            StringBuilder msg = new StringBuilder("Problem deserializing property '").append(getName()).append("' (expected type: ").append(getType()).append("; actual type: ").append(actType).append(")");
            String origMsg = ClassUtil.exceptionMessage(e);
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            } else {
                msg.append(" (no error message provided)");
            }
            throw JsonMappingException.from(p, msg.toString(), e);
        }
        _throwAsIOE(p, e);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IOException _throwAsIOE(JsonParser p, Exception e) throws IOException {
        ClassUtil.throwIfIOE(e);
        ClassUtil.throwIfRTE(e);
        Throwable th = ClassUtil.getRootCause(e);
        throw JsonMappingException.from(p, ClassUtil.exceptionMessage(th), th);
    }

    @Deprecated
    protected IOException _throwAsIOE(Exception e) throws IOException {
        return _throwAsIOE((JsonParser) null, e);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void _throwAsIOE(Exception e, Object value) throws IOException {
        _throwAsIOE((JsonParser) null, e, value);
    }

    public String toString() {
        return "[property '" + getName() + "']";
    }

    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/SettableBeanProperty$Delegating.class */
    public static abstract class Delegating extends SettableBeanProperty {
        protected final SettableBeanProperty delegate;

        protected abstract SettableBeanProperty withDelegate(SettableBeanProperty settableBeanProperty);

        /* JADX INFO: Access modifiers changed from: protected */
        public Delegating(SettableBeanProperty d) {
            super(d);
            this.delegate = d;
        }

        protected SettableBeanProperty _with(SettableBeanProperty newDelegate) {
            if (newDelegate == this.delegate) {
                return this;
            }
            return withDelegate(newDelegate);
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
            return _with(this.delegate.withValueDeserializer(deser));
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public SettableBeanProperty withName(PropertyName newName) {
            return _with(this.delegate.withName(newName));
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public SettableBeanProperty withNullProvider(NullValueProvider nva) {
            return _with(this.delegate.withNullProvider(nva));
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public void assignIndex(int index) {
            this.delegate.assignIndex(index);
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public void fixAccess(DeserializationConfig config) {
            this.delegate.fixAccess(config);
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        protected Class<?> getDeclaringClass() {
            return this.delegate.getDeclaringClass();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public String getManagedReferenceName() {
            return this.delegate.getManagedReferenceName();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public ObjectIdInfo getObjectIdInfo() {
            return this.delegate.getObjectIdInfo();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public boolean hasValueDeserializer() {
            return this.delegate.hasValueDeserializer();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public boolean hasValueTypeDeserializer() {
            return this.delegate.hasValueTypeDeserializer();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public JsonDeserializer<Object> getValueDeserializer() {
            return this.delegate.getValueDeserializer();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public TypeDeserializer getValueTypeDeserializer() {
            return this.delegate.getValueTypeDeserializer();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public boolean visibleInView(Class<?> activeView) {
            return this.delegate.visibleInView(activeView);
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public boolean hasViews() {
            return this.delegate.hasViews();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public int getPropertyIndex() {
            return this.delegate.getPropertyIndex();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public int getCreatorIndex() {
            return this.delegate.getCreatorIndex();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public Object getInjectableValueId() {
            return this.delegate.getInjectableValueId();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public boolean isInjectionOnly() {
            return this.delegate.isInjectionOnly();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty, com.fasterxml.jackson.databind.BeanProperty
        public AnnotatedMember getMember() {
            return this.delegate.getMember();
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty, com.fasterxml.jackson.databind.BeanProperty
        public <A extends Annotation> A getAnnotation(Class<A> cls) {
            return (A) this.delegate.getAnnotation(cls);
        }

        public SettableBeanProperty getDelegate() {
            return this.delegate;
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
            this.delegate.deserializeAndSet(p, ctxt, instance);
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
            return this.delegate.deserializeSetAndReturn(p, ctxt, instance);
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public void set(Object instance, Object value) throws IOException {
            this.delegate.set(instance, value);
        }

        @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
        public Object setAndReturn(Object instance, Object value) throws IOException {
            return this.delegate.setAndReturn(instance, value);
        }
    }
}
