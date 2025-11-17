package cn.hutool.core.lang.intern;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/intern/InternUtil.class */
public class InternUtil {
    public static <T> Interner<T> createWeakInterner() {
        return new WeakInterner();
    }

    public static Interner<String> createJdkInterner() {
        return new JdkStringInterner();
    }

    public static Interner<String> createStringInterner(boolean isWeak) {
        return isWeak ? createWeakInterner() : createJdkInterner();
    }
}
