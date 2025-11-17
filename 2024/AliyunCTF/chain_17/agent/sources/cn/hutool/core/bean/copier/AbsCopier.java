package cn.hutool.core.bean.copier;

import cn.hutool.core.lang.copier.Copier;
import cn.hutool.core.util.ObjectUtil;
import java.util.function.Supplier;

/* loaded from: agent.jar:BOOT-INF/lib/hutool-all-5.8.16.jar:cn/hutool/core/bean/copier/AbsCopier.class */
public abstract class AbsCopier<S, T> implements Copier<T> {
    protected final S source;
    protected final T target;
    protected final CopyOptions copyOptions;

    public AbsCopier(S source, T target, CopyOptions copyOptions) {
        this.source = source;
        this.target = target;
        this.copyOptions = (CopyOptions) ObjectUtil.defaultIfNull(copyOptions, (Supplier<? extends CopyOptions>) CopyOptions::create);
    }
}
