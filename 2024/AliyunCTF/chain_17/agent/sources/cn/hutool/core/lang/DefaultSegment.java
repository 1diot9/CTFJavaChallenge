package cn.hutool.core.lang;

import java.lang.Number;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/lang/DefaultSegment.class */
public class DefaultSegment<T extends Number> implements Segment<T> {
    protected T startIndex;
    protected T endIndex;

    public DefaultSegment(T startIndex, T endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    @Override // cn.hutool.core.lang.Segment
    public T getStartIndex() {
        return this.startIndex;
    }

    @Override // cn.hutool.core.lang.Segment
    public T getEndIndex() {
        return this.endIndex;
    }
}
