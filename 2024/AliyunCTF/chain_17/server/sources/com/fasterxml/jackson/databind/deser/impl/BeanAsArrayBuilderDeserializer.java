package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.util.Set;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/BeanAsArrayBuilderDeserializer.class */
public class BeanAsArrayBuilderDeserializer extends BeanDeserializerBase {
    private static final long serialVersionUID = 1;
    protected final BeanDeserializerBase _delegate;
    protected final SettableBeanProperty[] _orderedProperties;
    protected final AnnotatedMethod _buildMethod;
    protected final JavaType _targetType;

    public BeanAsArrayBuilderDeserializer(BeanDeserializerBase delegate, JavaType targetType, SettableBeanProperty[] ordered, AnnotatedMethod buildMethod) {
        super(delegate);
        this._delegate = delegate;
        this._targetType = targetType;
        this._orderedProperties = ordered;
        this._buildMethod = buildMethod;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return this._delegate.unwrappingDeserializer(unwrapper);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withObjectIdReader(oir), this._targetType, this._orderedProperties, this._buildMethod);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withByNameInclusion(Set<String> ignorableProps, Set<String> includableProps) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withByNameInclusion(ignorableProps, includableProps), this._targetType, this._orderedProperties, this._buildMethod);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withIgnoreAllUnknown(boolean ignoreUnknown) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withIgnoreAllUnknown(ignoreUnknown), this._targetType, this._orderedProperties, this._buildMethod);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
        return new BeanAsArrayBuilderDeserializer(this._delegate.withBeanProperties(props), this._targetType, this._orderedProperties, this._buildMethod);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    protected BeanDeserializerBase asArrayDeserializer() {
        return this;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    protected final Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
        try {
            return this._buildMethod.getMember().invoke(builder, (Object[]) null);
        } catch (Exception e) {
            return wrapInstantiationProblem(e, ctxt);
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return finishBuild(ctxt, _deserializeFromNonArray(p, ctxt));
        }
        if (!this._vanillaProcessing) {
            return finishBuild(ctxt, _deserializeNonVanilla(p, ctxt));
        }
        Object builder = this._valueInstantiator.createUsingDefault(ctxt);
        SettableBeanProperty[] props = this._orderedProperties;
        int i = 0;
        int propCount = props.length;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (i != propCount) {
                SettableBeanProperty prop = props[i];
                if (prop != null) {
                    try {
                        builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                    } catch (Exception e) {
                        wrapAndThrow(e, builder, prop.getName(), ctxt);
                    }
                } else {
                    p.skipChildren();
                }
                i++;
            } else {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportInputMismatch(handledType(), "Unexpected JSON values; expected at most %d properties (in JSON Array)", Integer.valueOf(propCount));
                }
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    p.skipChildren();
                }
                return finishBuild(ctxt, builder);
            }
        }
        return finishBuild(ctxt, builder);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value) throws IOException {
        return this._delegate.deserialize(p, ctxt, value);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        return _deserializeFromNonArray(p, ctxt);
    }

    protected Object _deserializeNonVanilla(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._nonStandardCreation) {
            return deserializeFromObjectUsingNonDefault(p, ctxt);
        }
        Object builder = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            injectValues(ctxt, builder);
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
                        prop.deserializeSetAndReturn(p, ctxt, builder);
                    } catch (Exception e) {
                        wrapAndThrow(e, builder, prop.getName(), ctxt);
                    }
                } else {
                    p.skipChildren();
                }
            } else {
                if (!this._ignoreAllUnknown && ctxt.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                    ctxt.reportWrongTokenException(this, JsonToken.END_ARRAY, "Unexpected JSON value(s); expected at most %d properties (in JSON Array)", Integer.valueOf(propCount));
                }
                while (p.nextToken() != JsonToken.END_ARRAY) {
                    p.skipChildren();
                }
                return builder;
            }
        }
        return builder;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
        SettableBeanProperty[] props = this._orderedProperties;
        int propCount = props.length;
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        int i = 0;
        Object builder = null;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            SettableBeanProperty prop = i < propCount ? props[i] : null;
            if (prop == null) {
                p.skipChildren();
            } else if (activeView != null && !prop.visibleInView(activeView)) {
                p.skipChildren();
            } else if (builder != null) {
                try {
                    builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                } catch (Exception e) {
                    wrapAndThrow(e, builder, prop.getName(), ctxt);
                }
            } else {
                String propName = prop.getName();
                SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                if (!buffer.readIdProperty(propName) || creatorProp != null) {
                    if (creatorProp != null) {
                        if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
                            try {
                                builder = creator.build(ctxt, buffer);
                                if (builder.getClass() != this._beanType.getRawClass()) {
                                    return ctxt.reportBadDefinition(this._beanType, String.format("Cannot support implicit polymorphic deserialization for POJOs-as-Arrays style: nominal type %s, actual type %s", ClassUtil.getTypeDescription(this._beanType), builder.getClass().getName()));
                                }
                            } catch (Exception e2) {
                                wrapAndThrow(e2, this._beanType.getRawClass(), propName, ctxt);
                            }
                        } else {
                            continue;
                        }
                    } else {
                        buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
                    }
                }
            }
            i++;
        }
        if (builder == null) {
            try {
                builder = creator.build(ctxt, buffer);
            } catch (Exception e3) {
                return wrapInstantiationProblem(e3, ctxt);
            }
        }
        return builder;
    }

    protected Object _deserializeFromNonArray(JsonParser p, DeserializationContext ctxt) throws IOException {
        return ctxt.handleUnexpectedToken(getValueType(ctxt), p.currentToken(), p, "Cannot deserialize a POJO (of type %s) from non-Array representation (token: %s): type/property designed to be serialized as JSON Array", this._beanType.getRawClass().getName(), p.currentToken());
    }
}
