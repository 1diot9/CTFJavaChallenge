package cn.hutool.core.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ReferenceUtil.class */
public class ReferenceUtil {

    /* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/util/ReferenceUtil$ReferenceType.class */
    public enum ReferenceType {
        SOFT,
        WEAK,
        PHANTOM
    }

    public static <T> Reference<T> create(ReferenceType type, T referent) {
        return create(type, referent, null);
    }

    public static <T> Reference<T> create(ReferenceType type, T referent, ReferenceQueue<T> queue) {
        switch (type) {
            case SOFT:
                return new SoftReference(referent, queue);
            case WEAK:
                return new WeakReference(referent, queue);
            case PHANTOM:
                return new PhantomReference(referent, queue);
            default:
                return null;
        }
    }
}
