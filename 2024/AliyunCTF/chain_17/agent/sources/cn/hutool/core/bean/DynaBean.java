package cn.hutool.core.bean;

import cn.hutool.core.clone.CloneSupport;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import java.io.Serializable;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/DynaBean.class */
public class DynaBean extends CloneSupport<DynaBean> implements Serializable {
    private static final long serialVersionUID = 1;
    private final Class<?> beanClass;
    private final Object bean;

    public static DynaBean create(Object bean) {
        return new DynaBean(bean);
    }

    public static DynaBean create(Class<?> beanClass) {
        return new DynaBean(beanClass);
    }

    public static DynaBean create(Class<?> beanClass, Object... params) {
        return new DynaBean(beanClass, params);
    }

    public DynaBean(Class<?> beanClass, Object... params) {
        this(ReflectUtil.newInstance(beanClass, params));
    }

    public DynaBean(Class<?> beanClass) {
        this(ReflectUtil.newInstance(beanClass, new Object[0]));
    }

    public DynaBean(Object bean) {
        Assert.notNull(bean);
        bean = bean instanceof DynaBean ? ((DynaBean) bean).getBean() : bean;
        this.bean = bean;
        this.beanClass = ClassUtil.getClass(bean);
    }

    public <T> T get(String str) throws BeanException {
        if (Map.class.isAssignableFrom(this.beanClass)) {
            return (T) ((Map) this.bean).get(str);
        }
        PropDesc prop = BeanUtil.getBeanDesc(this.beanClass).getProp(str);
        if (null == prop) {
            throw new BeanException("No public field or get method for {}", str);
        }
        return (T) prop.getValue(this.bean);
    }

    public boolean containsProp(String fieldName) {
        if (Map.class.isAssignableFrom(this.beanClass)) {
            return ((Map) this.bean).containsKey(fieldName);
        }
        return null != BeanUtil.getBeanDesc(this.beanClass).getProp(fieldName);
    }

    public <T> T safeGet(String str) {
        try {
            return (T) get(str);
        } catch (Exception e) {
            return null;
        }
    }

    public void set(String fieldName, Object value) throws BeanException {
        if (Map.class.isAssignableFrom(this.beanClass)) {
            ((Map) this.bean).put(fieldName, value);
            return;
        }
        PropDesc prop = BeanUtil.getBeanDesc(this.beanClass).getProp(fieldName);
        if (null == prop) {
            throw new BeanException("No public field or set method for {}", fieldName);
        }
        prop.setValue(this.bean, value);
    }

    public Object invoke(String methodName, Object... params) {
        return ReflectUtil.invoke(this.bean, methodName, params);
    }

    public <T> T getBean() {
        return (T) this.bean;
    }

    public <T> Class<T> getBeanClass() {
        return (Class<T>) this.beanClass;
    }

    public int hashCode() {
        int result = (31 * 1) + (this.bean == null ? 0 : this.bean.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DynaBean other = (DynaBean) obj;
        if (this.bean == null) {
            return other.bean == null;
        }
        return this.bean.equals(other.bean);
    }

    public String toString() {
        return this.bean.toString();
    }
}
