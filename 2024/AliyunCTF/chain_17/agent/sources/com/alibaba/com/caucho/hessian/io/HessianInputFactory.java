package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianInputFactory.class */
public class HessianInputFactory {
    public static final Logger log = Logger.getLogger(HessianInputFactory.class.getName());
    private SerializerFactory _serializerFactory;

    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public AbstractHessianInput open(InputStream is) throws IOException {
        int code = is.read();
        int major = is.read();
        is.read();
        switch (code) {
            case 67:
            case 82:
            case 99:
            case Opcodes.FREM /* 114 */:
                if (major >= 2) {
                    AbstractHessianInput in = new Hessian2Input(is);
                    in.setSerializerFactory(this._serializerFactory);
                    return in;
                }
                AbstractHessianInput in2 = new HessianInput(is);
                in2.setSerializerFactory(this._serializerFactory);
                return in2;
            default:
                throw new IOException(((char) code) + " is an unknown Hessian message code.");
        }
    }
}
