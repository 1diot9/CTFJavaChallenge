package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/BeanDeserializerBuilder.class */
public class BeanDeserializerBuilder {
    protected final DeserializationConfig _config;
    protected final DeserializationContext _context;
    protected final BeanDescription _beanDesc;
    protected final Map<String, SettableBeanProperty> _properties = new LinkedHashMap();
    protected List<ValueInjector> _injectables;
    protected HashMap<String, SettableBeanProperty> _backRefProperties;
    protected HashSet<String> _ignorableProps;
    protected HashSet<String> _includableProps;
    protected ValueInstantiator _valueInstantiator;
    protected ObjectIdReader _objectIdReader;
    protected SettableAnyProperty _anySetter;
    protected boolean _ignoreAllUnknown;
    protected AnnotatedMethod _buildMethod;
    protected JsonPOJOBuilder.Value _builderConfig;

    public BeanDeserializerBuilder(BeanDescription beanDesc, DeserializationContext ctxt) {
        this._beanDesc = beanDesc;
        this._context = ctxt;
        this._config = ctxt.getConfig();
    }

    protected BeanDeserializerBuilder(BeanDeserializerBuilder src) {
        this._beanDesc = src._beanDesc;
        this._context = src._context;
        this._config = src._config;
        this._properties.putAll(src._properties);
        this._injectables = _copy(src._injectables);
        this._backRefProperties = _copy(src._backRefProperties);
        this._ignorableProps = src._ignorableProps;
        this._includableProps = src._includableProps;
        this._valueInstantiator = src._valueInstantiator;
        this._objectIdReader = src._objectIdReader;
        this._anySetter = src._anySetter;
        this._ignoreAllUnknown = src._ignoreAllUnknown;
        this._buildMethod = src._buildMethod;
        this._builderConfig = src._builderConfig;
    }

    private static HashMap<String, SettableBeanProperty> _copy(HashMap<String, SettableBeanProperty> src) {
        if (src == null) {
            return null;
        }
        return new HashMap<>(src);
    }

    private static <T> List<T> _copy(List<T> src) {
        if (src == null) {
            return null;
        }
        return new ArrayList(src);
    }

    public void addOrReplaceProperty(SettableBeanProperty prop, boolean allowOverride) {
        this._properties.put(prop.getName(), prop);
    }

    public void addProperty(SettableBeanProperty prop) {
        SettableBeanProperty old = this._properties.put(prop.getName(), prop);
        if (old != null && old != prop) {
            throw new IllegalArgumentException("Duplicate property '" + prop.getName() + "' for " + this._beanDesc.getType());
        }
    }

    public void addBackReferenceProperty(String referenceName, SettableBeanProperty prop) throws JsonMappingException {
        if (this._backRefProperties == null) {
            this._backRefProperties = new HashMap<>(4);
        }
        if (this._config.canOverrideAccessModifiers()) {
            try {
                prop.fixAccess(this._config);
            } catch (IllegalArgumentException e) {
                _handleBadAccess(e);
            }
        }
        this._backRefProperties.put(referenceName, prop);
    }

    public void addInjectable(PropertyName propName, JavaType propType, Annotations contextAnnotations, AnnotatedMember member, Object valueId) throws JsonMappingException {
        if (this._injectables == null) {
            this._injectables = new ArrayList();
        }
        if (this._config.canOverrideAccessModifiers()) {
            try {
                member.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            } catch (IllegalArgumentException e) {
                _handleBadAccess(e);
            }
        }
        this._injectables.add(new ValueInjector(propName, propType, member, valueId));
    }

    public void addIgnorable(String propName) {
        if (this._ignorableProps == null) {
            this._ignorableProps = new HashSet<>();
        }
        this._ignorableProps.add(propName);
    }

    public void addIncludable(String propName) {
        if (this._includableProps == null) {
            this._includableProps = new HashSet<>();
        }
        this._includableProps.add(propName);
    }

    public void addCreatorProperty(SettableBeanProperty prop) {
        addProperty(prop);
    }

    public void setAnySetter(SettableAnyProperty s) {
        if (this._anySetter != null && s != null) {
            throw new IllegalStateException("_anySetter already set to non-null");
        }
        this._anySetter = s;
    }

    public void setIgnoreUnknownProperties(boolean ignore) {
        this._ignoreAllUnknown = ignore;
    }

    public void setValueInstantiator(ValueInstantiator inst) {
        this._valueInstantiator = inst;
    }

    public void setObjectIdReader(ObjectIdReader r) {
        this._objectIdReader = r;
    }

    public void setPOJOBuilder(AnnotatedMethod buildMethod, JsonPOJOBuilder.Value config) {
        this._buildMethod = buildMethod;
        this._builderConfig = config;
    }

    public Iterator<SettableBeanProperty> getProperties() {
        return this._properties.values().iterator();
    }

    public SettableBeanProperty findProperty(PropertyName propertyName) {
        return this._properties.get(propertyName.getSimpleName());
    }

    public boolean hasProperty(PropertyName propertyName) {
        return findProperty(propertyName) != null;
    }

    public SettableBeanProperty removeProperty(PropertyName name) {
        return this._properties.remove(name.getSimpleName());
    }

    public SettableAnyProperty getAnySetter() {
        return this._anySetter;
    }

    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    public List<ValueInjector> getInjectables() {
        return this._injectables;
    }

    public ObjectIdReader getObjectIdReader() {
        return this._objectIdReader;
    }

    public AnnotatedMethod getBuildMethod() {
        return this._buildMethod;
    }

    public JsonPOJOBuilder.Value getBuilderConfig() {
        return this._builderConfig;
    }

    public boolean hasIgnorable(String name) {
        return IgnorePropertiesUtil.shouldIgnore(name, this._ignorableProps, this._includableProps);
    }

    public JsonDeserializer<?> build() throws JsonMappingException {
        Collection<SettableBeanProperty> props = this._properties.values();
        _fixAccess(props);
        BeanPropertyMap propertyMap = BeanPropertyMap.construct(this._config, props, _collectAliases(props), _findCaseInsensitivity());
        propertyMap.assignIndexes();
        boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (!anyViews) {
            Iterator<SettableBeanProperty> it = props.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SettableBeanProperty prop = it.next();
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }
        if (this._objectIdReader != null) {
            ObjectIdValueProperty prop2 = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop2);
        }
        return new BeanDeserializer(this, this._beanDesc, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, this._includableProps, anyViews);
    }

    public AbstractDeserializer buildAbstract() {
        return new AbstractDeserializer(this, this._beanDesc, this._backRefProperties, this._properties);
    }

    public JsonDeserializer<?> buildBuilderBased(JavaType valueType, String expBuildMethodName) throws JsonMappingException {
        if (this._buildMethod == null) {
            if (!expBuildMethodName.isEmpty()) {
                this._context.reportBadDefinition(this._beanDesc.getType(), String.format("Builder class %s does not have build method (name: '%s')", ClassUtil.getTypeDescription(this._beanDesc.getType()), expBuildMethodName));
            }
        } else {
            Class<?> rawBuildType = this._buildMethod.getRawReturnType();
            Class<?> rawValueType = valueType.getRawClass();
            if (rawBuildType != rawValueType && !rawBuildType.isAssignableFrom(rawValueType) && !rawValueType.isAssignableFrom(rawBuildType)) {
                this._context.reportBadDefinition(this._beanDesc.getType(), String.format("Build method `%s` has wrong return type (%s), not compatible with POJO type (%s)", this._buildMethod.getFullName(), ClassUtil.getClassDescription(rawBuildType), ClassUtil.getTypeDescription(valueType)));
            }
        }
        Collection<SettableBeanProperty> props = this._properties.values();
        _fixAccess(props);
        BeanPropertyMap propertyMap = BeanPropertyMap.construct(this._config, props, _collectAliases(props), _findCaseInsensitivity());
        propertyMap.assignIndexes();
        boolean anyViews = !this._config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        if (!anyViews) {
            Iterator<SettableBeanProperty> it = props.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                SettableBeanProperty prop = it.next();
                if (prop.hasViews()) {
                    anyViews = true;
                    break;
                }
            }
        }
        if (this._objectIdReader != null) {
            ObjectIdValueProperty prop2 = new ObjectIdValueProperty(this._objectIdReader, PropertyMetadata.STD_REQUIRED);
            propertyMap = propertyMap.withProperty(prop2);
        }
        return createBuilderBasedDeserializer(valueType, propertyMap, anyViews);
    }

    protected JsonDeserializer<?> createBuilderBasedDeserializer(JavaType valueType, BeanPropertyMap propertyMap, boolean anyViews) {
        return new BuilderBasedDeserializer(this, this._beanDesc, valueType, propertyMap, this._backRefProperties, this._ignorableProps, this._ignoreAllUnknown, this._includableProps, anyViews);
    }

    protected void _fixAccess(Collection<SettableBeanProperty> mainProps) throws JsonMappingException {
        if (this._config.canOverrideAccessModifiers()) {
            for (SettableBeanProperty prop : mainProps) {
                try {
                    prop.fixAccess(this._config);
                } catch (IllegalArgumentException e) {
                    _handleBadAccess(e);
                }
            }
        }
        if (this._anySetter != null) {
            try {
                this._anySetter.fixAccess(this._config);
            } catch (IllegalArgumentException e2) {
                _handleBadAccess(e2);
            }
        }
        if (this._buildMethod != null) {
            try {
                this._buildMethod.fixAccess(this._config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
            } catch (IllegalArgumentException e3) {
                _handleBadAccess(e3);
            }
        }
    }

    protected Map<String, List<PropertyName>> _collectAliases(Collection<SettableBeanProperty> props) {
        Map<String, List<PropertyName>> mapping = null;
        AnnotationIntrospector intr = this._config.getAnnotationIntrospector();
        if (intr != null) {
            for (SettableBeanProperty prop : props) {
                List<PropertyName> aliases = intr.findPropertyAliases(prop.getMember());
                if (aliases != null && !aliases.isEmpty()) {
                    if (mapping == null) {
                        mapping = new HashMap<>();
                    }
                    mapping.put(prop.getName(), aliases);
                }
            }
        }
        if (mapping == null) {
            return Collections.emptyMap();
        }
        return mapping;
    }

    protected boolean _findCaseInsensitivity() {
        JsonFormat.Value format = this._beanDesc.findExpectedFormat(null);
        Boolean B = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        return B == null ? this._config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES) : B.booleanValue();
    }

    protected void _handleBadAccess(IllegalArgumentException e0) throws JsonMappingException {
        try {
            this._context.reportBadTypeDefinition(this._beanDesc, e0.getMessage(), new Object[0]);
        } catch (DatabindException e) {
            if (e.getCause() == null) {
                e.initCause(e0);
            }
            throw e;
        }
    }
}
