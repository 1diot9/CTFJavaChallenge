package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/PropertyBasedCreator.class */
public final class PropertyBasedCreator {
    protected final int _propertyCount;
    protected final ValueInstantiator _valueInstantiator;
    protected final HashMap<String, SettableBeanProperty> _propertyLookup;
    protected final SettableBeanProperty[] _allProperties;

    protected PropertyBasedCreator(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps, boolean caseInsensitive, boolean addAliases) {
        this._valueInstantiator = valueInstantiator;
        if (caseInsensitive) {
            this._propertyLookup = CaseInsensitiveMap.construct(ctxt.getConfig().getLocale());
        } else {
            this._propertyLookup = new HashMap<>();
        }
        int len = creatorProps.length;
        this._propertyCount = len;
        this._allProperties = new SettableBeanProperty[len];
        if (addAliases) {
            DeserializationConfig config = ctxt.getConfig();
            for (SettableBeanProperty prop : creatorProps) {
                if (!prop.isIgnorable()) {
                    List<PropertyName> aliases = prop.findAliases(config);
                    if (!aliases.isEmpty()) {
                        for (PropertyName pn : aliases) {
                            this._propertyLookup.put(pn.getSimpleName(), prop);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < len; i++) {
            SettableBeanProperty prop2 = creatorProps[i];
            this._allProperties[i] = prop2;
            if (!prop2.isIgnorable()) {
                this._propertyLookup.put(prop2.getName(), prop2);
            }
        }
    }

    public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps, BeanPropertyMap allProperties) throws JsonMappingException {
        int len = srcCreatorProps.length;
        SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];
        for (int i = 0; i < len; i++) {
            SettableBeanProperty prop = srcCreatorProps[i];
            if (!prop.hasValueDeserializer() && !prop.isInjectionOnly()) {
                prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
            }
            creatorProps[i] = prop;
        }
        return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, allProperties.isCaseInsensitive(), true);
    }

    public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps, boolean caseInsensitive) throws JsonMappingException {
        int len = srcCreatorProps.length;
        SettableBeanProperty[] creatorProps = new SettableBeanProperty[len];
        for (int i = 0; i < len; i++) {
            SettableBeanProperty prop = srcCreatorProps[i];
            if (!prop.hasValueDeserializer()) {
                prop = prop.withValueDeserializer(ctxt.findContextualValueDeserializer(prop.getType(), prop));
            }
            creatorProps[i] = prop;
        }
        return new PropertyBasedCreator(ctxt, valueInstantiator, creatorProps, caseInsensitive, false);
    }

    @Deprecated
    public static PropertyBasedCreator construct(DeserializationContext ctxt, ValueInstantiator valueInstantiator, SettableBeanProperty[] srcCreatorProps) throws JsonMappingException {
        return construct(ctxt, valueInstantiator, srcCreatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
    }

    public Collection<SettableBeanProperty> properties() {
        return this._propertyLookup.values();
    }

    public SettableBeanProperty findCreatorProperty(String name) {
        return this._propertyLookup.get(name);
    }

    public SettableBeanProperty findCreatorProperty(int propertyIndex) {
        for (SettableBeanProperty prop : this._propertyLookup.values()) {
            if (prop.getPropertyIndex() == propertyIndex) {
                return prop;
            }
        }
        return null;
    }

    public PropertyValueBuffer startBuilding(JsonParser p, DeserializationContext ctxt, ObjectIdReader oir) {
        return new PropertyValueBuffer(p, ctxt, this._propertyCount, oir);
    }

    public Object build(DeserializationContext ctxt, PropertyValueBuffer buffer) throws IOException {
        Object bean = this._valueInstantiator.createFromObjectWith(ctxt, this._allProperties, buffer);
        if (bean != null) {
            bean = buffer.handleIdValue(ctxt, bean);
            PropertyValue buffered = buffer.buffered();
            while (true) {
                PropertyValue pv = buffered;
                if (pv == null) {
                    break;
                }
                pv.assign(bean);
                buffered = pv.next;
            }
        }
        return bean;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/PropertyBasedCreator$CaseInsensitiveMap.class */
    static class CaseInsensitiveMap extends HashMap<String, SettableBeanProperty> {
        private static final long serialVersionUID = 1;
        protected final Locale _locale;

        @Deprecated
        public CaseInsensitiveMap() {
            this(Locale.getDefault());
        }

        public CaseInsensitiveMap(Locale l) {
            this._locale = l;
        }

        public static CaseInsensitiveMap construct(Locale l) {
            return new CaseInsensitiveMap(l);
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public SettableBeanProperty get(Object key0) {
            return (SettableBeanProperty) super.get((Object) ((String) key0).toLowerCase(this._locale));
        }

        @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
        public SettableBeanProperty put(String key, SettableBeanProperty value) {
            return (SettableBeanProperty) super.put((CaseInsensitiveMap) key.toLowerCase(this._locale), (String) value);
        }
    }
}
