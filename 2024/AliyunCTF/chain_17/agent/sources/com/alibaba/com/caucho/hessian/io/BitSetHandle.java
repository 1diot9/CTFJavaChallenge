package com.alibaba.com.caucho.hessian.io;

import java.io.Serializable;
import java.util.BitSet;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BitSetHandle.class */
public class BitSetHandle implements Serializable, HessianHandle {
    private long[] words;

    public BitSetHandle(long[] words) {
        this.words = words;
    }

    private Object readResolve() {
        if (this.words == null) {
            return null;
        }
        return BitSet.valueOf(this.words);
    }
}
