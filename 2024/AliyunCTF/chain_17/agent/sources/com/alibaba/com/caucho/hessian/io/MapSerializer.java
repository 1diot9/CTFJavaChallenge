package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/MapSerializer.class */
public class MapSerializer extends AbstractSerializer {
    private boolean _isSendJavaType = true;

    public boolean getSendJavaType() {
        return this._isSendJavaType;
    }

    public void setSendJavaType(boolean sendJavaType) {
        this._isSendJavaType = sendJavaType;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        if (out.addRef(obj)) {
            return;
        }
        Map map = (Map) obj;
        Class cl = obj.getClass();
        if (cl.equals(HashMap.class) || !this._isSendJavaType || !(obj instanceof Serializable)) {
            out.writeMapBegin(null);
        } else {
            out.writeMapBegin(obj.getClass().getName());
        }
        for (Map.Entry entry : map.entrySet()) {
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeMapEnd();
    }
}
