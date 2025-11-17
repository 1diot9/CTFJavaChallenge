package cn.hutool.core.bean;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.annotation.PropIgnore;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/PropDesc.class */
public class PropDesc {
    final Field field;
    protected Method getter;
    protected Method setter;

    public PropDesc(Field field, Method getter, Method setter) {
        this.field = field;
        this.getter = ClassUtil.setAccessible(getter);
        this.setter = ClassUtil.setAccessible(setter);
    }

    public String getFieldName() {
        return ReflectUtil.getFieldName(this.field);
    }

    public String getRawFieldName() {
        if (null == this.field) {
            return null;
        }
        return this.field.getName();
    }

    public Field getField() {
        return this.field;
    }

    public Type getFieldType() {
        if (null != this.field) {
            return TypeUtil.getType(this.field);
        }
        return findPropType(this.getter, this.setter);
    }

    public Class<?> getFieldClass() {
        if (null != this.field) {
            return TypeUtil.getClass(this.field);
        }
        return findPropClass(this.getter, this.setter);
    }

    public Method getGetter() {
        return this.getter;
    }

    public Method getSetter() {
        return this.setter;
    }

    public boolean isReadable(boolean checkTransient) {
        if (null == this.getter && false == ModifierUtil.isPublic(this.field)) {
            return false;
        }
        return !(checkTransient && isTransientForGet()) && false == isIgnoreGet();
    }

    public Object getValue(Object bean) {
        if (null != this.getter) {
            return ReflectUtil.invoke(bean, this.getter, new Object[0]);
        }
        if (ModifierUtil.isPublic(this.field)) {
            return ReflectUtil.getFieldValue(bean, this.field);
        }
        return null;
    }

    public Object getValue(Object bean, Type targetType, boolean ignoreError) {
        Object result = null;
        try {
            result = getValue(bean);
        } catch (Exception e) {
            if (false == ignoreError) {
                throw new BeanException(e, "Get value of [{}] error!", getFieldName());
            }
        }
        if (null != result && null != targetType) {
            return Convert.convertWithCheck(targetType, result, null, ignoreError);
        }
        return result;
    }

    public boolean isWritable(boolean checkTransient) {
        if (null == this.setter && false == ModifierUtil.isPublic(this.field)) {
            return false;
        }
        return !(checkTransient && isTransientForSet()) && false == isIgnoreSet();
    }

    public PropDesc setValue(Object bean, Object value) {
        if (null != this.setter) {
            ReflectUtil.invoke(bean, this.setter, value);
        } else if (ModifierUtil.isPublic(this.field)) {
            ReflectUtil.setFieldValue(bean, this.field, value);
        }
        return this;
    }

    public PropDesc setValue(Object bean, Object value, boolean ignoreNull, boolean ignoreError) {
        return setValue(bean, value, ignoreNull, ignoreError, true);
    }

    public PropDesc setValue(Object bean, Object value, boolean ignoreNull, boolean ignoreError, boolean override) {
        if (null == value && ignoreNull) {
            return this;
        }
        if (false == override && null != getValue(bean)) {
            return this;
        }
        if (null != value) {
            Class<?> propClass = getFieldClass();
            if (false == propClass.isInstance(value)) {
                value = Convert.convertWithCheck(propClass, value, null, ignoreError);
            }
        }
        if (null != value || false == ignoreNull) {
            try {
                setValue(bean, value);
            } catch (Exception e) {
                if (false == ignoreError) {
                    throw new BeanException(e, "Set value of [{}] error!", getFieldName());
                }
            }
        }
        return this;
    }

    private Type findPropType(Method getter, Method setter) {
        Type type = null;
        if (null != getter) {
            type = TypeUtil.getReturnType(getter);
        }
        if (null == type && null != setter) {
            type = TypeUtil.getParamType(setter, 0);
        }
        return type;
    }

    private Class<?> findPropClass(Method getter, Method setter) {
        Class<?> type = null;
        if (null != getter) {
            type = TypeUtil.getReturnClass(getter);
        }
        if (null == type && null != setter) {
            type = TypeUtil.getFirstParamClass(setter);
        }
        return type;
    }

    private boolean isIgnoreSet() {
        return AnnotationUtil.hasAnnotation(this.field, PropIgnore.class) || AnnotationUtil.hasAnnotation(this.setter, PropIgnore.class);
    }

    private boolean isIgnoreGet() {
        return AnnotationUtil.hasAnnotation(this.field, PropIgnore.class) || AnnotationUtil.hasAnnotation(this.getter, PropIgnore.class);
    }

    private boolean isTransientForGet() {
        boolean isTransient = ModifierUtil.hasModifier(this.field, ModifierUtil.ModifierType.TRANSIENT);
        if (false == isTransient && null != this.getter) {
            isTransient = ModifierUtil.hasModifier(this.getter, ModifierUtil.ModifierType.TRANSIENT);
            if (false == isTransient) {
                isTransient = AnnotationUtil.hasAnnotation(this.getter, Transient.class);
            }
        }
        return isTransient;
    }

    private boolean isTransientForSet() {
        boolean isTransient = ModifierUtil.hasModifier(this.field, ModifierUtil.ModifierType.TRANSIENT);
        if (false == isTransient && null != this.setter) {
            isTransient = ModifierUtil.hasModifier(this.setter, ModifierUtil.ModifierType.TRANSIENT);
            if (false == isTransient) {
                isTransient = AnnotationUtil.hasAnnotation(this.setter, Transient.class);
            }
        }
        return isTransient;
    }
}
