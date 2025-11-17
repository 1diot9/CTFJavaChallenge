package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Vector;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/EnumerationDeserializer.class */
public class EnumerationDeserializer extends AbstractListDeserializer {
    private static EnumerationDeserializer _deserializer;

    public static EnumerationDeserializer create() {
        if (_deserializer == null) {
            _deserializer = new EnumerationDeserializer();
        }
        return _deserializer;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractDeserializer, com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        Vector list = new Vector();
        in.addRef(list);
        while (!in.isEnd()) {
            list.add(in.readObject());
        }
        in.readEnd();
        return list.elements();
    }
}
