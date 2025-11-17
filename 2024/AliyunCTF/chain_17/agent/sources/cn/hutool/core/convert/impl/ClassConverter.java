package cn.hutool.core.convert.impl;

import cn.hutool.core.convert.AbstractConverter;
import cn.hutool.core.util.ClassLoaderUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/convert/impl/ClassConverter.class */
public class ClassConverter extends AbstractConverter<Class<?>> {
    private static final long serialVersionUID = 1;
    private final boolean isInitialized;

    public ClassConverter() {
        this(true);
    }

    public ClassConverter(boolean isInitialized) {
        this.isInitialized = isInitialized;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.convert.AbstractConverter
    public Class<?> convertInternal(Object value) {
        return ClassLoaderUtil.loadClass(convertToStr(value), this.isInitialized);
    }
}
