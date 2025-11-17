package cn.hutool.core.convert.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.core.map.MapProxy;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.Type;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/BeanConverter.class */
public class BeanConverter<T> extends AbstractConverter<T> {
    private static final long serialVersionUID = 1;
    private final Type beanType;
    private final Class<T> beanClass;
    private final CopyOptions copyOptions;

    public BeanConverter(Type beanType) {
        this(beanType, CopyOptions.create().setIgnoreError(true));
    }

    public BeanConverter(Class<T> beanClass) {
        this(beanClass, CopyOptions.create().setIgnoreError(true));
    }

    public BeanConverter(Type type, CopyOptions copyOptions) {
        this.beanType = type;
        this.beanClass = (Class<T>) TypeUtil.getClass(type);
        this.copyOptions = copyOptions;
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    /* renamed from: convertInternal */
    protected T convertInternal2(Object obj) {
        if ((obj instanceof Map) || (obj instanceof ValueProvider) || BeanUtil.isBean(obj.getClass())) {
            if ((obj instanceof Map) && this.beanClass.isInterface()) {
                return (T) MapProxy.create((Map) obj).toProxyBean(this.beanClass);
            }
            return (T) BeanCopier.create(obj, ReflectUtil.newInstanceIfPossible(this.beanClass), this.beanType, this.copyOptions).copy();
        }
        if (obj instanceof byte[]) {
            return (T) ObjectUtil.deserialize((byte[]) obj);
        }
        throw new ConvertException("Unsupported source type: {}", obj.getClass());
    }

    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<T> getTargetType() {
        return this.beanClass;
    }
}
