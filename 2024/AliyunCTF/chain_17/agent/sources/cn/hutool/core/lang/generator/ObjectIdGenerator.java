package cn.hutool.core.lang.generator;

import cn.hutool.core.lang.ObjectId;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/generator/ObjectIdGenerator.class */
public class ObjectIdGenerator implements Generator<String> {
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // cn.hutool.core.lang.generator.Generator
    public String next() {
        return ObjectId.next();
    }
}
