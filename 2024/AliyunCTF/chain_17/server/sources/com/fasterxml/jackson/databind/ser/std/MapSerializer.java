package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

@JacksonStdImpl
/* loaded from: server.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/ser/std/MapSerializer.class */
public class MapSerializer extends ContainerSerializer<Map<?, ?>> implements ContextualSerializer {
    private static final long serialVersionUID = 1;
    protected static final JavaType UNSPECIFIED_TYPE = TypeFactory.unknownType();
    public static final Object MARKER_FOR_EMPTY = JsonInclude.Include.NON_EMPTY;
    protected final BeanProperty _property;
    protected final boolean _valueTypeIsStatic;
    protected final JavaType _keyType;
    protected final JavaType _valueType;
    protected JsonSerializer<Object> _keySerializer;
    protected JsonSerializer<Object> _valueSerializer;
    protected final TypeSerializer _valueTypeSerializer;
    protected PropertySerializerMap _dynamicValueSerializers;
    protected final Set<String> _ignoredEntries;
    protected final Set<String> _includedEntries;
    protected final Object _filterId;
    protected final Object _suppressableValue;
    protected final boolean _suppressNulls;
    protected final IgnorePropertiesUtil.Checker _inclusionChecker;
    protected final boolean _sortKeys;

    protected MapSerializer(Set<String> ignoredEntries, Set<String> includedEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer) {
        super(Map.class, false);
        this._ignoredEntries = (ignoredEntries == null || ignoredEntries.isEmpty()) ? null : ignoredEntries;
        this._includedEntries = includedEntries;
        this._keyType = keyType;
        this._valueType = valueType;
        this._valueTypeIsStatic = valueTypeIsStatic;
        this._valueTypeSerializer = vts;
        this._keySerializer = keySerializer;
        this._valueSerializer = valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._property = null;
        this._filterId = null;
        this._sortKeys = false;
        this._suppressableValue = null;
        this._suppressNulls = false;
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignoredEntries, this._includedEntries);
    }

    @Deprecated
    protected MapSerializer(Set<String> ignoredEntries, JavaType keyType, JavaType valueType, boolean valueTypeIsStatic, TypeSerializer vts, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer) {
        this(ignoredEntries, null, keyType, valueType, valueTypeIsStatic, vts, keySerializer, valueSerializer);
    }

    protected MapSerializer(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries, Set<String> includedEntries) {
        super(Map.class, false);
        this._ignoredEntries = (ignoredEntries == null || ignoredEntries.isEmpty()) ? null : ignoredEntries;
        this._includedEntries = includedEntries;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._keySerializer = keySerializer;
        this._valueSerializer = valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._property = property;
        this._filterId = src._filterId;
        this._sortKeys = src._sortKeys;
        this._suppressableValue = src._suppressableValue;
        this._suppressNulls = src._suppressNulls;
        this._inclusionChecker = IgnorePropertiesUtil.buildCheckerIfNeeded(this._ignoredEntries, this._includedEntries);
    }

    @Deprecated
    protected MapSerializer(MapSerializer src, BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignoredEntries) {
        this(src, property, keySerializer, valueSerializer, ignoredEntries, null);
    }

    protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue, boolean suppressNulls) {
        super(Map.class, false);
        this._ignoredEntries = src._ignoredEntries;
        this._includedEntries = src._includedEntries;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = vts;
        this._keySerializer = src._keySerializer;
        this._valueSerializer = src._valueSerializer;
        this._dynamicValueSerializers = src._dynamicValueSerializers;
        this._property = src._property;
        this._filterId = src._filterId;
        this._sortKeys = src._sortKeys;
        this._suppressableValue = suppressableValue;
        this._suppressNulls = suppressNulls;
        this._inclusionChecker = src._inclusionChecker;
    }

    protected MapSerializer(MapSerializer src, Object filterId, boolean sortKeys) {
        super(Map.class, false);
        this._ignoredEntries = src._ignoredEntries;
        this._includedEntries = src._includedEntries;
        this._keyType = src._keyType;
        this._valueType = src._valueType;
        this._valueTypeIsStatic = src._valueTypeIsStatic;
        this._valueTypeSerializer = src._valueTypeSerializer;
        this._keySerializer = src._keySerializer;
        this._valueSerializer = src._valueSerializer;
        this._dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
        this._property = src._property;
        this._filterId = filterId;
        this._sortKeys = sortKeys;
        this._suppressableValue = src._suppressableValue;
        this._suppressNulls = src._suppressNulls;
        this._inclusionChecker = src._inclusionChecker;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public MapSerializer _withValueTypeSerializer(TypeSerializer vts) {
        if (this._valueTypeSerializer == vts) {
            return this;
        }
        _ensureOverride("_withValueTypeSerializer");
        return new MapSerializer(this, vts, this._suppressableValue, this._suppressNulls);
    }

    public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, Set<String> included, boolean sortKeys) {
        _ensureOverride("withResolved");
        MapSerializer ser = new MapSerializer(this, property, keySerializer, valueSerializer, ignored, included);
        if (sortKeys != ser._sortKeys) {
            ser = new MapSerializer(ser, this._filterId, sortKeys);
        }
        return ser;
    }

    public MapSerializer withResolved(BeanProperty property, JsonSerializer<?> keySerializer, JsonSerializer<?> valueSerializer, Set<String> ignored, boolean sortKeys) {
        return withResolved(property, keySerializer, valueSerializer, ignored, null, sortKeys);
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public MapSerializer withFilterId(Object filterId) {
        if (this._filterId == filterId) {
            return this;
        }
        _ensureOverride("withFilterId");
        return new MapSerializer(this, filterId, this._sortKeys);
    }

    public MapSerializer withContentInclusion(Object suppressableValue, boolean suppressNulls) {
        if (suppressableValue == this._suppressableValue && suppressNulls == this._suppressNulls) {
            return this;
        }
        _ensureOverride("withContentInclusion");
        return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, suppressNulls);
    }

    public static MapSerializer construct(Set<String> ignoredEntries, Set<String> includedEntries, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
        JavaType keyType;
        JavaType valueType;
        if (mapType == null) {
            JavaType javaType = UNSPECIFIED_TYPE;
            valueType = javaType;
            keyType = javaType;
        } else {
            keyType = mapType.getKeyType();
            if (mapType.hasRawClass(Properties.class)) {
                valueType = TypeFactory.unknownType();
            } else {
                valueType = mapType.getContentType();
            }
        }
        if (!staticValueType) {
            staticValueType = valueType != null && valueType.isFinal();
        } else if (valueType.getRawClass() == Object.class) {
            staticValueType = false;
        }
        MapSerializer ser = new MapSerializer(ignoredEntries, includedEntries, keyType, valueType, staticValueType, vts, keySerializer, valueSerializer);
        if (filterId != null) {
            ser = ser.withFilterId(filterId);
        }
        return ser;
    }

    public static MapSerializer construct(Set<String> ignoredEntries, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
        return construct(ignoredEntries, null, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
    }

    protected void _ensureOverride(String method) {
        ClassUtil.verifyMustOverride(MapSerializer.class, this, method);
    }

    @Deprecated
    protected void _ensureOverride() {
        _ensureOverride("N/A");
    }

    @Deprecated
    protected MapSerializer(MapSerializer src, TypeSerializer vts, Object suppressableValue) {
        this(src, vts, suppressableValue, false);
    }

    @Deprecated
    public MapSerializer withContentInclusion(Object suppressableValue) {
        return new MapSerializer(this, this._valueTypeSerializer, suppressableValue, this._suppressNulls);
    }

    @Deprecated
    public static MapSerializer construct(String[] ignoredList, JavaType mapType, boolean staticValueType, TypeSerializer vts, JsonSerializer<Object> keySerializer, JsonSerializer<Object> valueSerializer, Object filterId) {
        Set<String> ignoredEntries = ArrayBuilders.arrayToSet(ignoredList);
        return construct(ignoredEntries, mapType, staticValueType, vts, keySerializer, valueSerializer, filterId);
    }

    @Override // com.fasterxml.jackson.databind.ser.ContextualSerializer
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> keySer;
        JsonInclude.Include incl;
        Object valueToSuppress;
        boolean suppressNulls;
        Object filterId;
        Boolean B;
        JsonSerializer<?> ser = null;
        JsonSerializer<?> keySer2 = null;
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        AnnotatedMember propertyAcc = property == null ? null : property.getMember();
        if (_neitherNull(propertyAcc, intr)) {
            Object serDef = intr.findKeySerializer(propertyAcc);
            if (serDef != null) {
                keySer2 = provider.serializerInstance(propertyAcc, serDef);
            }
            Object serDef2 = intr.findContentSerializer(propertyAcc);
            if (serDef2 != null) {
                ser = provider.serializerInstance(propertyAcc, serDef2);
            }
        }
        if (ser == null) {
            ser = this._valueSerializer;
        }
        JsonSerializer<?> ser2 = findContextualConvertingSerializer(provider, property, ser);
        if (ser2 == null && this._valueTypeIsStatic && !this._valueType.isJavaLangObject()) {
            ser2 = provider.findContentValueSerializer(this._valueType, property);
        }
        if (keySer2 == null) {
            keySer2 = this._keySerializer;
        }
        if (keySer2 == null) {
            keySer = provider.findKeySerializer(this._keyType, property);
        } else {
            keySer = provider.handleSecondaryContextualization(keySer2, property);
        }
        Set<String> ignored = this._ignoredEntries;
        Set<String> included = this._includedEntries;
        boolean sortKeys = false;
        if (_neitherNull(propertyAcc, intr)) {
            SerializationConfig config = provider.getConfig();
            Set<String> newIgnored = intr.findPropertyIgnoralByName(config, propertyAcc).findIgnoredForSerialization();
            if (_nonEmpty(newIgnored)) {
                ignored = ignored == null ? new HashSet() : new HashSet(ignored);
                for (String str : newIgnored) {
                    ignored.add(str);
                }
            }
            Set<String> newIncluded = intr.findPropertyInclusionByName(config, propertyAcc).getIncluded();
            if (newIncluded != null) {
                included = included == null ? new HashSet() : new HashSet(included);
                for (String str2 : newIncluded) {
                    included.add(str2);
                }
            }
            Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
            sortKeys = Boolean.TRUE.equals(b);
        }
        JsonFormat.Value format = findFormatOverrides(provider, property, Map.class);
        if (format != null && (B = format.getFeature(JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES)) != null) {
            sortKeys = B.booleanValue();
        }
        MapSerializer mser = withResolved(property, keySer, ser2, ignored, included, sortKeys);
        if (propertyAcc != null && (filterId = intr.findFilterId(propertyAcc)) != null) {
            mser = mser.withFilterId(filterId);
        }
        JsonInclude.Value inclV = findIncludeOverrides(provider, property, Map.class);
        if (inclV != null && (incl = inclV.getContentInclusion()) != JsonInclude.Include.USE_DEFAULTS) {
            switch (incl) {
                case NON_DEFAULT:
                    valueToSuppress = BeanUtil.getDefaultValue(this._valueType);
                    suppressNulls = true;
                    if (valueToSuppress != null && valueToSuppress.getClass().isArray()) {
                        valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                        break;
                    }
                    break;
                case NON_ABSENT:
                    suppressNulls = true;
                    valueToSuppress = this._valueType.isReferenceType() ? MARKER_FOR_EMPTY : null;
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
            mser = mser.withContentInclusion(valueToSuppress, suppressNulls);
        }
        return mser;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public JavaType getContentType() {
        return this._valueType;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public JsonSerializer<?> getContentSerializer() {
        return this._valueSerializer;
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public boolean isEmpty(SerializerProvider prov, Map<?, ?> value) {
        if (value.isEmpty()) {
            return true;
        }
        Object supp = this._suppressableValue;
        if (supp == null && !this._suppressNulls) {
            return false;
        }
        JsonSerializer<Object> valueSer = this._valueSerializer;
        boolean checkEmpty = MARKER_FOR_EMPTY == supp;
        if (valueSer != null) {
            for (Object elemValue : value.values()) {
                if (elemValue == null) {
                    if (!this._suppressNulls) {
                        return false;
                    }
                } else if (checkEmpty) {
                    if (!valueSer.isEmpty(prov, elemValue)) {
                        return false;
                    }
                } else if (supp == null || !supp.equals(value)) {
                    return false;
                }
            }
            return true;
        }
        for (Object elemValue2 : value.values()) {
            if (elemValue2 == null) {
                if (!this._suppressNulls) {
                    return false;
                }
            } else {
                try {
                    JsonSerializer<Object> valueSer2 = _findSerializer(prov, elemValue2);
                    if (checkEmpty) {
                        if (!valueSer2.isEmpty(prov, elemValue2)) {
                            return false;
                        }
                    } else if (supp == null || !supp.equals(value)) {
                        return false;
                    }
                } catch (DatabindException e) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override // com.fasterxml.jackson.databind.ser.ContainerSerializer
    public boolean hasSingleElement(Map<?, ?> value) {
        return value.size() == 1;
    }

    public JsonSerializer<?> getKeySerializer() {
        return this._keySerializer;
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer
    public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject(value);
        serializeWithoutTypeInfo(value, gen, provider);
        gen.writeEndObject();
    }

    @Override // com.fasterxml.jackson.databind.JsonSerializer
    public void serializeWithType(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeWithoutTypeInfo(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    public void serializeWithoutTypeInfo(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        PropertyFilter pf;
        if (!value.isEmpty()) {
            if (this._sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = _orderEntries(value, gen, provider);
            }
            if (this._filterId != null && (pf = findPropertyFilter(provider, this._filterId, value)) != null) {
                serializeFilteredFields(value, gen, provider, pf, this._suppressableValue);
                return;
            }
            if (this._suppressableValue != null || this._suppressNulls) {
                serializeOptionalFields(value, gen, provider, this._suppressableValue);
            } else if (this._valueSerializer != null) {
                serializeFieldsUsing(value, gen, provider, this._valueSerializer);
            } else {
                serializeFields(value, gen, provider);
            }
        }
    }

    public void serializeFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (this._valueTypeSerializer != null) {
            serializeTypedFields(value, gen, provider, null);
            return;
        }
        JsonSerializer<Object> keySerializer = this._keySerializer;
        Object keyElem = null;
        try {
            for (Map.Entry<?, ?> entry : value.entrySet()) {
                Object valueElem = entry.getValue();
                keyElem = entry.getKey();
                if (keyElem == null) {
                    provider.findNullKeySerializer(this._keyType, this._property).serialize(null, gen, provider);
                } else if (this._inclusionChecker == null || !this._inclusionChecker.shouldIgnore(keyElem)) {
                    keySerializer.serialize(keyElem, gen, provider);
                }
                if (valueElem == null) {
                    provider.defaultSerializeNull(gen);
                } else {
                    JsonSerializer<Object> serializer = this._valueSerializer;
                    if (serializer == null) {
                        serializer = _findSerializer(provider, valueElem);
                    }
                    serializer.serialize(valueElem, gen, provider);
                }
            }
        } catch (Exception e) {
            wrapAndThrow(provider, e, value, String.valueOf(keyElem));
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(9:12|(2:54|55)(2:14|(1:19)(2:52|34))|20|(3:46|47|(2:51|34)(2:49|50))(5:22|23|(1:25)|26|(3:42|43|(2:45|34))(3:28|29|(2:33|34)))|35|36|38|34|10) */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00ed, code lost:            r18 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x00ef, code lost:            wrapAndThrow(r9, r18, r7, java.lang.String.valueOf(r0));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void serializeOptionalFields(java.util.Map<?, ?> r7, com.fasterxml.jackson.core.JsonGenerator r8, com.fasterxml.jackson.databind.SerializerProvider r9, java.lang.Object r10) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 256
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.ser.std.MapSerializer.serializeOptionalFields(java.util.Map, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider, java.lang.Object):void");
    }

    public void serializeFieldsUsing(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, JsonSerializer<Object> ser) throws IOException {
        JsonSerializer<Object> keySerializer = this._keySerializer;
        TypeSerializer typeSer = this._valueTypeSerializer;
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object keyElem = entry.getKey();
            if (this._inclusionChecker == null || !this._inclusionChecker.shouldIgnore(keyElem)) {
                if (keyElem == null) {
                    provider.findNullKeySerializer(this._keyType, this._property).serialize(null, gen, provider);
                } else {
                    keySerializer.serialize(keyElem, gen, provider);
                }
                Object valueElem = entry.getValue();
                if (valueElem == null) {
                    provider.defaultSerializeNull(gen);
                } else if (typeSer == null) {
                    try {
                        ser.serialize(valueElem, gen, provider);
                    } catch (Exception e) {
                        wrapAndThrow(provider, e, value, String.valueOf(keyElem));
                    }
                } else {
                    ser.serializeWithType(valueElem, gen, provider, typeSer);
                }
            }
        }
    }

    public void serializeFilteredFields(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider, PropertyFilter filter, Object suppressableValue) throws IOException {
        JsonSerializer<Object> keySerializer;
        JsonSerializer<Object> valueSer;
        MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
        boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object keyElem = entry.getKey();
            if (this._inclusionChecker == null || !this._inclusionChecker.shouldIgnore(keyElem)) {
                if (keyElem == null) {
                    keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
                } else {
                    keySerializer = this._keySerializer;
                }
                Object valueElem = entry.getValue();
                if (valueElem == null) {
                    if (!this._suppressNulls) {
                        valueSer = provider.getDefaultNullValueSerializer();
                    }
                } else {
                    valueSer = this._valueSerializer;
                    if (valueSer == null) {
                        valueSer = _findSerializer(provider, valueElem);
                    }
                    if (checkEmpty) {
                        if (valueSer.isEmpty(provider, valueElem)) {
                        }
                    } else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                    }
                }
                prop.reset(keyElem, valueElem, keySerializer, valueSer);
                try {
                    filter.serializeAsField(value, gen, provider, prop);
                } catch (Exception e) {
                    wrapAndThrow(provider, e, value, String.valueOf(keyElem));
                }
            }
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(10:7|(2:53|54)(2:9|(1:14)(2:51|34))|15|(3:45|46|(2:50|34)(2:48|49))(5:17|18|(1:20)|21|(3:40|41|(2:44|34)(1:43))(3:23|24|(2:38|34)))|29|30|31|33|34|5) */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00e0, code lost:            r18 = move-exception;     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00e2, code lost:            wrapAndThrow(r9, r18, r7, java.lang.String.valueOf(r0));     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void serializeTypedFields(java.util.Map<?, ?> r7, com.fasterxml.jackson.core.JsonGenerator r8, com.fasterxml.jackson.databind.SerializerProvider r9, java.lang.Object r10) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 243
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.ser.std.MapSerializer.serializeTypedFields(java.util.Map, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider, java.lang.Object):void");
    }

    public void serializeFilteredAnyProperties(SerializerProvider provider, JsonGenerator gen, Object bean, Map<?, ?> value, PropertyFilter filter, Object suppressableValue) throws IOException {
        JsonSerializer<Object> keySerializer;
        JsonSerializer<Object> valueSer;
        MapProperty prop = new MapProperty(this._valueTypeSerializer, this._property);
        boolean checkEmpty = MARKER_FOR_EMPTY == suppressableValue;
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object keyElem = entry.getKey();
            if (this._inclusionChecker == null || !this._inclusionChecker.shouldIgnore(keyElem)) {
                if (keyElem == null) {
                    keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
                } else {
                    keySerializer = this._keySerializer;
                }
                Object valueElem = entry.getValue();
                if (valueElem == null) {
                    if (!this._suppressNulls) {
                        valueSer = provider.getDefaultNullValueSerializer();
                    }
                } else {
                    valueSer = this._valueSerializer;
                    if (valueSer == null) {
                        valueSer = _findSerializer(provider, valueElem);
                    }
                    if (checkEmpty) {
                        if (valueSer.isEmpty(provider, valueElem)) {
                        }
                    } else if (suppressableValue != null && suppressableValue.equals(valueElem)) {
                    }
                }
                prop.reset(keyElem, valueElem, keySerializer, valueSer);
                try {
                    filter.serializeAsField(bean, gen, provider, prop);
                } catch (Exception e) {
                    wrapAndThrow(provider, e, value, String.valueOf(keyElem));
                }
            }
        }
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.jsonschema.SchemaAware
    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return createSchemaNode("object", true);
    }

    @Override // com.fasterxml.jackson.databind.ser.std.StdSerializer, com.fasterxml.jackson.databind.JsonSerializer, com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonMapFormatVisitor v2 = visitor.expectMapFormat(typeHint);
        if (v2 != null) {
            v2.keyFormat(this._keySerializer, this._keyType);
            JsonSerializer<?> valueSer = this._valueSerializer;
            if (valueSer == null) {
                valueSer = _findAndAddDynamic(this._dynamicValueSerializers, this._valueType, visitor.getProvider());
            }
            v2.valueFormat(valueSer, this._valueType);
        }
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, Class<?> type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map, JavaType type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, this._property);
        if (map != result.map) {
            this._dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }

    protected Map<?, ?> _orderEntries(Map<?, ?> input, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (input instanceof SortedMap) {
            return input;
        }
        if (_hasNullKey(input)) {
            TreeMap<Object, Object> result = new TreeMap<>();
            for (Map.Entry<?, ?> entry : input.entrySet()) {
                Object key = entry.getKey();
                if (key == null) {
                    _writeNullKeyedEntry(gen, provider, entry.getValue());
                } else {
                    result.put(key, entry.getValue());
                }
            }
            return result;
        }
        return new TreeMap(input);
    }

    protected boolean _hasNullKey(Map<?, ?> input) {
        return (input instanceof HashMap) && input.containsKey(null);
    }

    protected void _writeNullKeyedEntry(JsonGenerator gen, SerializerProvider provider, Object value) throws IOException {
        JsonSerializer<Object> valueSer;
        JsonSerializer<Object> keySerializer = provider.findNullKeySerializer(this._keyType, this._property);
        if (value == null) {
            if (this._suppressNulls) {
                return;
            } else {
                valueSer = provider.getDefaultNullValueSerializer();
            }
        } else {
            valueSer = this._valueSerializer;
            if (valueSer == null) {
                valueSer = _findSerializer(provider, value);
            }
            if (this._suppressableValue == MARKER_FOR_EMPTY) {
                if (valueSer.isEmpty(provider, value)) {
                    return;
                }
            } else if (this._suppressableValue != null && this._suppressableValue.equals(value)) {
                return;
            }
        }
        try {
            keySerializer.serialize(null, gen, provider);
            valueSer.serialize(value, gen, provider);
        } catch (Exception e) {
            wrapAndThrow(provider, e, value, "");
        }
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider, Object value) throws JsonMappingException {
        Class<?> cc = value.getClass();
        JsonSerializer<Object> valueSer = this._dynamicValueSerializers.serializerFor(cc);
        if (valueSer != null) {
            return valueSer;
        }
        if (this._valueType.hasGenericTypes()) {
            return _findAndAddDynamic(this._dynamicValueSerializers, provider.constructSpecializedType(this._valueType, cc), provider);
        }
        return _findAndAddDynamic(this._dynamicValueSerializers, cc, provider);
    }
}
