package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.BitSet;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/BitSetSerializer.class */
public class BitSetSerializer extends AbstractSerializer {
    private static BitSetSerializer SERIALIZER = new BitSetSerializer();

    public static BitSetSerializer create() {
        return SERIALIZER;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
        } else {
            BitSet bitSet = (BitSet) obj;
            out.writeObject(new BitSetHandle(bitSet.toLongArray()));
        }
    }
}
