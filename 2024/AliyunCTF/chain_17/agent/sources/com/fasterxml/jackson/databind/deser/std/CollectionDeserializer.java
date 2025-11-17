package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@JacksonStdImpl
/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/CollectionDeserializer.class */
public class CollectionDeserializer extends ContainerDeserializerBase<Collection<Object>> implements ContextualDeserializer {
    private static final long serialVersionUID = -1;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final JsonDeserializer<Object> _delegateDeserializer;

    public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
        this(collectionType, valueDeser, valueTypeDeser, valueInstantiator, null, null, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator, JsonDeserializer<Object> delegateDeser, NullValueProvider nuller, Boolean unwrapSingle) {
        super(collectionType, nuller, unwrapSingle);
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = delegateDeser;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CollectionDeserializer(CollectionDeserializer src) {
        super(src);
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
    }

    protected CollectionDeserializer withResolved(JsonDeserializer<?> dd, JsonDeserializer<?> vd, TypeDeserializer vtd, NullValueProvider nuller, Boolean unwrapSingle) {
        return new CollectionDeserializer(this._containerType, vd, vtd, this._valueInstantiator, dd, nuller, unwrapSingle);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public boolean isCachable() {
        return this._valueDeserializer == null && this._valueTypeDeserializer == null && this._delegateDeserializer == null;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }

    @Override // com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public CollectionDeserializer createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> valueDeser;
        JsonDeserializer<Object> delegateDeser = null;
        if (this._valueInstantiator != null) {
            if (this._valueInstantiator.canCreateUsingDelegate()) {
                JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                if (delegateType == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                delegateDeser = findDeserializer(ctxt, delegateType, property);
            } else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
                JavaType delegateType2 = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
                if (delegateType2 == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                delegateDeser = findDeserializer(ctxt, delegateType2, property);
            }
        }
        Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonDeserializer<?> valueDeser2 = findConvertingContentDeserializer(ctxt, property, this._valueDeserializer);
        JavaType vt = this._containerType.getContentType();
        if (valueDeser2 == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser2, property, vt);
        }
        TypeDeserializer valueTypeDeser = this._valueTypeDeserializer;
        if (valueTypeDeser != null) {
            valueTypeDeser = valueTypeDeser.forProperty(property);
        }
        NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);
        if (!Objects.equals(unwrapSingle, this._unwrapSingle) || nuller != this._nullProvider || delegateDeser != this._delegateDeserializer || valueDeser != this._valueDeserializer || valueTypeDeser != this._valueTypeDeserializer) {
            return withResolved(delegateDeser, valueDeser, valueTypeDeser, nuller, unwrapSingle);
        }
        return this;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.deser.ValueInstantiator.Gettable
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return (Collection) this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (p.isExpectedStartArrayToken()) {
            return _deserializeFromArray(p, ctxt, createDefaultInstance(ctxt));
        }
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _deserializeFromString(p, ctxt, p.getText());
        }
        return handleNonArray(p, ctxt, createDefaultInstance(ctxt));
    }

    protected Collection<Object> createDefaultInstance(DeserializationContext ctxt) throws IOException {
        return (Collection) this._valueInstantiator.createUsingDefault(ctxt);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Collection<Object> deserialize(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
        if (p.isExpectedStartArrayToken()) {
            return _deserializeFromArray(p, ctxt, result);
        }
        return handleNonArray(p, ctxt, result);
    }

    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer, com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    protected Collection<Object> _deserializeFromString(JsonParser p, DeserializationContext ctxt, String value) throws IOException {
        CoercionAction act;
        Class<?> rawTargetType = handledType();
        if (value.isEmpty()) {
            CoercionAction act2 = ctxt.findCoercionAction(logicalType(), rawTargetType, CoercionInputShape.EmptyString);
            if (act2 != null && act2 != CoercionAction.Fail) {
                return (Collection) _deserializeFromEmptyString(p, ctxt, act2, rawTargetType, "empty String (\"\")");
            }
        } else if (_isBlank(value) && (act = ctxt.findCoercionFromBlankString(logicalType(), rawTargetType, CoercionAction.Fail)) != CoercionAction.Fail) {
            return (Collection) _deserializeFromEmptyString(p, ctxt, act, rawTargetType, "blank String (all whitespace)");
        }
        return handleNonArray(p, ctxt, createDefaultInstance(ctxt));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Collection<Object> _deserializeFromArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
        Object value;
        p.setCurrentValue(result);
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        if (valueDes.getObjectIdReader() != null) {
            return _deserializeWithObjectId(p, ctxt, result);
        }
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        while (true) {
            JsonToken t = p.nextToken();
            if (t != JsonToken.END_ARRAY) {
                try {
                    if (t == JsonToken.VALUE_NULL) {
                        if (!this._skipNullValues) {
                            value = this._nullProvider.getNullValue(ctxt);
                        }
                    } else if (typeDeser == null) {
                        value = valueDes.deserialize(p, ctxt);
                    } else {
                        value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                    }
                    result.add(value);
                } catch (Exception e) {
                    boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
                    if (!wrap) {
                        ClassUtil.throwIfRTE(e);
                    }
                    throw JsonMappingException.wrapWithPath(e, result, result.size());
                }
            } else {
                return result;
            }
        }
    }

    protected final Collection<Object> handleNonArray(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
        Object value;
        boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (Collection) ctxt.handleUnexpectedToken(this._containerType, p);
        }
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        try {
            if (p.hasToken(JsonToken.VALUE_NULL)) {
                if (this._skipNullValues) {
                    return result;
                }
                value = this._nullProvider.getNullValue(ctxt);
            } else if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            } else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            result.add(value);
            return result;
        } catch (Exception e) {
            boolean wrap = ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
            if (!wrap) {
                ClassUtil.throwIfRTE(e);
            }
            throw JsonMappingException.wrapWithPath(e, Object.class, result.size());
        }
    }

    protected Collection<Object> _deserializeWithObjectId(JsonParser p, DeserializationContext ctxt, Collection<Object> result) throws IOException {
        Object value;
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt, result);
        }
        p.setCurrentValue(result);
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        CollectionReferringAccumulator referringAccumulator = new CollectionReferringAccumulator(this._containerType.getContentType().getRawClass(), result);
        while (true) {
            JsonToken t = p.nextToken();
            if (t != JsonToken.END_ARRAY) {
                try {
                } catch (UnresolvedForwardReference reference) {
                    ReadableObjectId.Referring ref = referringAccumulator.handleUnresolvedReference(reference);
                    reference.getRoid().appendReferring(ref);
                } catch (Exception e) {
                    boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
                    if (!wrap) {
                        ClassUtil.throwIfRTE(e);
                    }
                    throw JsonMappingException.wrapWithPath(e, result, result.size());
                }
                if (t == JsonToken.VALUE_NULL) {
                    if (!this._skipNullValues) {
                        value = this._nullProvider.getNullValue(ctxt);
                    }
                } else if (typeDeser == null) {
                    value = valueDes.deserialize(p, ctxt);
                } else {
                    value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                }
                referringAccumulator.add(value);
            } else {
                return result;
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/CollectionDeserializer$CollectionReferringAccumulator.class */
    public static class CollectionReferringAccumulator {
        private final Class<?> _elementType;
        private final Collection<Object> _result;
        private List<CollectionReferring> _accumulator = new ArrayList();

        public CollectionReferringAccumulator(Class<?> elementType, Collection<Object> result) {
            this._elementType = elementType;
            this._result = result;
        }

        public void add(Object value) {
            if (this._accumulator.isEmpty()) {
                this._result.add(value);
            } else {
                CollectionReferring ref = this._accumulator.get(this._accumulator.size() - 1);
                ref.next.add(value);
            }
        }

        public ReadableObjectId.Referring handleUnresolvedReference(UnresolvedForwardReference reference) {
            CollectionReferring id = new CollectionReferring(this, reference, this._elementType);
            this._accumulator.add(id);
            return id;
        }

        public void resolveForwardReference(Object id, Object value) throws IOException {
            Iterator<CollectionReferring> iterator = this._accumulator.iterator();
            Collection<Object> collection = this._result;
            while (true) {
                Collection<Object> previous = collection;
                if (iterator.hasNext()) {
                    CollectionReferring ref = iterator.next();
                    if (ref.hasId(id)) {
                        iterator.remove();
                        previous.add(value);
                        previous.addAll(ref.next);
                        return;
                    }
                    collection = ref.next;
                } else {
                    throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id + "] that wasn't previously seen as unresolved.");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/std/CollectionDeserializer$CollectionReferring.class */
    public static final class CollectionReferring extends ReadableObjectId.Referring {
        private final CollectionReferringAccumulator _parent;
        public final List<Object> next;

        CollectionReferring(CollectionReferringAccumulator parent, UnresolvedForwardReference reference, Class<?> contentType) {
            super(reference, contentType);
            this.next = new ArrayList();
            this._parent = parent;
        }

        @Override // com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring
        public void handleResolvedForwardReference(Object id, Object value) throws IOException {
            this._parent.resolveForwardReference(id, value);
        }
    }
}
