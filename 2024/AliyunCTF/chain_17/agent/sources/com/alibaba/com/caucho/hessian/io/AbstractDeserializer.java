package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/AbstractDeserializer.class */
public abstract class AbstractDeserializer implements Deserializer {
    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Class getType() {
        return Object.class;
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        String className = getClass().getName();
        if (obj != null) {
            throw error(className + ": unexpected object " + obj.getClass().getName());
        }
        throw error(className + ": unexpected null value");
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readList(AbstractHessianInput in, int length, Class<?> expectType) throws IOException {
        if (expectType == null) {
            return readList(in, length);
        }
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readLengthList(AbstractHessianInput in, int length, Class<?> expectType) throws IOException {
        if (expectType == null) {
            return readLengthList(in, length);
        }
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in) throws IOException {
        Object obj = in.readObject();
        String className = getClass().getName();
        if (obj != null) {
            throw error(className + ": unexpected object " + obj.getClass().getName());
        }
        throw error(className + ": unexpected null value");
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readMap(AbstractHessianInput in, Class<?> expectKeyType, Class<?> expectValueType) throws IOException {
        if (expectKeyType == null && expectValueType == null) {
            return readMap(in);
        }
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    @Override // com.alibaba.com.caucho.hessian.io.Deserializer
    public Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public HessianProtocolException error(String msg) {
        return new HessianProtocolException(msg);
    }

    protected String codeName(int ch2) {
        if (ch2 < 0) {
            return "end of file";
        }
        return "0x" + Integer.toHexString(ch2 & Const.MAX_ARRAY_DIMENSIONS);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SerializerFactory findSerializerFactory(AbstractHessianInput in) {
        SerializerFactory serializerFactory = null;
        if (in instanceof Hessian2Input) {
            serializerFactory = ((Hessian2Input) in).findSerializerFactory();
        } else if (in instanceof HessianInput) {
            serializerFactory = ((HessianInput) in).getSerializerFactory();
        }
        return serializerFactory == null ? new SerializerFactory() : serializerFactory;
    }
}
