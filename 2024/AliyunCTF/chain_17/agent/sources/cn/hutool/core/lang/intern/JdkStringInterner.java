package cn.hutool.core.lang.intern;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/intern/JdkStringInterner.class */
public class JdkStringInterner implements Interner<String> {
    @Override // cn.hutool.core.lang.intern.Interner
    public String intern(String sample) {
        if (null == sample) {
            return null;
        }
        return sample.intern();
    }
}
