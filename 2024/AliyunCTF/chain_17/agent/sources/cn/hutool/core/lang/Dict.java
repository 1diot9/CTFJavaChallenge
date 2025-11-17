package cn.hutool.core.lang;

import cn.hutool.core.bean.BeanPath;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.getter.BasicTypeGetter;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.lang.func.LambdaUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/Dict.class */
public class Dict extends LinkedHashMap<String, Object> implements BasicTypeGetter<String> {
    private static final long serialVersionUID = 6135423866861206530L;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    private boolean caseInsensitive;

    @Override // java.util.HashMap, java.util.Map
    public /* bridge */ /* synthetic */ Object merge(Object obj, Object obj2, BiFunction biFunction) {
        return merge((String) obj, obj2, (BiFunction<? super Object, ? super Object, ?>) biFunction);
    }

    @Override // java.util.HashMap, java.util.Map
    public /* bridge */ /* synthetic */ Object compute(Object obj, BiFunction biFunction) {
        return compute((String) obj, (BiFunction<? super String, ? super Object, ?>) biFunction);
    }

    @Override // java.util.HashMap, java.util.Map
    public /* bridge */ /* synthetic */ Object computeIfPresent(Object obj, BiFunction biFunction) {
        return computeIfPresent((String) obj, (BiFunction<? super String, ? super Object, ?>) biFunction);
    }

    @Override // java.util.HashMap, java.util.Map
    public /* bridge */ /* synthetic */ Object computeIfAbsent(Object obj, Function function) {
        return computeIfAbsent((String) obj, (Function<? super String, ?>) function);
    }

    public static Dict create() {
        return new Dict();
    }

    public static <T> Dict parse(T bean) {
        return create().parseBean(bean);
    }

    @SafeVarargs
    public static Dict of(Pair<String, Object>... pairs) {
        Dict dict = create();
        for (Pair<String, Object> pair : pairs) {
            dict.put(pair.getKey(), pair.getValue());
        }
        return dict;
    }

    public static Dict of(Object... keysAndValues) {
        Dict dict = create();
        String key = null;
        for (int i = 0; i < keysAndValues.length; i++) {
            if (i % 2 == 0) {
                key = Convert.toStr(keysAndValues[i]);
            } else {
                dict.put(key, keysAndValues[i]);
            }
        }
        return dict;
    }

    public Dict() {
        this(false);
    }

    public Dict(boolean caseInsensitive) {
        this(16, caseInsensitive);
    }

    public Dict(int initialCapacity) {
        this(initialCapacity, false);
    }

    public Dict(int initialCapacity, boolean caseInsensitive) {
        this(initialCapacity, 0.75f, caseInsensitive);
    }

    public Dict(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, false);
    }

    public Dict(int initialCapacity, float loadFactor, boolean caseInsensitive) {
        super(initialCapacity, loadFactor);
        this.caseInsensitive = caseInsensitive;
    }

    public Dict(Map<String, Object> m) {
        super(null == m ? new HashMap<>() : m);
    }

    public <T> T toBean(T t) {
        return (T) toBean(t, false);
    }

    public <T> T toBeanIgnoreCase(T bean) {
        BeanUtil.fillBeanWithMapIgnoreCase(this, bean, false);
        return bean;
    }

    public <T> T toBean(T bean, boolean isToCamelCase) {
        BeanUtil.fillBeanWithMap((Map<?, ?>) this, (Object) bean, isToCamelCase, false);
        return bean;
    }

    public <T> T toBeanWithCamelCase(T bean) {
        BeanUtil.fillBeanWithMap((Map<?, ?>) this, (Object) bean, true, false);
        return bean;
    }

    public <T> T toBean(Class<T> cls) {
        return (T) BeanUtil.toBean(this, cls);
    }

    public <T> T toBeanIgnoreCase(Class<T> cls) {
        return (T) BeanUtil.toBeanIgnoreCase(this, cls, false);
    }

    public <T> Dict parseBean(T bean) {
        Assert.notNull(bean, "Bean class must be not null", new Object[0]);
        putAll(BeanUtil.beanToMap(bean, new String[0]));
        return this;
    }

    public <T> Dict parseBean(T bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
        Assert.notNull(bean, "Bean class must be not null", new Object[0]);
        putAll(BeanUtil.beanToMap(bean, isToUnderlineCase, ignoreNullValue));
        return this;
    }

    public <T extends Dict> void removeEqual(T dict, String... withoutNames) {
        HashSet<String> withoutSet = CollUtil.newHashSet(withoutNames);
        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            if (!withoutSet.contains(entry.getKey())) {
                Object value = get(entry.getKey());
                if (Objects.equals(value, entry.getValue())) {
                    remove(entry.getKey());
                }
            }
        }
    }

    public Dict filter(String... keys) {
        Dict result = new Dict(keys.length, 1.0f);
        for (String key : keys) {
            if (containsKey(key)) {
                result.put(key, get(key));
            }
        }
        return result;
    }

    public Dict set(String attr, Object value) {
        put(attr, value);
        return this;
    }

    public Dict setIgnoreNull(String attr, Object value) {
        if (null != attr && null != value) {
            set(attr, value);
        }
        return this;
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Object getObj(String key) {
        return super.get(key);
    }

    public <T> T getBean(String str) {
        return (T) get(str, null);
    }

    public <T> T get(String str, T t) {
        T t2 = (T) get(str);
        return t2 != null ? t2 : t;
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public String getStr(String attr) {
        return Convert.toStr(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Integer getInt(String attr) {
        return Convert.toInt(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Long getLong(String attr) {
        return Convert.toLong(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Float getFloat(String attr) {
        return Convert.toFloat(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Short getShort(String attr) {
        return Convert.toShort(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Character getChar(String attr) {
        return Convert.toChar(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Double getDouble(String attr) {
        return Convert.toDouble(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Byte getByte(String attr) {
        return Convert.toByte(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Boolean getBool(String attr) {
        return Convert.toBool(get(attr), null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public BigDecimal getBigDecimal(String attr) {
        return Convert.toBigDecimal(get(attr));
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public BigInteger getBigInteger(String attr) {
        return Convert.toBigInteger(get(attr));
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public <E extends Enum<E>> E getEnum(Class<E> cls, String str) {
        return (E) Convert.toEnum(cls, get(str));
    }

    public byte[] getBytes(String attr) {
        return (byte[]) get(attr, null);
    }

    @Override // cn.hutool.core.getter.BasicTypeGetter
    public Date getDate(String attr) {
        return (Date) get(attr, null);
    }

    public Time getTime(String attr) {
        return (Time) get(attr, null);
    }

    public Timestamp getTimestamp(String attr) {
        return (Timestamp) get(attr, null);
    }

    public Number getNumber(String attr) {
        return (Number) get(attr, null);
    }

    public <T> T getByPath(String str) {
        return (T) BeanPath.create(str).get(this);
    }

    public <T> T getByPath(String str, Class<T> cls) {
        return (T) Convert.convert((Class) cls, getByPath(str));
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object key) {
        return super.containsKey(customKey((String) key));
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object get(Object key) {
        return super.get(customKey((String) key));
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object put(String key, Object value) {
        return super.put((Dict) customKey(key), (String) value);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends String, ?> m) {
        m.forEach(this::put);
    }

    @Override // java.util.HashMap, java.util.AbstractMap
    public Dict clone() {
        return (Dict) super.clone();
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public Object remove(Object key) {
        return super.remove(customKey((String) key));
    }

    @Override // java.util.HashMap, java.util.Map
    public boolean remove(Object key, Object value) {
        return super.remove(customKey((String) key), value);
    }

    @Override // java.util.HashMap, java.util.Map
    public boolean replace(String key, Object oldValue, Object newValue) {
        return super.replace((Dict) customKey(key), oldValue, newValue);
    }

    @Override // java.util.HashMap, java.util.Map
    public Object replace(String key, Object value) {
        return super.replace((Dict) customKey(key), (String) value);
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.Map
    public Object getOrDefault(Object key, Object defaultValue) {
        return super.getOrDefault(customKey((String) key), defaultValue);
    }

    public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return super.computeIfPresent((Dict) customKey(key), (BiFunction<? super Dict, ? super V, ? extends V>) remappingFunction);
    }

    public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
        return super.compute((Dict) customKey(key), (BiFunction<? super Dict, ? super V, ? extends V>) remappingFunction);
    }

    public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return super.merge((Dict) customKey(key), (String) value, (BiFunction<? super String, ? super String, ? extends String>) remappingFunction);
    }

    @Override // java.util.HashMap, java.util.Map
    public Object putIfAbsent(String key, Object value) {
        return super.putIfAbsent((Dict) customKey(key), (String) value);
    }

    public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
        return super.computeIfAbsent((Dict) customKey(key), (Function<? super Dict, ? extends V>) mappingFunction);
    }

    private String customKey(String key) {
        if (this.caseInsensitive && null != key) {
            key = key.toLowerCase();
        }
        return key;
    }

    public Dict setFields(Func0<?>... fields) {
        Arrays.stream(fields).forEach(f -> {
            set(LambdaUtil.getFieldName(f), f.callWithRuntimeException());
        });
        return this;
    }
}
