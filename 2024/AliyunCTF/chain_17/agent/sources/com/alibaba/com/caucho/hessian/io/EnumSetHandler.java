package com.alibaba.com.caucho.hessian.io;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/EnumSetHandler.class */
class EnumSetHandler implements Serializable, HessianHandle {
    private Class type;
    private Object[] objects;

    /* JADX INFO: Access modifiers changed from: package-private */
    public EnumSetHandler(Class type, Object[] objects) {
        this.type = type;
        this.objects = objects;
    }

    private Object readResolve() {
        EnumSet enumSet = EnumSet.noneOf(this.type);
        enumSet.addAll(Arrays.asList(this.objects));
        return enumSet;
    }
}
