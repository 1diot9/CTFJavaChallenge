package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/ReferenceTypeSerializer.class */
public abstract class ReferenceTypeSerializer<T> extends StdSerializer<T> implements ContextualSerializer {
    private static final long serialVersionUID = 1;
    public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
    protected final JavaType _referredType;
    protected final BeanProperty _property;
    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _valueSerializer;
    protected final NameTransformer _unwrapper;
    protected transient PropertySerializerMap _dynamicSerializers;
    protected final Object _suppressableValue;
    protected final boolean _suppressNulls;

    protected abstract ReferenceTypeSerializer<T> withResolved(BeanProperty beanProperty, TypeSerializer typeSerializer, JsonSerializer<?> jsonSerializer, NameTransformer nameTransformer);

    public abstract ReferenceTypeSerializer<T> withContentInclusion(Object obj, boolean z);

    protected abstract boolean _isValuePresent(T t);

    protected abstract Object _getReferenced(T t);

    protected abstract Object _getReferencedIfPresent(T t);

    public ReferenceTypeSerializer(ReferenceType fullType, boolean staticTyping, TypeSerializer vts, JsonSerializer<Object> ser) {
        super(fullType);
        this._referredType = fullType.getReferencedType();
        this._property = null;
        this._valueTypeSerializer = vts;
        this._valueSerializer = ser;
        this._unwrapper = null;
        this._suppressableValue = null;
        this._suppressNulls = false;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ReferenceTypeSerializer(ReferenceTypeSerializer<?> base, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper, Object suppressableValue, boolean suppressNulls) {
        super(base);
        this._referredType = base._referredType;
        this._dynamicSerializers = PropertySerializerMap.emptyForProperties();
        this._property = property;
        this._valueTypeSerializer = vts;
        this._valueSerializer = valueSer;
        this._unwrapper = unwrapper;
        this._suppressableValue = suppressableValue;
        this._suppressNulls = suppressNulls;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public JsonSerializer<T> unwrappingSerializer(NameTransformer transformer) {
        JsonSerializer<Object> valueSer = this._valueSerializer;
        if (valueSer != null) {
            valueSer = valueSer.unwrappingSerializer(transformer);
            if (valueSer == this._valueSerializer) {
                return this;
            }
        }
        NameTransformer unwrapper = this._unwrapper == null ? transformer : NameTransformer.chainedTransformer(transformer, this._unwrapper);
        if (this._valueSerializer == valueSer && this._unwrapper == unwrapper) {
            return this;
        }
        return withResolved(this._property, this._valueTypeSerializer, valueSer, unwrapper);
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        ReferenceTypeSerializer<T> withResolved;
        JsonInclude.Value inclV;
        JsonInclude.Include incl;
        Object valueToSuppress;
        boolean suppressNulls;
        TypeSerializer typeSer = this._valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }
        JsonSerializer<?> ser = findAnnotatedContentSerializer(provider, property);
        if (ser == null) {
            ser = this._valueSerializer;
            if (ser == null) {
                if (_useStatic(provider, property, this._referredType)) {
                    ser = _findSerializer(provider, this._referredType, property);
                }
            } else {
                ser = provider.handlePrimaryContextualization(ser, property);
            }
        }
        if (this._property == property && this._valueTypeSerializer == typeSer && this._valueSerializer == ser) {
            withResolved = this;
        } else {
            withResolved = withResolved(property, typeSer, ser, this._unwrapper);
        }
        if (property != null && (inclV = property.findPropertyInclusion(provider.getConfig(), handledType())) != null && (incl = inclV.getContentInclusion()) != JsonInclude.Include.USE_DEFAULTS) {
            switch (incl) {
                case NON_DEFAULT:
                    valueToSuppress = BeanUtil.getDefaultValue(this._referredType);
                    suppressNulls = true;
                    if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                        break;
                    }
                    break;
                case NON_ABSENT:
                    suppressNulls = true;
                    valueToSuppress = this._referredType.isReferenceType() ? MARKER_FOR_EMPTY : null;
                    break;
                case NON_EMPTY:
                    suppressNulls = true;
                    valueToSuppress = MARKER_FOR_EMPTY;
                    break;
                case CUSTOM:
                    valueToSuppress = provider.includeFilterInstance(null, inclV.getContentFilter());
                    if (valueToSuppress == null) {
                        suppressNulls = true;
                        break;
                    } else {
                        suppressNulls = provider.includeFilterSuppressNulls(valueToSuppress);
                        break;
                    }
                case NON_NULL:
                    valueToSuppress = null;
                    suppressNulls = true;
                    break;
                case ALWAYS:
                default:
                    valueToSuppress = null;
                    suppressNulls = false;
                    break;
            }
            if (this._suppressableValue != valueToSuppress || this._suppressNulls != suppressNulls) {
                withResolved = withResolved.withContentInclusion(valueToSuppress, suppressNulls);
            }
        }
        return withResolved;
    }

    protected boolean _useStatic(SerializerProvider provider, BeanProperty property, JavaType referredType) {
        if (referredType.isJavaLangObject()) {
            return false;
        }
        if (referredType.isFinal() || referredType.useStaticType()) {
            return true;
        }
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if (intr != null && property != null) {
            Annotated ann = property.getMember();
            if (ann != null) {
                JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
                if (t == JsonSerialize.Typing.STATIC) {
                    return true;
                }
                if (t == JsonSerialize.Typing.DYNAMIC) {
                    return false;
                }
            }
        }
        return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider provider, T value) {
        if (!_isValuePresent(value)) {
            return true;
        }
        Object contents = _getReferenced(value);
        if (contents == null) {
            return this._suppressNulls;
        }
        if (this._suppressableValue == null) {
            return false;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            try {
                ser = _findCachedSerializer(provider, contents.getClass());
            } catch (JsonMappingException e) {
                throw new RuntimeJsonMappingException(e);
            }
        }
        if (this._suppressableValue == MARKER_FOR_EMPTY) {
            return ser.isEmpty(provider, contents);
        }
        return this._suppressableValue.equals(contents);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isUnwrappingSerializer() {
        return this._unwrapper != null;
    }

    public JavaType getReferredType() {
        return this._referredType;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(T ref, JsonGenerator g, SerializerProvider provider) throws IOException {
        Object value = _getReferencedIfPresent(ref);
        if (value == null) {
            if (this._unwrapper == null) {
                provider.defaultSerializeNull(g);
                return;
            }
            return;
        }
        JsonSerializer<Object> ser = this._valueSerializer;
        if (ser == null) {
            ser = _findCachedSerializer(provider, value.getClass());
        }
        if (this._valueTypeSerializer != null) {
            ser.serializeWithType(value, g, provider, this._valueTypeSerializer);
        } else {
            ser.serialize(value, g, provider);
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(T ref, JsonGenerator g, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        Object value = _getReferencedIfPresent(ref);
        if (value == null) {
            if (this._unwrapper == null) {
                provider.defaultSerializeNull(g);
            }
        } else {
            JsonSerializer<Object> ser = this._valueSerializer;
            if (ser == null) {
                ser = _findCachedSerializer(provider, value.getClass());
            }
            ser.serializeWithType(value, g, provider, typeSer);
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonSerializer<?> ser = this._valueSerializer;
        if (ser == null) {
            ser = _findSerializer(visitor.getProvider(), this._referredType, this._property);
            if (this._unwrapper != null) {
                ser = ser.unwrappingSerializer(this._unwrapper);
            }
        }
        ser.acceptJsonFormatVisitor(visitor, this._referredType);
    }

    private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider, Class<?> rawType) throws JsonMappingException {
        JsonSerializer<Object> ser = this._dynamicSerializers.serializerFor(rawType);
        if (ser == null) {
            if (this._referredType.hasGenericTypes()) {
                JavaType fullType = provider.constructSpecializedType(this._referredType, rawType);
                ser = provider.findPrimaryPropertySerializer(fullType, this._property);
            } else {
                ser = provider.findPrimaryPropertySerializer(rawType, this._property);
            }
            if (this._unwrapper != null) {
                ser = ser.unwrappingSerializer(this._unwrapper);
            }
            this._dynamicSerializers = this._dynamicSerializers.newWith(rawType, ser);
        }
        return ser;
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, JavaType type, BeanProperty prop) throws JsonMappingException {
        return provider.findPrimaryPropertySerializer(type, prop);
    }
}
