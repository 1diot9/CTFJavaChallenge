package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.util.Collection;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/CollectionSerializer.class */
public class CollectionSerializer extends AbstractSerializer {
    private boolean _sendJavaType = true;

    public boolean getSendJavaType() {
        return this._sendJavaType;
    }

    public void setSendJavaType(boolean sendJavaType) {
        this._sendJavaType = sendJavaType;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Collection list = (Collection) obj;
        obj.getClass();
        boolean hasEnd = out.writeListBegin(list.size(), obj.getClass().getName());
        for (Object value : list) {
            out.writeObject(value);
        }
        if (hasEnd) {
            out.writeListEnd();
        }
    }
}
