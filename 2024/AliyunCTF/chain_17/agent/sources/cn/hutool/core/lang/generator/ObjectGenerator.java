package cn.hutool.core.lang.generator;

import cn.hutool.core.util.ReflectUtil;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/generator/ObjectGenerator.class */
public class ObjectGenerator<T> implements Generator<T> {
    private final Class<T> clazz;

    public ObjectGenerator(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override // cn.hutool.core.lang.generator.Generator
    public T next() {
        return (T) ReflectUtil.newInstanceIfPossible(this.clazz);
    }
}
