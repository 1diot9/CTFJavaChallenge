package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/ValueDeserializer.class */
public abstract class ValueDeserializer extends AbstractDeserializer {
    abstract Object create(String str) throws IOException;

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        String initValue = null;
        while (!in.isEnd()) {
            String key = in.readString();
            if (key.equals("value")) {
                initValue = in.readString();
            } else {
                in.readObject();
            }
        }
        in.readMapEnd();
        return create(initValue);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        String initValue = null;
        for (String str : fieldNames) {
            if ("value".equals(str)) {
                initValue = in.readString();
            } else {
                in.readObject();
            }
        }
        return create(initValue);
    }
}
