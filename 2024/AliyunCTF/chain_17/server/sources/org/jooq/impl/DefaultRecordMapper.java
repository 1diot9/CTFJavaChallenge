package org.jooq.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import org.jooq.Attachable;
import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.exception.MappingException;
import org.jooq.tools.StringUtils;
import org.jooq.tools.reflect.Reflect;

/* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper.class */
public class DefaultRecordMapper<R extends Record, E> implements RecordMapper<R, E> {
    private final Field<?>[] fields;
    private final RecordType<R> rowType;
    private final Class<? extends E> type;
    private final Configuration configuration;
    private final String namePathSeparator;
    private RecordMapper<R, E> delegate;
    private transient Map<String, Integer> prefixes;

    public DefaultRecordMapper(RecordType<R> rowType, Class<? extends E> type) {
        this(rowType, type, null, null);
    }

    public DefaultRecordMapper(RecordType<R> rowType, Class<? extends E> type, Configuration configuration) {
        this(rowType, type, null, configuration);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public DefaultRecordMapper(RecordType<R> rowType, Class<? extends E> type, E instance, Configuration configuration) {
        this.rowType = rowType;
        this.fields = rowType.fields();
        this.type = type;
        this.configuration = Tools.configuration(configuration);
        this.namePathSeparator = this.configuration.settings().getNamePathSeparator();
        init(instance);
    }

    /* JADX WARN: Code restructure failed: missing block: B:47:0x014c, code lost:            if (r0.booleanValue() != false) goto L50;     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:102:0x0405 A[Catch: IllegalAccessException | NoSuchMethodException | InvocationTargetException | ReflectException -> 0x04a9, TryCatch #1 {IllegalAccessException | NoSuchMethodException | InvocationTargetException | ReflectException -> 0x04a9, blocks: (B:85:0x02b7, B:88:0x02fd, B:90:0x0309, B:91:0x0356, B:93:0x035e, B:95:0x038f, B:97:0x03a6, B:100:0x03e7, B:102:0x0405, B:103:0x042f, B:105:0x044c, B:107:0x045a, B:111:0x0467, B:110:0x046e, B:118:0x047e), top: B:84:0x02b7 }] */
    /* JADX WARN: Removed duplicated region for block: B:114:0x042d  */
    /* JADX WARN: Removed duplicated region for block: B:127:0x04db  */
    /* JADX WARN: Removed duplicated region for block: B:172:0x05e1  */
    /* JADX WARN: Removed duplicated region for block: B:181:0x05e5  */
    /* JADX WARN: Type inference failed for: r15v0, types: [E, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r43v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r43v1 */
    /* JADX WARN: Type inference failed for: r43v2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private final void init(E r15) {
        /*
            Method dump skipped, instructions count: 1742
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jooq.impl.DefaultRecordMapper.init(java.lang.Object):void");
    }

    private static final String debug(Boolean debug) {
        return debug == null ? "check skipped" : debug.toString();
    }

    private List<String> collectParameterNames(Parameter[] parameters) {
        return (List) Arrays.stream(parameters).map((v0) -> {
            return v0.getName();
        }).collect(Collectors.toList());
    }

    @Override // org.jooq.RecordMapper
    public final E map(R r) {
        if (r == null) {
            return null;
        }
        try {
            return (E) attach(this.delegate.map(r), r);
        } catch (MappingException e) {
            throw e;
        } catch (Exception e2) {
            throw new MappingException("An error occurred when mapping record to " + String.valueOf(this.type), e2);
        }
    }

    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$AbstractDelegateMapper.class */
    private abstract class AbstractDelegateMapper<R0 extends Record, E0> implements RecordMapper<R0, E0> {
        private AbstractDelegateMapper() {
        }

        public String toString() {
            return getClass().getSimpleName() + " [ (" + String.valueOf(DefaultRecordMapper.this.rowType) + ") -> " + String.valueOf(DefaultRecordMapper.this.type) + "]";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$ArrayMapper.class */
    public class ArrayMapper extends DefaultRecordMapper<R, E>.AbstractDelegateMapper<R, E> {
        private final E instance;

        ArrayMapper(E instance) {
            super();
            this.instance = instance;
        }

        @Override // org.jooq.RecordMapper
        public final E map(R r) {
            E e;
            int size = r.size();
            Class<?> componentType = DefaultRecordMapper.this.type.getComponentType();
            if (this.instance != null) {
                e = this.instance;
            } else {
                e = (E) java.lang.reflect.Array.newInstance(componentType, size);
            }
            Object[] objArr = e;
            if (size > objArr.length) {
                objArr = (Object[]) java.lang.reflect.Array.newInstance(componentType, size);
            }
            for (int i = 0; i < size; i++) {
                objArr[i] = Convert.convert(r.get(i), componentType);
            }
            return (E) objArr;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$ValueTypeMapper.class */
    public class ValueTypeMapper extends DefaultRecordMapper<R, E>.AbstractDelegateMapper<R, E> {
        private ValueTypeMapper() {
            super();
        }

        @Override // org.jooq.RecordMapper
        public final E map(R r) {
            int size = r.size();
            if (size != 1) {
                throw new MappingException("Cannot map multi-column record of degree " + size + " to value type " + String.valueOf(DefaultRecordMapper.this.type));
            }
            return (E) r.get(0, DefaultRecordMapper.this.type);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$ProxyMapper.class */
    public class ProxyMapper extends DefaultRecordMapper<R, E>.AbstractDelegateMapper<R, E> {
        private final DefaultRecordMapper<R, E>.MutablePOJOMapper pojomapper;

        ProxyMapper() {
            super();
            this.pojomapper = new MutablePOJOMapper(() -> {
                return Reflect.on(new HashMap()).as(DefaultRecordMapper.this.type);
            }, null);
        }

        @Override // org.jooq.RecordMapper
        public final E map(R record) {
            return this.pojomapper.map(record);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$RecordToRecordMapper.class */
    public class RecordToRecordMapper<E extends AbstractRecord> extends DefaultRecordMapper<R, E>.AbstractDelegateMapper<R, AbstractRecord> {
        private final E instance;

        @Override // org.jooq.RecordMapper
        public /* bridge */ /* synthetic */ Object map(Record record) {
            return map((RecordToRecordMapper<E>) record);
        }

        RecordToRecordMapper(E instance) {
            super();
            this.instance = instance;
        }

        @Override // org.jooq.RecordMapper
        public final AbstractRecord map(R record) {
            try {
                if (record instanceof AbstractRecord) {
                    AbstractRecord a = (AbstractRecord) record;
                    if (this.instance != null) {
                        return (AbstractRecord) a.intoRecord(this.instance);
                    }
                    return (AbstractRecord) a.intoRecord(DefaultRecordMapper.this.type);
                }
                throw new MappingException("Cannot map record " + String.valueOf(record) + " to type " + String.valueOf(DefaultRecordMapper.this.type));
            } catch (Exception e) {
                throw new MappingException("An error occurred when mapping record to " + String.valueOf(DefaultRecordMapper.this.type), e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$ConstructorCall.class */
    public static final class ConstructorCall<E> extends Record implements Callable<E> {
        private final Constructor<? extends E> constructor;

        private ConstructorCall(Constructor<? extends E> constructor) {
            this.constructor = constructor;
        }

        @Override // java.lang.Record
        public final String toString() {
            return (String) ObjectMethods.bootstrap(MethodHandles.lookup(), "toString", MethodType.methodType(String.class, ConstructorCall.class), ConstructorCall.class, "constructor", "FIELD:Lorg/jooq/impl/DefaultRecordMapper$ConstructorCall;->constructor:Ljava/lang/reflect/Constructor;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final int hashCode() {
            return (int) ObjectMethods.bootstrap(MethodHandles.lookup(), "hashCode", MethodType.methodType(Integer.TYPE, ConstructorCall.class), ConstructorCall.class, "constructor", "FIELD:Lorg/jooq/impl/DefaultRecordMapper$ConstructorCall;->constructor:Ljava/lang/reflect/Constructor;").dynamicInvoker().invoke(this) /* invoke-custom */;
        }

        @Override // java.lang.Record
        public final boolean equals(Object o) {
            return (boolean) ObjectMethods.bootstrap(MethodHandles.lookup(), "equals", MethodType.methodType(Boolean.TYPE, ConstructorCall.class, Object.class), ConstructorCall.class, "constructor", "FIELD:Lorg/jooq/impl/DefaultRecordMapper$ConstructorCall;->constructor:Ljava/lang/reflect/Constructor;").dynamicInvoker().invoke(this, o) /* invoke-custom */;
        }

        public Constructor<? extends E> constructor() {
            return this.constructor;
        }

        @Override // java.util.concurrent.Callable
        public E call() throws Exception {
            return this.constructor.newInstance(new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$MutablePOJOMapper.class */
    public class MutablePOJOMapper extends DefaultRecordMapper<R, E>.AbstractDelegateMapper<R, E> {
        private final Callable<E> constructor;
        private final boolean useAnnotations;
        private final List<java.lang.reflect.Field>[] members;
        private final List<Method>[] methods;
        private final Map<String, NestedMappingInfo> nestedMappingInfos;
        private final E instance;

        MutablePOJOMapper(Callable<E> constructor, E instance) {
            super();
            this.constructor = constructor;
            this.useAnnotations = Tools.hasColumnAnnotations(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type);
            this.members = new List[DefaultRecordMapper.this.fields.length];
            this.methods = new List[DefaultRecordMapper.this.fields.length];
            this.instance = instance;
            this.nestedMappingInfos = new HashMap();
            Map<String, List<Field<?>>> nestedMappedFields = null;
            for (int i = 0; i < DefaultRecordMapper.this.fields.length; i++) {
                Field<?> field = DefaultRecordMapper.this.fields[i];
                String name = field.getName();
                if (this.useAnnotations) {
                    this.members[i] = Tools.getAnnotatedMembers(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name, true);
                    this.methods[i] = Tools.getAnnotatedSetters(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name, true);
                } else {
                    int separator = name.indexOf(DefaultRecordMapper.this.namePathSeparator);
                    if (separator > -1) {
                        String prefix = name.substring(0, separator);
                        nestedMappedFields = nestedMappedFields == null ? new HashMap<>() : nestedMappedFields;
                        nestedMappedFields.computeIfAbsent(prefix, p -> {
                            return new ArrayList();
                        }).add(DSL.field(DSL.name(name.substring(prefix.length() + 1)), field.getDataType()));
                        this.nestedMappingInfos.computeIfAbsent(prefix, p2 -> {
                            return new NestedMappingInfo();
                        }).indexLookup.add(Integer.valueOf(i));
                        this.members[i] = Collections.emptyList();
                        this.methods[i] = Collections.emptyList();
                    } else {
                        this.members[i] = Tools.getMatchingMembers(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name, true);
                        this.methods[i] = Tools.getMatchingSetters(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name, true);
                    }
                }
            }
            if (nestedMappedFields != null) {
                nestedMappedFields.forEach((str, list) -> {
                    NestedMappingInfo nestedMappingInfo = this.nestedMappingInfos.get(str);
                    nestedMappingInfo.row = Tools.row0(list);
                    nestedMappingInfo.recordDelegate = Tools.newRecord(true, Tools.recordType(nestedMappingInfo.row.size()), nestedMappingInfo.row, DefaultRecordMapper.this.configuration);
                    Iterator<java.lang.reflect.Field> it = Tools.getMatchingMembers(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, str, true).iterator();
                    while (it.hasNext()) {
                        nestedMappingInfo.mappers.add(nestedMappingInfo.row.fields.mapper(DefaultRecordMapper.this.configuration, it.next().getType()));
                    }
                    Iterator<Method> it2 = Tools.getMatchingSetters(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, str, true).iterator();
                    while (it2.hasNext()) {
                        nestedMappingInfo.mappers.add(nestedMappingInfo.row.fields.mapper(DefaultRecordMapper.this.configuration, it2.next().getParameterTypes()[0]));
                    }
                });
            }
        }

        final boolean isMutable() {
            for (List<Method> m : this.methods) {
                if (!m.isEmpty()) {
                    return true;
                }
            }
            for (List<java.lang.reflect.Field> m1 : this.members) {
                for (java.lang.reflect.Field m2 : m1) {
                    if ((m2.getModifiers() & 16) == 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override // org.jooq.RecordMapper
        public final E map(R record) {
            try {
                E result = this.instance != null ? this.instance : this.constructor.call();
                for (int i = 0; i < DefaultRecordMapper.this.fields.length; i++) {
                    for (java.lang.reflect.Field member : this.members[i]) {
                        if ((member.getModifiers() & 16) == 0) {
                            map(record, result, member, i);
                        }
                    }
                    for (Method method : this.methods[i]) {
                        Class<?> mType = method.getParameterTypes()[0];
                        Object value = record.get(i, mType);
                        Collection<?> list = tryConvertToListOrSet(value, mType, method.getGenericParameterTypes()[0]);
                        if (list != null) {
                            method.invoke(result, list);
                        } else {
                            method.invoke(result, value);
                        }
                    }
                }
                for (Map.Entry<String, NestedMappingInfo> entry : this.nestedMappingInfos.entrySet()) {
                    String prefix = entry.getKey();
                    for (RecordMapper<AbstractRecord, Object> mapper : entry.getValue().mappers) {
                        entry.getValue().recordDelegate.operate(rec -> {
                            List<Integer> indexes = ((NestedMappingInfo) entry.getValue()).indexLookup;
                            for (int index = 0; index < indexes.size(); index++) {
                                rec.set(index, record.get(indexes.get(index).intValue()));
                            }
                            Object value2 = mapper.map(rec);
                            for (java.lang.reflect.Field member2 : Tools.getMatchingMembers(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, prefix, true)) {
                                if ((member2.getModifiers() & 16) == 0) {
                                    map(value2, result, member2);
                                }
                            }
                            for (Method method2 : Tools.getMatchingSetters(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, prefix, true)) {
                                method2.invoke(result, value2);
                            }
                            return rec;
                        });
                    }
                }
                return result;
            } catch (Exception e) {
                throw new MappingException("An error occurred when mapping record to " + String.valueOf(DefaultRecordMapper.this.type), e);
            }
        }

        private final void map(Record record, Object result, java.lang.reflect.Field member, int index) throws IllegalAccessException {
            Class<?> mType = member.getType();
            if (mType.isPrimitive()) {
                if (mType == Byte.TYPE) {
                    map(record.get(index, Byte.TYPE), result, member);
                    return;
                }
                if (mType == Short.TYPE) {
                    map(record.get(index, Short.TYPE), result, member);
                    return;
                }
                if (mType == Integer.TYPE) {
                    map(record.get(index, Integer.TYPE), result, member);
                    return;
                }
                if (mType == Long.TYPE) {
                    map(record.get(index, Long.TYPE), result, member);
                    return;
                }
                if (mType == Float.TYPE) {
                    map(record.get(index, Float.TYPE), result, member);
                    return;
                }
                if (mType == Double.TYPE) {
                    map(record.get(index, Double.TYPE), result, member);
                    return;
                } else if (mType == Boolean.TYPE) {
                    map(record.get(index, Boolean.TYPE), result, member);
                    return;
                } else {
                    if (mType == Character.TYPE) {
                        map(record.get(index, Character.TYPE), result, member);
                        return;
                    }
                    return;
                }
            }
            Object value = record.get(index, (Class<? extends Object>) mType);
            Object list = tryConvertToListOrSet(value, mType, member.getGenericType());
            if (list != null) {
                member.set(result, list);
            } else {
                map(value, result, member);
            }
        }

        private final Collection<?> tryConvertToListOrSet(Object value, Class<?> mType, Type genericType) {
            if (value instanceof Collection) {
                Collection<?> c = (Collection) value;
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType p = (ParameterizedType) genericType;
                    Class<?> componentType = (Class) p.getActualTypeArguments()[0];
                    if (mType == List.class || mType == ArrayList.class) {
                        return Convert.convert(c, (Class) componentType);
                    }
                    if (mType == Set.class || mType == LinkedHashSet.class) {
                        return new LinkedHashSet(Convert.convert(c, (Class) componentType));
                    }
                    if (mType == HashSet.class) {
                        return new HashSet(Convert.convert(c, (Class) componentType));
                    }
                    return null;
                }
                return null;
            }
            return null;
        }

        private final void map(Object value, Object result, java.lang.reflect.Field member) throws IllegalAccessException {
            Class<?> mType = member.getType();
            if (mType.isPrimitive()) {
                if (mType == Byte.TYPE) {
                    member.setByte(result, ((Byte) value).byteValue());
                    return;
                }
                if (mType == Short.TYPE) {
                    member.setShort(result, ((Short) value).shortValue());
                    return;
                }
                if (mType == Integer.TYPE) {
                    member.setInt(result, ((Integer) value).intValue());
                    return;
                }
                if (mType == Long.TYPE) {
                    member.setLong(result, ((Long) value).longValue());
                    return;
                }
                if (mType == Float.TYPE) {
                    member.setFloat(result, ((Float) value).floatValue());
                    return;
                }
                if (mType == Double.TYPE) {
                    member.setDouble(result, ((Double) value).doubleValue());
                    return;
                } else if (mType == Boolean.TYPE) {
                    member.setBoolean(result, ((Boolean) value).booleanValue());
                    return;
                } else {
                    if (mType == Character.TYPE) {
                        member.setChar(result, ((Character) value).charValue());
                        return;
                    }
                    return;
                }
            }
            member.set(result, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$ImmutablePOJOMapper.class */
    public class ImmutablePOJOMapper extends DefaultRecordMapper<R, E>.AbstractDelegateMapper<R, E> {
        final Constructor<E> constructor;
        final Class<?>[] parameterTypes;
        private final boolean nested;
        private final NestedMappingInfo[] nestedMappingInfo;
        private final Integer[] propertyIndexes;
        private final List<String> propertyNames;
        private final boolean useAnnotations;
        private final List<java.lang.reflect.Field>[] members;
        private final Method[] methods;

        /* JADX WARN: Multi-variable type inference failed */
        ImmutablePOJOMapper(Constructor<E> constructor, Class<?>[] clsArr, List<String> list, boolean z) {
            super();
            int size = DefaultRecordMapper.this.prefixes().size();
            this.constructor = (Constructor) Reflect.accessible(constructor);
            this.parameterTypes = clsArr;
            this.nestedMappingInfo = new NestedMappingInfo[size];
            this.propertyIndexes = new Integer[DefaultRecordMapper.this.fields.length];
            this.propertyNames = list;
            this.useAnnotations = Tools.hasColumnAnnotations(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type);
            this.members = new List[DefaultRecordMapper.this.fields.length];
            this.methods = new Method[DefaultRecordMapper.this.fields.length];
            if (list.isEmpty()) {
                if (!z) {
                    for (int i = 0; i < DefaultRecordMapper.this.fields.length; i++) {
                        this.propertyIndexes[i] = Integer.valueOf(i);
                    }
                } else {
                    for (int i2 = 0; i2 < DefaultRecordMapper.this.fields.length; i2++) {
                        String name = DefaultRecordMapper.this.fields[i2].getName();
                        int indexOf = name.indexOf(DefaultRecordMapper.this.namePathSeparator);
                        this.propertyIndexes[i2] = DefaultRecordMapper.this.prefixes().get(indexOf > -1 ? name.substring(0, indexOf) : name);
                    }
                }
            } else {
                for (int i3 = 0; i3 < DefaultRecordMapper.this.fields.length; i3++) {
                    String name2 = DefaultRecordMapper.this.fields[i3].getName();
                    String camelCaseLC = StringUtils.toCamelCaseLC(name2);
                    if (this.useAnnotations) {
                        this.members[i3] = Tools.getAnnotatedMembers(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name2, false);
                        this.methods[i3] = Tools.getAnnotatedGetter(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name2, true);
                    } else {
                        this.members[i3] = Tools.getMatchingMembers(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name2, false);
                        this.methods[i3] = Tools.getMatchingGetter(DefaultRecordMapper.this.configuration, DefaultRecordMapper.this.type, name2, true);
                    }
                    for (int i4 = 0; i4 < list.size(); i4++) {
                        if (name2.equals(list.get(i4)) || camelCaseLC.equals(list.get(i4))) {
                            this.propertyIndexes[i3] = Integer.valueOf(i4);
                            break;
                        }
                    }
                    int i5 = 0;
                    while (true) {
                        if (i5 >= list.size()) {
                            break;
                        }
                        if (!name2.startsWith(list.get(i5) + DefaultRecordMapper.this.namePathSeparator)) {
                            i5++;
                        } else {
                            this.propertyIndexes[i3] = Integer.valueOf(i5);
                            break;
                        }
                    }
                }
            }
            boolean z2 = false;
            List[] listArr = new List[size];
            if (z) {
                for (Map.Entry<String, Integer> entry : DefaultRecordMapper.this.prefixes().entrySet()) {
                    String key = entry.getKey();
                    int intValue = entry.getValue().intValue();
                    if (this.nestedMappingInfo[intValue] == null) {
                        this.nestedMappingInfo[intValue] = new NestedMappingInfo();
                    }
                    int i6 = 0;
                    while (true) {
                        if (i6 < DefaultRecordMapper.this.fields.length) {
                            if (!DefaultRecordMapper.this.fields[i6].getName().equals(key)) {
                                i6++;
                            } else {
                                this.nestedMappingInfo[intValue].indexLookup.add(Integer.valueOf(i6));
                                break;
                            }
                        } else {
                            for (int i7 = 0; i7 < DefaultRecordMapper.this.fields.length; i7++) {
                                if (DefaultRecordMapper.this.fields[i7].getName().startsWith(key + DefaultRecordMapper.this.namePathSeparator)) {
                                    z2 = true;
                                    if (listArr[intValue] == null) {
                                        listArr[intValue] = new ArrayList();
                                    }
                                    listArr[intValue].add(DSL.field(DSL.name(DefaultRecordMapper.this.fields[i7].getName().substring(key.length() + 1)), DefaultRecordMapper.this.fields[i7].getDataType()));
                                    this.nestedMappingInfo[intValue].indexLookup.add(Integer.valueOf(i7));
                                }
                            }
                            if (listArr[intValue] != null) {
                                this.nestedMappingInfo[intValue].row = Tools.row0((Field<?>[]) listArr[intValue].toArray(Tools.EMPTY_FIELD));
                                this.nestedMappingInfo[intValue].recordDelegate = Tools.newRecord(true, Tools.recordType(this.nestedMappingInfo[intValue].row.size()), this.nestedMappingInfo[intValue].row, DefaultRecordMapper.this.configuration);
                                this.nestedMappingInfo[intValue].mappers.add(this.nestedMappingInfo[intValue].row.fields.mapper(DefaultRecordMapper.this.configuration, clsArr[this.propertyIndexes[this.nestedMappingInfo[intValue].indexLookup.get(0).intValue()].intValue()]));
                            }
                        }
                    }
                }
            }
            this.nested = z2;
        }

        @Override // org.jooq.RecordMapper
        public final E map(R record) {
            try {
                return this.constructor.newInstance(this.nested ? mapNested(record) : mapNonnested(record));
            } catch (Exception e) {
                throw new MappingException("An error occurred when mapping record to " + String.valueOf(DefaultRecordMapper.this.type), e);
            }
        }

        private final Object[] mapNonnested(R record) {
            Object[] converted = Tools.map(this.parameterTypes, c -> {
                return Reflect.initValue(c);
            }, x$0 -> {
                return new Object[x$0];
            });
            for (int i = 0; i < record.size(); i++) {
                set(record, i, converted, this.propertyIndexes[i]);
            }
            return converted;
        }

        final void set(Record from, int fromIndex, Object[] to, Integer toIndex) {
            if (toIndex != null) {
                to[toIndex.intValue()] = from.get(fromIndex, this.parameterTypes[toIndex.intValue()]);
                return;
            }
            for (java.lang.reflect.Field member : this.members[fromIndex]) {
                int index = this.propertyNames.indexOf(member.getName());
                if (index >= 0) {
                    to[index] = from.get(fromIndex, member.getType());
                }
            }
            if (this.methods[fromIndex] != null) {
                String name = Tools.getPropertyName(this.methods[fromIndex].getName());
                int index2 = this.propertyNames.indexOf(name);
                if (index2 >= 0) {
                    to[index2] = from.get(fromIndex, this.methods[fromIndex].getReturnType());
                }
            }
        }

        private final Object[] mapNested(R record) {
            Object[] converted = new Object[this.parameterTypes.length];
            for (int i = 0; i < this.nestedMappingInfo.length; i++) {
                NestedMappingInfo info = this.nestedMappingInfo[i];
                List<Integer> indexLookup = info.indexLookup;
                Integer j = indexLookup.get(0);
                Integer k = this.propertyIndexes[j.intValue()];
                if (k != null) {
                    if (info.row == null) {
                        converted[k.intValue()] = record.get(j.intValue(), this.parameterTypes[k.intValue()]);
                    } else {
                        converted[k.intValue()] = info.mappers.get(0).map(info.recordDelegate.operate(rec -> {
                            for (int x = 0; x < indexLookup.size(); x++) {
                                rec.set(x, record.get(((Integer) indexLookup.get(x)).intValue()));
                            }
                            return rec;
                        }));
                    }
                }
            }
            return converted;
        }

        @Override // org.jooq.impl.DefaultRecordMapper.AbstractDelegateMapper
        public String toString() {
            return getClass().getSimpleName() + " [ (" + String.valueOf(DefaultRecordMapper.this.rowType) + ") -> " + String.valueOf(this.constructor) + "]";
        }
    }

    private static <E> E attach(E attachable, Record record) {
        if (attachable instanceof Attachable) {
            Attachable a = (Attachable) attachable;
            if (Tools.attachRecords(record.configuration())) {
                a.attach(record.configuration());
            }
        }
        return attachable;
    }

    private final Map<String, Integer> prefixes() {
        if (this.prefixes == null) {
            this.prefixes = new LinkedHashMap();
            int[] i = {0};
            for (Field<?> field : this.fields) {
                String name = field.getName();
                int separator = name.indexOf(this.namePathSeparator);
                this.prefixes.computeIfAbsent(separator > -1 ? name.substring(0, separator) : name, k -> {
                    int i2 = i[0];
                    i[0] = i2 + 1;
                    return Integer.valueOf(i2);
                });
            }
        }
        return this.prefixes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: server.jar:BOOT-INF/lib/jooq-3.19.3.jar:org/jooq/impl/DefaultRecordMapper$NestedMappingInfo.class */
    public static class NestedMappingInfo {
        AbstractRow row;
        RecordDelegate<? extends AbstractRecord> recordDelegate;
        final List<RecordMapper<AbstractRecord, Object>> mappers = new ArrayList();
        final List<Integer> indexLookup = new ArrayList();

        NestedMappingInfo() {
        }

        public String toString() {
            return "NestedMappingInfo " + String.valueOf(this.indexLookup) + "; (" + String.valueOf(this.row) + ")";
        }
    }

    public String toString() {
        return this.delegate.toString();
    }
}
