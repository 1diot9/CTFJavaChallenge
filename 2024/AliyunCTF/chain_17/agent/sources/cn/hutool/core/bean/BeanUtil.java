package cn.hutool.core.bean;

import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/BeanUtil.class */
public class BeanUtil {
    private static /* synthetic */ Object $deserializeLambda$(SerializedLambda lambda) {
        String implMethodName = lambda.getImplMethodName();
        boolean z = -1;
        switch (implMethodName.hashCode()) {
            case -1541658353:
                if (implMethodName.equals("lambda$getBeanDesc$e7c7684d$1")) {
                    z = true;
                    break;
                }
                break;
            case 1988560598:
                if (implMethodName.equals("lambda$getPropertyDescriptorMap$58f3b7cb$1")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case false:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/bean/BeanUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;Z)Ljava/util/Map;")) {
                    Class cls = (Class) lambda.getCapturedArg(0);
                    boolean booleanValue = ((Boolean) lambda.getCapturedArg(1)).booleanValue();
                    return () -> {
                        return internalGetPropertyDescriptorMap(cls, booleanValue);
                    };
                }
                break;
            case true:
                if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/bean/BeanUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)Lcn/hutool/core/bean/BeanDesc;")) {
                    Class cls2 = (Class) lambda.getCapturedArg(0);
                    return () -> {
                        return new BeanDesc(cls2);
                    };
                }
                break;
        }
        throw new IllegalArgumentException("Invalid lambda deserialization");
    }

    public static boolean isReadableBean(Class<?> clazz) {
        return hasGetter(clazz) || hasPublicField(clazz);
    }

    public static boolean isBean(Class<?> clazz) {
        return hasSetter(clazz) || hasPublicField(clazz);
    }

    public static boolean hasSetter(Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 1 && method.getName().startsWith("set")) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean hasGetter(Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 0) {
                    String name = method.getName();
                    if ((name.startsWith(ch.qos.logback.core.joran.util.beans.BeanUtil.PREFIX_GETTER_GET) || name.startsWith(ch.qos.logback.core.joran.util.beans.BeanUtil.PREFIX_GETTER_IS)) && false == "getClass".equals(name)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    public static boolean hasPublicField(Class<?> clazz) {
        if (ClassUtil.isNormalClass(clazz)) {
            for (Field field : clazz.getFields()) {
                if (ModifierUtil.isPublic(field) && false == ModifierUtil.isStatic(field)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static DynaBean createDynaBean(Object bean) {
        return new DynaBean(bean);
    }

    public static PropertyEditor findEditor(Class<?> type) {
        return PropertyEditorManager.findEditor(type);
    }

    public static BeanDesc getBeanDesc(Class<?> clazz) {
        return BeanDescCache.INSTANCE.getBeanDesc(clazz, () -> {
            return new BeanDesc(clazz);
        });
    }

    public static void descForEach(Class<?> clazz, Consumer<? super PropDesc> action) {
        getBeanDesc(clazz).getProps().forEach(action);
    }

    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeanException {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            return (PropertyDescriptor[]) ArrayUtil.filter(beanInfo.getPropertyDescriptors(), t -> {
                return false == "class".equals(t.getName());
            });
        } catch (IntrospectionException e) {
            throw new BeanException((Throwable) e);
        }
    }

    public static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
        return BeanInfoCache.INSTANCE.getPropertyDescriptorMap(clazz, ignoreCase, () -> {
            return internalGetPropertyDescriptorMap(clazz, ignoreCase);
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<String, PropertyDescriptor> internalGetPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
        PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
        Map<String, PropertyDescriptor> map = ignoreCase ? new CaseInsensitiveMap<>(propertyDescriptors.length, 1.0f) : new HashMap<>(propertyDescriptors.length, 1.0f);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            map.put(propertyDescriptor.getName(), propertyDescriptor);
        }
        return map;
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName) throws BeanException {
        return getPropertyDescriptor(clazz, fieldName, false);
    }

    public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName, boolean ignoreCase) throws BeanException {
        Map<String, PropertyDescriptor> map = getPropertyDescriptorMap(clazz, ignoreCase);
        if (null == map) {
            return null;
        }
        return map.get(fieldName);
    }

    public static Object getFieldValue(Object bean, String fieldNameOrIndex) {
        if (null == bean || null == fieldNameOrIndex) {
            return null;
        }
        if (bean instanceof Map) {
            return ((Map) bean).get(fieldNameOrIndex);
        }
        if (bean instanceof Collection) {
            try {
                return CollUtil.get((Collection) bean, Integer.parseInt(fieldNameOrIndex));
            } catch (NumberFormatException e) {
                return CollUtil.map((Collection) bean, beanEle -> {
                    return getFieldValue(beanEle, fieldNameOrIndex);
                }, false);
            }
        }
        if (ArrayUtil.isArray(bean)) {
            try {
                return ArrayUtil.get(bean, Integer.parseInt(fieldNameOrIndex));
            } catch (NumberFormatException e2) {
                return ArrayUtil.map(bean, Object.class, beanEle2 -> {
                    return getFieldValue(beanEle2, fieldNameOrIndex);
                });
            }
        }
        return ReflectUtil.getFieldValue(bean, fieldNameOrIndex);
    }

    public static Object setFieldValue(Object bean, String fieldNameOrIndex, Object value) {
        if (bean instanceof Map) {
            ((Map) bean).put(fieldNameOrIndex, value);
        } else if (bean instanceof List) {
            ListUtil.setOrPadding((List) bean, Convert.toInt(fieldNameOrIndex).intValue(), value);
        } else {
            if (ArrayUtil.isArray(bean)) {
                return ArrayUtil.setOrAppend(bean, Convert.toInt(fieldNameOrIndex).intValue(), value);
            }
            ReflectUtil.setFieldValue(bean, fieldNameOrIndex, value);
        }
        return bean;
    }

    public static <T> T getProperty(Object obj, String str) {
        if (null == obj || StrUtil.isBlank(str)) {
            return null;
        }
        return (T) BeanPath.create(str).get(obj);
    }

    public static void setProperty(Object bean, String expression, Object value) {
        BeanPath.create(expression).set(bean, value);
    }

    @Deprecated
    public static <T> T mapToBean(Map<?, ?> map, Class<T> cls, boolean z) {
        return (T) fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(cls), z);
    }

    @Deprecated
    public static <T> T mapToBeanIgnoreCase(Map<?, ?> map, Class<T> cls, boolean z) {
        return (T) fillBeanWithMapIgnoreCase(map, ReflectUtil.newInstanceIfPossible(cls), z);
    }

    @Deprecated
    public static <T> T mapToBean(Map<?, ?> map, Class<T> cls, CopyOptions copyOptions) {
        return (T) fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(cls), copyOptions);
    }

    public static <T> T mapToBean(Map<?, ?> map, Class<T> cls, boolean z, CopyOptions copyOptions) {
        return (T) fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(cls), z, copyOptions);
    }

    public static <T> T fillBeanWithMap(Map<?, ?> map, T t, boolean z) {
        return (T) fillBeanWithMap(map, (Object) t, false, z);
    }

    public static <T> T fillBeanWithMap(Map<?, ?> map, T t, boolean z, boolean z2) {
        return (T) fillBeanWithMap(map, t, z, CopyOptions.create().setIgnoreError(z2));
    }

    public static <T> T fillBeanWithMapIgnoreCase(Map<?, ?> map, T t, boolean z) {
        return (T) fillBeanWithMap(map, t, CopyOptions.create().setIgnoreCase(true).setIgnoreError(z));
    }

    public static <T> T fillBeanWithMap(Map<?, ?> map, T t, CopyOptions copyOptions) {
        return (T) fillBeanWithMap(map, (Object) t, false, copyOptions);
    }

    public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase, CopyOptions copyOptions) {
        if (MapUtil.isEmpty(map)) {
            return bean;
        }
        if (isToCamelCase) {
            map = MapUtil.toCamelCaseMap(map);
        }
        copyProperties(map, bean, copyOptions);
        return bean;
    }

    public static <T> T toBean(Object obj, Class<T> cls) {
        return (T) toBean(obj, cls, (CopyOptions) null);
    }

    public static <T> T toBeanIgnoreError(Object obj, Class<T> cls) {
        return (T) toBean(obj, cls, CopyOptions.create().setIgnoreError(true));
    }

    public static <T> T toBeanIgnoreCase(Object obj, Class<T> cls, boolean z) {
        return (T) toBean(obj, cls, CopyOptions.create().setIgnoreCase(true).setIgnoreError(z));
    }

    public static <T> T toBean(Object obj, Class<T> cls, CopyOptions copyOptions) {
        return (T) toBean(obj, () -> {
            return ReflectUtil.newInstanceIfPossible(cls);
        }, copyOptions);
    }

    public static <T> T toBean(Object source, Supplier<T> targetSupplier, CopyOptions options) {
        if (null == source || null == targetSupplier) {
            return null;
        }
        T target = targetSupplier.get();
        copyProperties(source, target, options);
        return target;
    }

    public static <T> T toBean(Class<T> cls, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
        if (null == cls || null == valueProvider) {
            return null;
        }
        return (T) fillBean(ReflectUtil.newInstanceIfPossible(cls), valueProvider, copyOptions);
    }

    public static <T> T fillBean(T t, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
        if (null == valueProvider) {
            return t;
        }
        return (T) BeanCopier.create(valueProvider, t, copyOptions).copy();
    }

    public static Map<String, Object> beanToMap(Object bean, String... properties) {
        int mapSize = 16;
        Editor<String> keyEditor = null;
        if (ArrayUtil.isNotEmpty((Object[]) properties)) {
            mapSize = properties.length;
            Set<String> propertiesSet = CollUtil.set(false, properties);
            keyEditor = property -> {
                if (propertiesSet.contains(property)) {
                    return property;
                }
                return null;
            };
        }
        return beanToMap(bean, (Map<String, Object>) new LinkedHashMap(mapSize, 1.0f), false, keyEditor);
    }

    public static Map<String, Object> beanToMap(Object bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
        if (null == bean) {
            return null;
        }
        return beanToMap(bean, new LinkedHashMap(), isToUnderlineCase, ignoreNullValue);
    }

    public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean isToUnderlineCase, boolean ignoreNullValue) {
        if (null == bean) {
            return null;
        }
        return beanToMap(bean, targetMap, ignoreNullValue, (Editor<String>) key -> {
            return isToUnderlineCase ? StrUtil.toUnderlineCase(key) : key;
        });
    }

    public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean ignoreNullValue, Editor<String> keyEditor) {
        if (null == bean) {
            return null;
        }
        return (Map) BeanCopier.create(bean, targetMap, CopyOptions.create().setIgnoreNullValue(ignoreNullValue).setFieldNameEditor(keyEditor)).copy();
    }

    public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, CopyOptions copyOptions) {
        if (null == bean) {
            return null;
        }
        return (Map) BeanCopier.create(bean, targetMap, copyOptions).copy();
    }

    public static <T> T copyProperties(Object obj, Class<T> cls, String... strArr) {
        if (null == obj) {
            return null;
        }
        T t = (T) ReflectUtil.newInstanceIfPossible(cls);
        copyProperties(obj, t, CopyOptions.create().setIgnoreProperties(strArr));
        return t;
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
    }

    public static void copyProperties(Object source, Object target, boolean ignoreCase) {
        BeanCopier.create(source, target, CopyOptions.create().setIgnoreCase(ignoreCase)).copy();
    }

    public static void copyProperties(Object source, Object target, CopyOptions copyOptions) {
        if (null == source) {
            return;
        }
        BeanCopier.create(source, target, (CopyOptions) ObjectUtil.defaultIfNull(copyOptions, (Supplier<? extends CopyOptions>) CopyOptions::create)).copy();
    }

    public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType, CopyOptions copyOptions) {
        if (null == collection) {
            return null;
        }
        if (collection.isEmpty()) {
            return new ArrayList(0);
        }
        return (List) collection.stream().map(source -> {
            Object newInstanceIfPossible = ReflectUtil.newInstanceIfPossible(targetType);
            copyProperties(source, newInstanceIfPossible, copyOptions);
            return newInstanceIfPossible;
        }).collect(Collectors.toList());
    }

    public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType) {
        return copyToList(collection, targetType, CopyOptions.create());
    }

    public static boolean isMatchName(Object bean, String beanClassName, boolean isSimple) {
        if (null == bean || StrUtil.isBlank(beanClassName)) {
            return false;
        }
        return ClassUtil.getClassName(bean, isSimple).equals(isSimple ? StrUtil.upperFirst(beanClassName) : beanClassName);
    }

    public static <T> T edit(T bean, Editor<Field> editor) {
        if (bean == null) {
            return null;
        }
        Field[] fields = ReflectUtil.getFields(bean.getClass());
        for (Field field : fields) {
            if (!ModifierUtil.isStatic(field)) {
                editor.edit(field);
            }
        }
        return bean;
    }

    public static <T> T trimStrFields(T t, String... strArr) {
        return (T) edit(t, field -> {
            String val;
            if (strArr != null && ArrayUtil.containsIgnoreCase(strArr, field.getName())) {
                return field;
            }
            if (String.class.equals(field.getType()) && null != (val = (String) ReflectUtil.getFieldValue(t, field))) {
                String trimVal = StrUtil.trim(val);
                if (false == val.equals(trimVal)) {
                    ReflectUtil.setFieldValue(t, field, trimVal);
                }
            }
            return field;
        });
    }

    public static boolean isNotEmpty(Object bean, String... ignoreFieldNames) {
        return false == isEmpty(bean, ignoreFieldNames);
    }

    public static boolean isEmpty(Object bean, String... ignoreFieldNames) {
        if (null != bean) {
            for (Field field : ReflectUtil.getFields(bean.getClass())) {
                if (!ModifierUtil.isStatic(field) && false == ArrayUtil.contains(ignoreFieldNames, field.getName()) && null != ReflectUtil.getFieldValue(bean, field)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public static boolean hasNullField(Object bean, String... ignoreFieldNames) {
        if (null == bean) {
            return true;
        }
        for (Field field : ReflectUtil.getFields(bean.getClass())) {
            if (!ModifierUtil.isStatic(field) && false == ArrayUtil.contains(ignoreFieldNames, field.getName()) && null == ReflectUtil.getFieldValue(bean, field)) {
                return true;
            }
        }
        return false;
    }

    public static String getFieldName(String getterOrSetterName) {
        if (getterOrSetterName.startsWith(ch.qos.logback.core.joran.util.beans.BeanUtil.PREFIX_GETTER_GET) || getterOrSetterName.startsWith("set")) {
            return StrUtil.removePreAndLowerFirst(getterOrSetterName, 3);
        }
        if (getterOrSetterName.startsWith(ch.qos.logback.core.joran.util.beans.BeanUtil.PREFIX_GETTER_IS)) {
            return StrUtil.removePreAndLowerFirst(getterOrSetterName, 2);
        }
        throw new IllegalArgumentException("Invalid Getter or Setter name: " + getterOrSetterName);
    }

    public static boolean isCommonFieldsEqual(Object source, Object target, String... ignoreProperties) {
        if (null == source && null == target) {
            return true;
        }
        if (null == source || null == target) {
            return false;
        }
        Map<String, Object> sourceFieldsMap = beanToMap(source, new String[0]);
        Map<String, Object> targetFieldsMap = beanToMap(target, new String[0]);
        Set<String> sourceFields = sourceFieldsMap.keySet();
        sourceFields.removeAll(Arrays.asList(ignoreProperties));
        for (String field : sourceFields) {
            if (ObjectUtil.notEqual(sourceFieldsMap.get(field), targetFieldsMap.get(field))) {
                return false;
            }
        }
        return true;
    }
}
