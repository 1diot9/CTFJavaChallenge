package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.OutputStream;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/AbstractHessianOutput.class */
public abstract class AbstractHessianOutput {
    protected SerializerFactory _serializerFactory;

    public abstract void startCall() throws IOException;

    public abstract void startCall(String str, int i) throws IOException;

    public abstract void writeMethod(String str) throws IOException;

    public abstract void completeCall() throws IOException;

    public abstract void writeBoolean(boolean z) throws IOException;

    public abstract void writeInt(int i) throws IOException;

    public abstract void writeLong(long j) throws IOException;

    public abstract void writeDouble(double d) throws IOException;

    public abstract void writeUTCDate(long j) throws IOException;

    public abstract void writeNull() throws IOException;

    public abstract void writeString(String str) throws IOException;

    public abstract void writeString(char[] cArr, int i, int i2) throws IOException;

    public abstract void writeBytes(byte[] bArr) throws IOException;

    public abstract void writeBytes(byte[] bArr, int i, int i2) throws IOException;

    public abstract void writeByteBufferStart() throws IOException;

    public abstract void writeByteBufferPart(byte[] bArr, int i, int i2) throws IOException;

    public abstract void writeByteBufferEnd(byte[] bArr, int i, int i2) throws IOException;

    protected abstract void writeRef(int i) throws IOException;

    public abstract boolean removeRef(Object obj) throws IOException;

    public abstract boolean replaceRef(Object obj, Object obj2) throws IOException;

    public abstract boolean addRef(Object obj) throws IOException;

    public abstract void writeObject(Object obj) throws IOException;

    public abstract boolean writeListBegin(int i, String str) throws IOException;

    public abstract void writeListEnd() throws IOException;

    public abstract void writeMapBegin(String str) throws IOException;

    public abstract void writeMapEnd() throws IOException;

    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }

    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    public final SerializerFactory findSerializerFactory() {
        SerializerFactory factory = this._serializerFactory;
        if (factory == null) {
            SerializerFactory serializerFactory = new SerializerFactory();
            factory = serializerFactory;
            this._serializerFactory = serializerFactory;
        }
        return factory;
    }

    public void init(OutputStream os) {
    }

    public void call(String method, Object[] args) throws IOException {
        int length = args != null ? args.length : 0;
        startCall(method, length);
        for (int i = 0; i < length; i++) {
            writeObject(args[i]);
        }
        completeCall();
    }

    public void writeHeader(String name) throws IOException {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public void resetReferences() {
    }

    public int writeObjectBegin(String type) throws IOException {
        writeMapBegin(type);
        return -2;
    }

    public void writeClassFieldLength(int len) throws IOException {
    }

    public void writeObjectEnd() throws IOException {
    }

    public void writeReply(Object o) throws IOException {
        startReply();
        writeObject(o);
        completeReply();
    }

    public void startReply() throws IOException {
    }

    public void completeReply() throws IOException {
    }

    public void writeFault(String code, String message, Object detail) throws IOException {
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }
}
