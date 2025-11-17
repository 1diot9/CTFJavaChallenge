package cn.hutool.core.codec;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/codec/Decoder.class */
public interface Decoder<T, R> {
    R decode(T t);
}
