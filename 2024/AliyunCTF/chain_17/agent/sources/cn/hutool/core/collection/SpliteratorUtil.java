package cn.hutool.core.collection;

import java.util.Spliterator;
import java.util.function.Function;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/collection/SpliteratorUtil.class */
public class SpliteratorUtil {
    public static <F, T> Spliterator<T> trans(Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
        return new TransSpliterator(fromSpliterator, function);
    }
}
