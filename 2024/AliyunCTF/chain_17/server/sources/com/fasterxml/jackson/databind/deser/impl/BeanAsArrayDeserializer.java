package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/BeanAsArrayDeserializer.class */
public class BeanAsArrayDeserializer extends BeanDeserializerBase {
    private static final long serialVersionUID = 1;
    protected final BeanDeserializerBase _delegate;
    protected final SettableBeanProperty[] _orderedProperties;

    public BeanAsArrayDeserializer(BeanDeserializerBase delegate, SettableBeanProperty[] ordered) {
        super(delegate);
        this._delegate = delegate;
        this._orderedProperties = ordered;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return this._delegate.unwrappingDeserializer(unwrapper);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
        return new BeanAsArrayDeserializer(this._delegate.withObjectIdReader(oir), this._orderedProperties);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withByNameInclusion(Set<String> ignorableProps, Set<String> includableProps) {
        return new BeanAsArrayDeserializer(this._delegate.withByNameInclusion(ignorableProps, includableProps), this._orderedProperties);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withIgnoreAllUnknown(boolean ignoreUnknown) {
        return new BeanAsArrayDeserializer(this._delegate.withIgnoreAllUnknown(ignoreUnknown), this._orderedProperties);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
        return new BeanAsArrayDeserializer(this._delegate.withBeanProperties(props), this._orderedProperties);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    protected BeanDeserializerBase asArrayDeserializer() {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return _deserializeFromNonArray(p, ctxt);
        }
        if (!this._vanillaProcessing) {
            return _deserializeNonVanilla(p, ctxt);
        }
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i != propCount) {
                SettableBeanProperty prop = props[i];
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                } else {
                    p.skipChildren();
                }
                i++;
            } else {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", Integer.valueOf(propCount));
                }
                do {
                    p.skipChildren();
                } while (p.nextToken() != JsonToken.END_ARRAY);
                return bean;
            }
        }
        return bean;
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
        p.setCurrentValue(bean);
        if (!p.isExpectedStartArrayToken()) {
            return _deserializeFromNonArray(p, ctxt);
        }
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i != propCount) {
                SettableBeanProperty prop = props[i];
                if (prop != null) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                } else {
                    p.skipChildren();
                }
                i++;
            } else {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", Integer.valueOf(propCount));
                }
                do {
                    p.skipChildren();
                } while (p.nextToken() != JsonToken.END_ARRAY);
                return bean;
            }
        }
        return bean;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        return _deserializeFromNonArray(p, ctxt);
    }

    protected Object _deserializeNonVanilla(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._nonStandardCreation) {
            return deserializeFromObjectUsingNonDefault(p, ctxt);
        }
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        p.setCurrentValue(bean);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i != propCount) {
                SettableBeanProperty prop = props[i];
                i++;
                if (prop != null && (activeView == null || prop.visibleInView(activeView))) {
                    try {
                        prop.deserializeAndSet(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow(e, bean, prop.getName(), ctxt);
                    }
                } else {
                    p.skipChildren();
                }
            } else {
                if (!this._ignoreAllUnknown) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON values; expected at most %d properties (in JSON Array)", Integer.valueOf(propCount));
                }
                do {
                    p.skipChildren();
                } while (p.nextToken() != JsonToken.END_ARRAY);
                return bean;
            }
        }
        return bean;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        SettableBeanProperty[] props = this._orderedProperties;
        int propCount = props.length;
        int i = 0;
        Object bean = null;
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            SettableBeanProperty prop = i < propCount ? props[i] : null;
            if (prop == null) {
                p.skipChildren();
            } else if (activeView != null && !prop.visibleInView(activeView)) {
                p.skipChildren();
            } else if (bean != null) {
                try {
                    prop.deserializeAndSet(p, ctxt, bean);
                } catch (Exception e) {
                    wrapAndThrow(e, bean, prop.getName(), ctxt);
                }
            } else {
                String propName = prop.getName();
                SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                if (!buffer.readIdProperty(propName) || creatorProp != null) {
                    if (creatorProp != null) {
                        if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                            try {
                                bean = creator.build(ctxt, buffer);
                                p.setCurrentValue(bean);
                                if (bean.getClass() != this._beanType.getRawClass()) {
                                    ctxt.reportBadDefinition(this._beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", ClassUtil.getTypeDescription(this._beanType), ClassUtil.getClassDescription(bean)));
                                }
                            } catch (Exception e2) {
                                wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                            }
                        }
                    } else {
                        buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                    }
                }
            }
            i++;
        }
        if (bean == null) {
            try {
                bean = creator.build(ctxt, buffer);
            } catch (Exception e3) {
                return wrapInstantiationProblem(e3, ctxt);
            }
        }
        return bean;
    }

    protected Object _deserializeFromNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p, "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array", ClassUtil.getTypeDescription(this._beanType), p.currentToken());
    }
}
