package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/JavaUtilCollectionsDeserializers.class */
public abstract class JavaUtilCollectionsDeserializers {
    private static final int TYPE_SINGLETON_SET = 1;
    private static final int TYPE_SINGLETON_LIST = 2;
    private static final int TYPE_SINGLETON_MAP = 3;
    private static final int TYPE_UNMODIFIABLE_SET = 4;
    private static final int TYPE_UNMODIFIABLE_LIST = 5;
    private static final int TYPE_UNMODIFIABLE_MAP = 6;
    private static final int TYPE_SYNC_SET = 7;
    private static final int TYPE_SYNC_COLLECTION = 8;
    private static final int TYPE_SYNC_LIST = 9;
    private static final int TYPE_SYNC_MAP = 10;
    public static final int TYPE_AS_LIST = 11;
    private static final String PREFIX_JAVA_UTIL_COLLECTIONS = "java.util.Collections$";
    private static final String PREFIX_JAVA_UTIL_ARRAYS = "java.util.Arrays$";
    private static final String PREFIX_JAVA_UTIL_IMMUTABLE_COLL = "java.util.ImmutableCollections$";

    public static JsonDeserializer<?> findForCollection(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        String clsName = type.getRawClass().getName();
        if (!clsName.startsWith("java.util.")) {
            return null;
        }
        String localName = _findUtilCollectionsTypeName(clsName);
        if (localName != null) {
            JavaUtilCollectionsConverter conv = null;
            String name = _findUnmodifiableTypeName(localName);
            if (name != null) {
                if (name.endsWith("Set")) {
                    conv = converter(4, type, Set.class);
                } else if (name.endsWith("List")) {
                    conv = converter(5, type, List.class);
                }
            } else {
                String name2 = _findSingletonTypeName(localName);
                if (name2 != null) {
                    if (name2.endsWith("Set")) {
                        conv = converter(1, type, Set.class);
                    } else if (name2.endsWith("List")) {
                        conv = converter(2, type, List.class);
                    }
                } else {
                    String name3 = _findSyncTypeName(localName);
                    if (name3 != null) {
                        if (name3.endsWith("Set")) {
                            conv = converter(7, type, Set.class);
                        } else if (name3.endsWith("List")) {
                            conv = converter(9, type, List.class);
                        } else if (name3.endsWith("Collection")) {
                            conv = converter(8, type, Collection.class);
                        }
                    }
                }
            }
            if (conv == null) {
                return null;
            }
            return new StdDelegatingDeserializer(conv);
        }
        String localName2 = _findUtilArrayTypeName(clsName);
        if (localName2 != null) {
            if (localName2.contains("List")) {
                return new StdDelegatingDeserializer(converter(11, type, List.class));
            }
            return null;
        }
        String localName3 = _findUtilCollectionsImmutableTypeName(clsName);
        if (localName3 != null) {
            if (localName3.contains("List")) {
                return new StdDelegatingDeserializer(converter(11, type, List.class));
            }
            if (localName3.contains("Set")) {
                return new StdDelegatingDeserializer(converter(4, type, Set.class));
            }
            return null;
        }
        return null;
    }

    public static JsonDeserializer<?> findForMap(DeserializationContext ctxt, JavaType type) throws JsonMappingException {
        String clsName = type.getRawClass().getName();
        JavaUtilCollectionsConverter conv = null;
        String localName = _findUtilCollectionsTypeName(clsName);
        if (localName != null) {
            String name = _findUnmodifiableTypeName(localName);
            if (name != null) {
                if (name.contains("Map")) {
                    conv = converter(6, type, Map.class);
                }
            } else {
                String name2 = _findSingletonTypeName(localName);
                if (name2 != null) {
                    if (name2.contains("Map")) {
                        conv = converter(3, type, Map.class);
                    }
                } else {
                    String name3 = _findSyncTypeName(localName);
                    if (name3 != null && name3.contains("Map")) {
                        conv = converter(10, type, Map.class);
                    }
                }
            }
        } else {
            String localName2 = _findUtilCollectionsImmutableTypeName(clsName);
            if (localName2 != null && localName2.contains("Map")) {
                conv = converter(6, type, Map.class);
            }
        }
        if (conv == null) {
            return null;
        }
        return new StdDelegatingDeserializer(conv);
    }

    static JavaUtilCollectionsConverter converter(int kind, JavaType concreteType, Class<?> rawSuper) {
        return new JavaUtilCollectionsConverter(kind, concreteType.findSuperType(rawSuper));
    }

    private static String _findUtilArrayTypeName(String clsName) {
        if (clsName.startsWith(PREFIX_JAVA_UTIL_ARRAYS)) {
            return clsName.substring(PREFIX_JAVA_UTIL_ARRAYS.length());
        }
        return null;
    }

    private static String _findUtilCollectionsTypeName(String clsName) {
        if (clsName.startsWith(PREFIX_JAVA_UTIL_COLLECTIONS)) {
            return clsName.substring(PREFIX_JAVA_UTIL_COLLECTIONS.length());
        }
        return null;
    }

    private static String _findUtilCollectionsImmutableTypeName(String clsName) {
        if (clsName.startsWith(PREFIX_JAVA_UTIL_IMMUTABLE_COLL)) {
            return clsName.substring(PREFIX_JAVA_UTIL_IMMUTABLE_COLL.length());
        }
        return null;
    }

    private static String _findSingletonTypeName(String localName) {
        if (localName.startsWith("Singleton")) {
            return localName.substring(9);
        }
        return null;
    }

    private static String _findSyncTypeName(String localName) {
        if (localName.startsWith("Synchronized")) {
            return localName.substring(12);
        }
        return null;
    }

    private static String _findUnmodifiableTypeName(String localName) {
        if (localName.startsWith("Unmodifiable")) {
            return localName.substring(12);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/jackson-databind-2.15.3.jar:com/fasterxml/jackson/databind/deser/impl/JavaUtilCollectionsDeserializers$JavaUtilCollectionsConverter.class */
    public static class JavaUtilCollectionsConverter implements Converter<Object, Object> {
        private final JavaType _inputType;
        private final int _kind;

        JavaUtilCollectionsConverter(int kind, JavaType inputType) {
            this._inputType = inputType;
            this._kind = kind;
        }

        @Override // com.fasterxml.jackson.databind.util.Converter
        public Object convert(Object value) {
            if (value == null) {
                return null;
            }
            switch (this._kind) {
                case 1:
                    Set<?> set = (Set) value;
                    _checkSingleton(set.size());
                    return Collections.singleton(set.iterator().next());
                case 2:
                    List<?> list = (List) value;
                    _checkSingleton(list.size());
                    return Collections.singletonList(list.get(0));
                case 3:
                    Map<?, ?> map = (Map) value;
                    _checkSingleton(map.size());
                    Map.Entry<?, ?> entry = map.entrySet().iterator().next();
                    return Collections.singletonMap(entry.getKey(), entry.getValue());
                case 4:
                    return Collections.unmodifiableSet((Set) value);
                case 5:
                    return Collections.unmodifiableList((List) value);
                case 6:
                    return Collections.unmodifiableMap((Map) value);
                case 7:
                    return Collections.synchronizedSet((Set) value);
                case 8:
                    return Collections.synchronizedCollection((Collection) value);
                case 9:
                    return Collections.synchronizedList((List) value);
                case 10:
                    return Collections.synchronizedMap((Map) value);
                case 11:
                default:
                    return value;
            }
        }

        @Override // com.fasterxml.jackson.databind.util.Converter
        public JavaType getInputType(TypeFactory typeFactory) {
            return this._inputType;
        }

        @Override // com.fasterxml.jackson.databind.util.Converter
        public JavaType getOutputType(TypeFactory typeFactory) {
            return this._inputType;
        }

        private void _checkSingleton(int size) {
            if (size != 1) {
                throw new IllegalArgumentException("Can not deserialize Singleton container from " + size + " entries");
            }
        }
    }
}
