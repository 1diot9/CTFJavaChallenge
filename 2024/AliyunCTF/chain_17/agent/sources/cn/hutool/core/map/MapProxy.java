package cn.hutool.core.map;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.getter.OptNullBasicTypeFromObjectGetter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.springframework.jmx.export.naming.IdentityNamingStrategy;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/map/MapProxy.class */
public class MapProxy implements Map<Object, Object>, OptNullBasicTypeFromObjectGetter<Object>, InvocationHandler, Serializable {
    private static final long serialVersionUID = 1;
    Map map;

    public static MapProxy create(Map<?, ?> map) {
        return map instanceof MapProxy ? (MapProxy) map : new MapProxy(map);
    }

    public MapProxy(Map<?, ?> map) {
        this.map = map;
    }

    @Override // cn.hutool.core.getter.OptBasicTypeGetter
    public Object getObj(Object key, Object defaultValue) {
        Object value = this.map.get(key);
        return null != value ? value : defaultValue;
    }

    @Override // java.util.Map
    public int size() {
        return this.map.size();
    }

    @Override // java.util.Map
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override // java.util.Map
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override // java.util.Map
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override // java.util.Map
    public Object get(Object key) {
        return this.map.get(key);
    }

    @Override // java.util.Map
    public Object put(Object key, Object value) {
        return this.map.put(key, value);
    }

    @Override // java.util.Map
    public Object remove(Object key) {
        return this.map.remove(key);
    }

    @Override // java.util.Map
    public void putAll(Map<? extends Object, ? extends Object> map) {
        this.map.putAll(map);
    }

    @Override // java.util.Map
    public void clear() {
        this.map.clear();
    }

    @Override // java.util.Map
    public Set<Object> keySet() {
        return this.map.keySet();
    }

    @Override // java.util.Map
    public Collection<Object> values() {
        return this.map.values();
    }

    @Override // java.util.Map
    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.map.entrySet();
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (ArrayUtil.isEmpty((Object[]) parameterTypes)) {
            Class<?> returnType = method.getReturnType();
            if (Void.TYPE != returnType) {
                String methodName = method.getName();
                String fieldName = null;
                if (methodName.startsWith(BeanUtil.PREFIX_GETTER_GET)) {
                    fieldName = StrUtil.removePreAndLowerFirst(methodName, 3);
                } else if (BooleanUtil.isBoolean(returnType) && methodName.startsWith(BeanUtil.PREFIX_GETTER_IS)) {
                    fieldName = StrUtil.removePreAndLowerFirst(methodName, 2);
                } else {
                    if (IdentityNamingStrategy.HASH_CODE_KEY.equals(methodName)) {
                        return Integer.valueOf(hashCode());
                    }
                    if ("toString".equals(methodName)) {
                        return toString();
                    }
                }
                if (StrUtil.isNotBlank(fieldName)) {
                    if (false == containsKey(fieldName)) {
                        fieldName = StrUtil.toUnderlineCase(fieldName);
                    }
                    return Convert.convert(method.getGenericReturnType(), get(fieldName));
                }
            }
        } else if (1 == parameterTypes.length) {
            String methodName2 = method.getName();
            if (methodName2.startsWith("set")) {
                String fieldName2 = StrUtil.removePreAndLowerFirst(methodName2, 3);
                if (StrUtil.isNotBlank(fieldName2)) {
                    put(fieldName2, args[0]);
                    if (method.getReturnType().isInstance(proxy)) {
                        return proxy;
                    }
                }
            } else if ("equals".equals(methodName2)) {
                return Boolean.valueOf(equals(args[0]));
            }
        }
        throw new UnsupportedOperationException(method.toGenericString());
    }

    public <T> T toProxyBean(Class<T> cls) {
        return (T) Proxy.newProxyInstance(ClassLoaderUtil.getClassLoader(), new Class[]{cls}, this);
    }
}
