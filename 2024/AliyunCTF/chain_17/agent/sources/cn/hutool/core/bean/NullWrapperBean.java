package cn.hutool.core.bean;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/NullWrapperBean.class */
public class NullWrapperBean<T> {
    private final Class<T> clazz;

    public NullWrapperBean(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getWrappedClass() {
        return this.clazz;
    }
}
