package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.MethodProperty;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BeanDeserializer.class */
public class BeanDeserializer extends BeanDeserializerBase implements Serializable {
    private static final long serialVersionUID = 1;
    protected transient Exception _nullFromCreator;
    private volatile transient NameTransformer _currentlyTransforming;

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public /* bridge */ /* synthetic */ BeanDeserializerBase withByNameInclusion(Set set, Set set2) {
        return withByNameInclusion((Set<String>) set, (Set<String>) set2);
    }

    @Deprecated
    public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, null, hasViews);
    }

    public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, Set<String> includableProps, boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, includableProps, hasViews);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanDeserializer(BeanDeserializerBase src) {
        super(src, src._ignoreAllUnknown);
    }

    protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
        super(src, ignoreAllUnknown);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
        super(src, unwrapper);
    }

    public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
        super(src, oir);
    }

    @Deprecated
    public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps) {
        super(src, ignorableProps);
    }

    public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps, Set<String> includableProps) {
        super(src, ignorableProps, includableProps);
    }

    public BeanDeserializer(BeanDeserializerBase src, BeanPropertyMap props) {
        super(src, props);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer transformer) {
        if (getClass() != BeanDeserializer.class) {
            return this;
        }
        if (this._currentlyTransforming == transformer) {
            return this;
        }
        this._currentlyTransforming = transformer;
        try {
            return new BeanDeserializer(this, transformer);
        } finally {
            this._currentlyTransforming = null;
        }
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {
        return new BeanDeserializer(this, oir);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializer withByNameInclusion(Set<String> ignorableProps, Set<String> includableProps) {
        return new BeanDeserializer(this, ignorableProps, includableProps);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withIgnoreAllUnknown(boolean ignoreUnknown) {
        return new BeanDeserializer(this, ignoreUnknown);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
        return new BeanDeserializer(this, props);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    protected BeanDeserializerBase asArrayDeserializer() {
        SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
        return new BeanAsArrayDeserializer(this, props);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.isExpectedStartObjectToken()) {
            if (this._vanillaProcessing) {
                return vanillaDeserialize(p, ctxt, p.nextToken());
            }
            p.nextToken();
            if (this._objectIdReader != null) {
                return deserializeWithObjectId(p, ctxt);
            }
            return deserializeFromObject(p, ctxt);
        }
        return _deserializeOther(p, ctxt, p.currentToken());
    }

    protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
        if (t != null) {
            switch (t) {
                case VALUE_STRING:
                    return deserializeFromString(p, ctxt);
                case VALUE_NUMBER_INT:
                    return deserializeFromNumber(p, ctxt);
                case VALUE_NUMBER_FLOAT:
                    return deserializeFromDouble(p, ctxt);
                case VALUE_EMBEDDED_OBJECT:
                    return deserializeFromEmbedded(p, ctxt);
                case VALUE_TRUE:
                case VALUE_FALSE:
                    return deserializeFromBoolean(p, ctxt);
                case VALUE_NULL:
                    return deserializeFromNull(p, ctxt);
                case START_ARRAY:
                    return _deserializeFromArray(p, ctxt);
                case FIELD_NAME:
                case END_OBJECT:
                    if (this._vanillaProcessing) {
                        return vanillaDeserialize(p, ctxt, t);
                    }
                    if (this._objectIdReader != null) {
                        return deserializeWithObjectId(p, ctxt);
                    }
                    return deserializeFromObject(p, ctxt);
            }
        }
        return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }

    @Deprecated
    protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
        throw ctxt.endOfInputException(handledType());
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
        String propName;
        String nextFieldName;
        Class<?> view;
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        if (this._unwrappedPropertyHandler != null) {
            return deserializeWithUnwrapped(p, ctxt, bean);
        }
        if (this._externalTypeIdHandler != null) {
            return deserializeWithExternalTypeId(p, ctxt, bean);
        }
        if (p.isExpectedStartObjectToken()) {
            propName = p.nextFieldName();
            if (propName == null) {
                return bean;
            }
        } else if (p.hasTokenId(5)) {
            propName = p.currentName();
        } else {
            return bean;
        }
        if (this._needViewProcesing && (view = ctxt.getActiveView()) != null) {
            return deserializeWithView(p, ctxt, bean, view);
        }
        do {
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(p, ctxt, bean);
                } catch (Exception e) {
                    wrapAndThrow(e, bean, propName, ctxt);
                }
            } else {
                handleUnknownVanilla(p, ctxt, bean, propName);
            }
            nextFieldName = p.nextFieldName();
            propName = nextFieldName;
        } while (nextFieldName != null);
        return bean;
    }

    private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
        String nextFieldName;
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (p.hasTokenId(5)) {
            String propName = p.currentName();
            do {
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow(e, bean, propName, ctxt);
                    }
                } else {
                    handleUnknownVanilla(p, ctxt, bean, propName);
                }
                nextFieldName = p.nextFieldName();
                propName = nextFieldName;
            } while (nextFieldName != null);
        }
        return bean;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        String nextFieldName;
        Class<?> view;
        Object id;
        if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && p.hasTokenId(5) && this._objectIdReader.isValidReferencePropertyName(p.currentName(), p)) {
            return deserializeFromObjectId(p, ctxt);
        }
        if (this._nonStandardCreation) {
            if (this._unwrappedPropertyHandler != null) {
                return deserializeWithUnwrapped(p, ctxt);
            }
            if (this._externalTypeIdHandler != null) {
                return deserializeWithExternalTypeId(p, ctxt);
            }
            return deserializeFromObjectUsingNonDefault(p, ctxt);
        }
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (p.canReadObjectId() && (id = p.getObjectId()) != null) {
            _handleTypedObjectId(p, ctxt, bean, id);
        }
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        if (this._needViewProcesing && (view = ctxt.getActiveView()) != null) {
            return deserializeWithView(p, ctxt, bean, view);
        }
        if (p.hasTokenId(5)) {
            String propName = p.currentName();
            do {
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow(e, bean, propName, ctxt);
                    }
                } else {
                    handleUnknownVanilla(p, ctxt, bean, propName);
                }
                nextFieldName = p.nextFieldName();
                propName = nextFieldName;
            } while (nextFieldName != null);
        }
        return bean;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
        Object bean;
        Object bean2;
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        TokenBuffer unknown = null;
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        JsonToken t = p.currentToken();
        List<BeanReferring> referrings = null;
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.currentName();
            p.nextToken();
            SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (!buffer.readIdProperty(propName) || creatorProp != null) {
                if (creatorProp != null) {
                    if (activeView != null && !creatorProp.visibleInView(activeView)) {
                        p.skipChildren();
                    } else {
                        Object value = _deserializeWithErrorWrapping(p, ctxt, creatorProp);
                        if (buffer.assignParameter(creatorProp, value)) {
                            p.nextToken();
                            try {
                                bean2 = creator.build(ctxt, buffer);
                            } catch (Exception e) {
                                bean2 = wrapInstantiationProblem(e, ctxt);
                            }
                            if (bean2 == null) {
                                return ctxt.handleInstantiationProblem(handledType(), null, _creatorReturnedNullException());
                            }
                            p.setCurrentValue(bean2);
                            if (bean2.getClass() != this._beanType.getRawClass()) {
                                return handlePolymorphic(p, ctxt, p.streamReadConstraints(), bean2, unknown);
                            }
                            if (unknown != null) {
                                bean2 = handleUnknownProperties(ctxt, bean2, unknown);
                            }
                            return deserialize(p, ctxt, bean2);
                        }
                    }
                } else {
                    SettableBeanProperty prop = this._beanProperties.find(propName);
                    if (prop != null && (!this._beanType.isRecordType() || (prop instanceof MethodProperty))) {
                        try {
                            buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
                        } catch (UnresolvedForwardReference reference) {
                            BeanReferring referring = handleUnresolvedReference(ctxt, prop, buffer, reference);
                            if (referrings == null) {
                                referrings = new ArrayList<>();
                            }
                            referrings.add(referring);
                        }
                    } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
                        handleIgnoredProperty(p, ctxt, handledType(), propName);
                    } else if (this._anySetter != null) {
                        try {
                            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
                        } catch (Exception e2) {
                            wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                        }
                    } else if (this._ignoreAllUnknown) {
                        p.skipChildren();
                    } else {
                        if (unknown == null) {
                            unknown = ctxt.bufferForInputBuffering(p);
                        }
                        unknown.writeFieldName(propName);
                        unknown.copyCurrentStructure(p);
                    }
                }
            }
            t = p.nextToken();
        }
        try {
            bean = creator.build(ctxt, buffer);
        } catch (Exception e3) {
            wrapInstantiationProblem(e3, ctxt);
            bean = null;
        }
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        if (referrings != null) {
            for (BeanReferring referring2 : referrings) {
                referring2.setBean(bean);
            }
        }
        if (unknown != null) {
            if (bean.getClass() != this._beanType.getRawClass()) {
                return handlePolymorphic(null, ctxt, p.streamReadConstraints(), bean, unknown);
            }
            return handleUnknownProperties(ctxt, bean, unknown);
        }
        return bean;
    }

    private BeanReferring handleUnresolvedReference(DeserializationContext ctxt, SettableBeanProperty prop, PropertyValueBuffer buffer, UnresolvedForwardReference reference) throws JsonMappingException {
        BeanReferring referring = new BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
        reference.getRoid().appendReferring(referring);
        return referring;
    }

    protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
        try {
            return prop.deserialize(p, ctxt);
        } catch (Exception e) {
            wrapAndThrow(e, this._beanType.getRawClass(), prop.getName(), ctxt);
            return null;
        }
    }

    protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.requiresCustomCodec()) {
            TokenBuffer tb = ctxt.bufferForInputBuffering(p);
            tb.writeEndObject();
            JsonParser p2 = tb.asParser(p);
            p2.nextToken();
            Object ob = this._vanillaProcessing ? vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : deserializeFromObject(p2, ctxt);
            p2.close();
            return ob;
        }
        return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer
    public Object _deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonDeserializer<Object> delegateDeser = this._arrayDelegateDeserializer;
        if (delegateDeser == null) {
            JsonDeserializer<Object> jsonDeserializer = this._delegateDeserializer;
            delegateDeser = jsonDeserializer;
            if (jsonDeserializer == null) {
                CoercionAction act = _findCoercionFromEmptyArray(ctxt);
                boolean unwrap = ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);
                if (unwrap || act != CoercionAction.Fail) {
                    JsonToken unwrappedToken = p.nextToken();
                    if (unwrappedToken == JsonToken.END_ARRAY) {
                        switch (act) {
                            case AsEmpty:
                                return getEmptyValue(ctxt);
                            case AsNull:
                            case TryConvert:
                                return getNullValue(ctxt);
                            default:
                                return ctxt.handleUnexpectedToken(getValueType(ctxt), JsonToken.START_ARRAY, p, (String) null, new Object[0]);
                        }
                    }
                    if (unwrap) {
                        if (unwrappedToken == JsonToken.START_ARRAY) {
                            JavaType targetType = getValueType(ctxt);
                            return ctxt.handleUnexpectedToken(targetType, JsonToken.START_ARRAY, p, "Cannot deserialize value of type %s from deeply-nested Array: only single wrapper allowed with `%s`", ClassUtil.getTypeDescription(targetType), "DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS");
                        }
                        Object value = deserialize(p, ctxt);
                        if (p.nextToken() != JsonToken.END_ARRAY) {
                            handleMissingEndArrayForSingle(p, ctxt);
                        }
                        return value;
                    }
                }
                return ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            }
        }
        Object bean = this._valueInstantiator.createUsingArrayDelegate(ctxt, delegateDeser.deserialize(p, ctxt));
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        return bean;
    }

    protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
        String nextFieldName;
        if (p.hasTokenId(5)) {
            String propName = p.currentName();
            do {
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    if (!prop.visibleInView(activeView)) {
                        p.skipChildren();
                    } else {
                        try {
                            prop.deserializeAndSet(p, ctxt, bean);
                        } catch (Exception e) {
                            wrapAndThrow(e, bean, propName, ctxt);
                        }
                    }
                } else {
                    handleUnknownVanilla(p, ctxt, bean, propName);
                }
                nextFieldName = p.nextFieldName();
                propName = nextFieldName;
            } while (nextFieldName != null);
        }
        return bean;
    }

    protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
        }
        TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
        tokens.writeStartObject();
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        String currentName = p.hasTokenId(5) ? p.currentName() : null;
        while (true) {
            String propName = currentName;
            if (propName != null) {
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    if (activeView != null && !prop.visibleInView(activeView)) {
                        p.skipChildren();
                    } else {
                        try {
                            prop.deserializeAndSet(p, ctxt, bean);
                        } catch (Exception e) {
                            wrapAndThrow(e, bean, propName, ctxt);
                        }
                    }
                } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
                    handleIgnoredProperty(p, ctxt, bean, propName);
                } else if (this._anySetter == null) {
                    tokens.writeFieldName(propName);
                    tokens.copyCurrentStructure(p);
                } else {
                    TokenBuffer b2 = ctxt.bufferAsCopyOfValue(p);
                    tokens.writeFieldName(propName);
                    tokens.append(b2);
                    try {
                        this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
                    } catch (Exception e2) {
                        wrapAndThrow(e2, bean, propName, ctxt);
                    }
                }
                currentName = p.nextFieldName();
            } else {
                tokens.writeEndObject();
                this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
                return bean;
            }
        }
    }

    protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }
        TokenBuffer tokens = ctxt.bufferForInputBuffering(p);
        tokens.writeStartObject();
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (t == JsonToken.FIELD_NAME) {
            String propName = p.currentName();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            p.nextToken();
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                } else {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            } else if (IgnorePropertiesUtil.shouldIgnore(propName, this._ignorableProps, this._includableProps)) {
                handleIgnoredProperty(p, ctxt, bean, propName);
            } else if (this._anySetter == null) {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
            } else {
                TokenBuffer b2 = ctxt.bufferAsCopyOfValue(p);
                tokens.writeFieldName(propName);
                tokens.append(b2);
                try {
                    this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
                } catch (Exception e2) {
                    wrapAndThrow(e2, bean, propName, ctxt);
                }
            }
            t = p.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
        return bean;
    }

    /* JADX WARN: Incorrect condition in loop: B:3:0x0028 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object deserializeUsingPropertyBasedWithUnwrapped(com.fasterxml.jackson.core.JsonParser r10, com.fasterxml.jackson.databind.DeserializationContext r11) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 447
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BeanDeserializer.deserializeUsingPropertyBasedWithUnwrapped(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):java.lang.Object");
    }

    protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
    }

    protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
        return _deserializeWithExternalTypeId(p, ctxt, bean, this._externalTypeIdHandler.start());
    }

    /* JADX WARN: Incorrect condition in loop: B:6:0x001c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object _deserializeWithExternalTypeId(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8, java.lang.Object r9, com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler r10) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 235
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BeanDeserializer._deserializeWithExternalTypeId(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, java.lang.Object, com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler):java.lang.Object");
    }

    /* JADX WARN: Incorrect condition in loop: B:6:0x0037 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object deserializeUsingPropertyBasedWithExternalTypeId(com.fasterxml.jackson.core.JsonParser r9, com.fasterxml.jackson.databind.DeserializationContext r10) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 425
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BeanDeserializer.deserializeUsingPropertyBasedWithExternalTypeId(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):java.lang.Object");
    }

    protected Exception _creatorReturnedNullException() {
        if (this._nullFromCreator == null) {
            this._nullFromCreator = new NullPointerException("JSON Creator returned null");
        }
        return this._nullFromCreator;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BeanDeserializer$BeanReferring.class */
    public static class BeanReferring extends ReadableObjectId.Referring {
        private final DeserializationContext _context;
        private final SettableBeanProperty _prop;
        private Object _bean;

        BeanReferring(DeserializationContext ctxt, UnresolvedForwardReference ref, JavaType valueType, PropertyValueBuffer buffer, SettableBeanProperty prop) {
            super(ref, valueType);
            this._context = ctxt;
            this._prop = prop;
        }

        public void setBean(Object bean) {
            this._bean = bean;
        }

        @Override // com.fasterxml.jackson.databind.deser.impl.ReadableObjectId.Referring
        public void handleResolvedForwardReference(Object id, Object value) throws IOException {
            if (this._bean == null) {
                this._context.reportInputMismatch(this._prop, "Cannot resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", this._prop.getName(), this._prop.getDeclaringClass().getName());
            }
            this._prop.set(this._bean, value);
        }
    }
}
