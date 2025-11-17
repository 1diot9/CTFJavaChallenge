package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/InputStreamSerializer.class */
public class InputStreamSerializer extends AbstractSerializer {
    @Override // com.alibaba.com.caucho.hessian.io.AbstractSerializer, com.alibaba.com.caucho.hessian.io.Serializer
    public void writeObject(Object obj, AbstractHessianOutput out) throws IOException {
        InputStream is = (InputStream) obj;
        if (is == null) {
            out.writeNull();
            return;
        }
        byte[] buf = new byte[1024];
        while (true) {
            int len = is.read(buf, 0, buf.length);
            if (len > 0) {
                out.writeByteBufferPart(buf, 0, len);
            } else {
                out.writeByteBufferEnd(buf, 0, 0);
                return;
            }
        }
    }
}
