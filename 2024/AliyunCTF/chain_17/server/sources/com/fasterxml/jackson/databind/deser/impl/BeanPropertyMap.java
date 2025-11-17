package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/BeanPropertyMap.class */
public class BeanPropertyMap implements Iterable<SettableBeanProperty>, Serializable {
    private static final long serialVersionUID = 2;
    protected final boolean _caseInsensitive;
    private int _hashMask;
    private int _size;
    private int _spillCount;
    private Object[] _hashArea;
    private final SettableBeanProperty[] _propsInOrder;
    private final Map<String, List<PropertyName>> _aliasDefs;
    private final Map<String, String> _aliasMapping;
    private final Locale _locale;

    public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasDefs, Locale locale) {
        this._caseInsensitive = caseInsensitive;
        this._propsInOrder = (SettableBeanProperty[]) props.toArray(new SettableBeanProperty[props.size()]);
        this._aliasDefs = aliasDefs;
        this._locale = locale;
        this._aliasMapping = _buildAliasMapping(aliasDefs, caseInsensitive, locale);
        init(props);
    }

    @Deprecated
    public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasDefs) {
        this(caseInsensitive, props, aliasDefs, Locale.getDefault());
    }

    private BeanPropertyMap(BeanPropertyMap src, SettableBeanProperty newProp, int hashIndex, int orderedIndex) {
        this._caseInsensitive = src._caseInsensitive;
        this._locale = src._locale;
        this._hashMask = src._hashMask;
        this._size = src._size;
        this._spillCount = src._spillCount;
        this._aliasDefs = src._aliasDefs;
        this._aliasMapping = src._aliasMapping;
        this._hashArea = Arrays.copyOf(src._hashArea, src._hashArea.length);
        this._propsInOrder = (SettableBeanProperty[]) Arrays.copyOf(src._propsInOrder, src._propsInOrder.length);
        this._hashArea[hashIndex] = newProp;
        this._propsInOrder[orderedIndex] = newProp;
    }

    private BeanPropertyMap(BeanPropertyMap src, SettableBeanProperty newProp, String key, int slot) {
        this._caseInsensitive = src._caseInsensitive;
        this._locale = src._locale;
        this._hashMask = src._hashMask;
        this._size = src._size;
        this._spillCount = src._spillCount;
        this._aliasDefs = src._aliasDefs;
        this._aliasMapping = src._aliasMapping;
        this._hashArea = Arrays.copyOf(src._hashArea, src._hashArea.length);
        int last = src._propsInOrder.length;
        this._propsInOrder = (SettableBeanProperty[]) Arrays.copyOf(src._propsInOrder, last + 1);
        this._propsInOrder[last] = newProp;
        int hashSize = this._hashMask + 1;
        int ix = slot << 1;
        if (this._hashArea[ix] != null) {
            ix = (hashSize + (slot >> 1)) << 1;
            if (this._hashArea[ix] != null) {
                ix = ((hashSize + (hashSize >> 1)) << 1) + this._spillCount;
                this._spillCount += 2;
                if (ix >= this._hashArea.length) {
                    this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length + 4);
                }
            }
        }
        this._hashArea[ix] = key;
        this._hashArea[ix + 1] = newProp;
    }

    protected BeanPropertyMap(BeanPropertyMap base, boolean caseInsensitive) {
        this._caseInsensitive = caseInsensitive;
        this._locale = base._locale;
        this._aliasDefs = base._aliasDefs;
        this._aliasMapping = base._aliasMapping;
        this._propsInOrder = (SettableBeanProperty[]) Arrays.copyOf(base._propsInOrder, base._propsInOrder.length);
        init(Arrays.asList(this._propsInOrder));
    }

    public BeanPropertyMap withCaseInsensitivity(boolean state) {
        if (this._caseInsensitive == state) {
            return this;
        }
        return new BeanPropertyMap(this, state);
    }

    protected void init(Collection<SettableBeanProperty> props) {
        this._size = props.size();
        int hashSize = findSize(this._size);
        this._hashMask = hashSize - 1;
        int alloc = (hashSize + (hashSize >> 1)) * 2;
        Object[] hashed = new Object[alloc];
        int spillCount = 0;
        for (SettableBeanProperty prop : props) {
            if (prop != null) {
                String key = getPropertyName(prop);
                int slot = _hashCode(key);
                int ix = slot << 1;
                if (hashed[ix] != null) {
                    ix = (hashSize + (slot >> 1)) << 1;
                    if (hashed[ix] != null) {
                        ix = ((hashSize + (hashSize >> 1)) << 1) + spillCount;
                        spillCount += 2;
                        if (ix >= hashed.length) {
                            hashed = Arrays.copyOf(hashed, hashed.length + 4);
                        }
                    }
                }
                hashed[ix] = key;
                hashed[ix + 1] = prop;
            }
        }
        this._hashArea = hashed;
        this._spillCount = spillCount;
    }

    private static final int findSize(int size) {
        if (size <= 5) {
            return 8;
        }
        if (size <= 12) {
            return 16;
        }
        int needed = size + (size >> 2);
        int i = 32;
        while (true) {
            int result = i;
            if (result < needed) {
                i = result + result;
            } else {
                return result;
            }
        }
    }

    public static BeanPropertyMap construct(MapperConfig<?> config, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasMapping, boolean caseInsensitive) {
        return new BeanPropertyMap(caseInsensitive, props, aliasMapping, config.getLocale());
    }

    @Deprecated
    public static BeanPropertyMap construct(MapperConfig<?> config, Collection<SettableBeanProperty> props, Map<String, List<PropertyName>> aliasMapping) {
        return new BeanPropertyMap(config.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES), props, aliasMapping, config.getLocale());
    }

    @Deprecated
    public static BeanPropertyMap construct(Collection<SettableBeanProperty> props, boolean caseInsensitive, Map<String, List<PropertyName>> aliasMapping) {
        return new BeanPropertyMap(caseInsensitive, props, aliasMapping);
    }

    public BeanPropertyMap withProperty(SettableBeanProperty newProp) {
        String key = getPropertyName(newProp);
        int end = this._hashArea.length;
        for (int i = 1; i < end; i += 2) {
            SettableBeanProperty prop = (SettableBeanProperty) this._hashArea[i];
            if (prop != null && prop.getName().equals(key)) {
                return new BeanPropertyMap(this, newProp, i, _findFromOrdered(prop));
            }
        }
        int slot = _hashCode(key);
        return new BeanPropertyMap(this, newProp, key, slot);
    }

    public BeanPropertyMap assignIndexes() {
        int index = 0;
        int end = this._hashArea.length;
        for (int i = 1; i < end; i += 2) {
            SettableBeanProperty prop = (SettableBeanProperty) this._hashArea[i];
            if (prop != null) {
                int i2 = index;
                index++;
                prop.assignIndex(i2);
            }
        }
        return this;
    }

    public BeanPropertyMap renameAll(NameTransformer transformer) {
        if (transformer == null || transformer == NameTransformer.NOP) {
            return this;
        }
        int len = this._propsInOrder.length;
        ArrayList<SettableBeanProperty> newProps = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            SettableBeanProperty prop = this._propsInOrder[i];
            if (prop == null) {
                newProps.add(prop);
            } else {
                newProps.add(_rename(prop, transformer));
            }
        }
        return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs, this._locale);
    }

    public BeanPropertyMap withoutProperties(Collection<String> toExclude) {
        return withoutProperties(toExclude, null);
    }

    public BeanPropertyMap withoutProperties(Collection<String> toExclude, Collection<String> toInclude) {
        if ((toExclude == null || toExclude.isEmpty()) && toInclude == null) {
            return this;
        }
        int len = this._propsInOrder.length;
        ArrayList<SettableBeanProperty> newProps = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            SettableBeanProperty prop = this._propsInOrder[i];
            if (prop != null && !IgnorePropertiesUtil.shouldIgnore(prop.getName(), toExclude, toInclude)) {
                newProps.add(prop);
            }
        }
        return new BeanPropertyMap(this._caseInsensitive, newProps, this._aliasDefs, this._locale);
    }

    public void replace(SettableBeanProperty origProp, SettableBeanProperty newProp) {
        int end = this._hashArea.length;
        for (int i = 1; i < end; i += 2) {
            if (this._hashArea[i] == origProp) {
                this._hashArea[i] = newProp;
                this._propsInOrder[_findFromOrdered(origProp)] = newProp;
                return;
            }
        }
        throw new NoSuchElementException("No entry '" + origProp.getName() + "' found, can't replace");
    }

    public void remove(SettableBeanProperty propToRm) {
        ArrayList<SettableBeanProperty> props = new ArrayList<>(this._size);
        String key = getPropertyName(propToRm);
        boolean found = false;
        int end = this._hashArea.length;
        for (int i = 1; i < end; i += 2) {
            SettableBeanProperty prop = (SettableBeanProperty) this._hashArea[i];
            if (prop != null) {
                if (!found) {
                    found = key.equals(this._hashArea[i - 1]);
                    if (found) {
                        this._propsInOrder[_findFromOrdered(prop)] = null;
                    }
                }
                props.add(prop);
            }
        }
        if (!found) {
            throw new NoSuchElementException("No entry '" + propToRm.getName() + "' found, can't remove");
        }
        init(props);
    }

    public int size() {
        return this._size;
    }

    public boolean isCaseInsensitive() {
        return this._caseInsensitive;
    }

    public boolean hasAliases() {
        return !this._aliasDefs.isEmpty();
    }

    @Override // java.lang.Iterable
    public Iterator<SettableBeanProperty> iterator() {
        return _properties().iterator();
    }

    private List<SettableBeanProperty> _properties() {
        ArrayList<SettableBeanProperty> p = new ArrayList<>(this._size);
        int end = this._hashArea.length;
        for (int i = 1; i < end; i += 2) {
            SettableBeanProperty prop = (SettableBeanProperty) this._hashArea[i];
            if (prop != null) {
                p.add(prop);
            }
        }
        return p;
    }

    public SettableBeanProperty[] getPropertiesInInsertionOrder() {
        return this._propsInOrder;
    }

    protected final String getPropertyName(SettableBeanProperty prop) {
        return this._caseInsensitive ? prop.getName().toLowerCase(this._locale) : prop.getName();
    }

    public SettableBeanProperty find(int index) {
        int end = this._hashArea.length;
        for (int i = 1; i < end; i += 2) {
            SettableBeanProperty prop = (SettableBeanProperty) this._hashArea[i];
            if (prop != null && index == prop.getPropertyIndex()) {
                return prop;
            }
        }
        return null;
    }

    public SettableBeanProperty find(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot pass null property name");
        }
        if (this._caseInsensitive) {
            key = key.toLowerCase(this._locale);
        }
        int slot = key.hashCode() & this._hashMask;
        int ix = slot << 1;
        Object match = this._hashArea[ix];
        if (match == key || key.equals(match)) {
            return (SettableBeanProperty) this._hashArea[ix + 1];
        }
        return _find2(key, slot, match);
    }

    private final SettableBeanProperty _find2(String key, int slot, Object match) {
        if (match == null) {
            return _findWithAlias(this._aliasMapping.get(key));
        }
        int hashSize = this._hashMask + 1;
        int ix = (hashSize + (slot >> 1)) << 1;
        Object match2 = this._hashArea[ix];
        if (key.equals(match2)) {
            return (SettableBeanProperty) this._hashArea[ix + 1];
        }
        if (match2 != null) {
            int i = (hashSize + (hashSize >> 1)) << 1;
            int end = i + this._spillCount;
            while (i < end) {
                Object match3 = this._hashArea[i];
                if (match3 != key && !key.equals(match3)) {
                    i += 2;
                } else {
                    return (SettableBeanProperty) this._hashArea[i + 1];
                }
            }
        }
        return _findWithAlias(this._aliasMapping.get(key));
    }

    private SettableBeanProperty _findWithAlias(String keyFromAlias) {
        if (keyFromAlias == null) {
            return null;
        }
        int slot = _hashCode(keyFromAlias);
        int ix = slot << 1;
        Object match = this._hashArea[ix];
        if (keyFromAlias.equals(match)) {
            return (SettableBeanProperty) this._hashArea[ix + 1];
        }
        if (match == null) {
            return null;
        }
        return _find2ViaAlias(keyFromAlias, slot, match);
    }

    private SettableBeanProperty _find2ViaAlias(String key, int slot, Object match) {
        int hashSize = this._hashMask + 1;
        int ix = (hashSize + (slot >> 1)) << 1;
        Object match2 = this._hashArea[ix];
        if (key.equals(match2)) {
            return (SettableBeanProperty) this._hashArea[ix + 1];
        }
        if (match2 != null) {
            int i = (hashSize + (hashSize >> 1)) << 1;
            int end = i + this._spillCount;
            while (i < end) {
                Object match3 = this._hashArea[i];
                if (match3 != key && !key.equals(match3)) {
                    i += 2;
                } else {
                    return (SettableBeanProperty) this._hashArea[i + 1];
                }
            }
            return null;
        }
        return null;
    }

    public boolean findDeserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, String key) throws IOException {
        SettableBeanProperty prop = find(key);
        if (prop == null) {
            return false;
        }
        try {
            prop.deserializeAndSet(p, ctxt, bean);
            return true;
        } catch (Exception e) {
            wrapAndThrow(e, bean, key, ctxt);
            return true;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Properties=[");
        int count = 0;
        Iterator<SettableBeanProperty> it = iterator();
        while (it.hasNext()) {
            SettableBeanProperty prop = it.next();
            int i = count;
            count++;
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(prop.getName());
            sb.append('(');
            sb.append(prop.getType());
            sb.append(')');
        }
        sb.append(']');
        if (!this._aliasDefs.isEmpty()) {
            sb.append("(aliases: ");
            sb.append(this._aliasDefs);
            sb.append(")");
        }
        return sb.toString();
    }

    protected SettableBeanProperty _rename(SettableBeanProperty prop, NameTransformer xf) {
        JsonDeserializer<Object> newDeser;
        if (prop == null) {
            return prop;
        }
        String newName = xf.transform(prop.getName());
        SettableBeanProperty prop2 = prop.withSimpleName(newName);
        JsonDeserializer<Object> deser = prop2.getValueDeserializer();
        if (deser != null && (newDeser = deser.unwrappingDeserializer(xf)) != deser) {
            prop2 = prop2.withValueDeserializer(newDeser);
        }
        return prop2;
    }

    protected void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
        while ((t instanceof InvocationTargetException) && t.getCause() != null) {
            t = t.getCause();
        }
        ClassUtil.throwIfError(t);
        boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JacksonException)) {
                throw ((IOException) t);
            }
        } else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        throw JsonMappingException.wrapWithPath(t, bean, fieldName);
    }

    private final int _findFromOrdered(SettableBeanProperty prop) {
        int end = this._propsInOrder.length;
        for (int i = 0; i < end; i++) {
            if (this._propsInOrder[i] == prop) {
                return i;
            }
        }
        throw new IllegalStateException("Illegal state: property '" + prop.getName() + "' missing from _propsInOrder");
    }

    private final int _hashCode(String key) {
        return key.hashCode() & this._hashMask;
    }

    private Map<String, String> _buildAliasMapping(Map<String, List<PropertyName>> defs, boolean caseInsensitive, Locale loc) {
        if (defs == null || defs.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> aliases = new HashMap<>();
        for (Map.Entry<String, List<PropertyName>> entry : defs.entrySet()) {
            String key = entry.getKey();
            if (caseInsensitive) {
                key = key.toLowerCase(loc);
            }
            for (PropertyName pn : entry.getValue()) {
                String mapped = pn.getSimpleName();
                if (caseInsensitive) {
                    mapped = mapped.toLowerCase(loc);
                }
                aliases.put(mapped, key);
            }
        }
        return aliases;
    }
}
