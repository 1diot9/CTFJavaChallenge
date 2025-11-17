package cn.hutool.core.clone;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/clone/CloneSupport.class */
public class CloneSupport<T> implements Cloneable<T> {
    @Override // cn.hutool.core.clone.Cloneable
    public T clone() {
        try {
            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneRuntimeException(e);
        }
    }
}
