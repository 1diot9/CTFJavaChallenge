package com.alibaba.com.caucho.hessian.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import org.w3c.dom.Node;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/AbstractHessianInput.class */
public abstract class AbstractHessianInput {
    private HessianRemoteResolver resolver;

    public abstract String getMethod();

    public abstract boolean checkAndReadNull();

    public abstract int readCall() throws IOException;

    public abstract String readHeader() throws IOException;

    public abstract String readMethod() throws IOException;

    public abstract void startCall() throws IOException;

    public abstract void completeCall() throws IOException;

    public abstract Object readReply(Class cls) throws Throwable;

    public abstract void startReply() throws Throwable;

    public abstract void completeReply() throws IOException;

    public abstract boolean readBoolean() throws IOException;

    public abstract void readNull() throws IOException;

    public abstract int readInt() throws IOException;

    public abstract long readLong() throws IOException;

    public abstract double readDouble() throws IOException;

    public abstract long readUTCDate() throws IOException;

    public abstract String readString() throws IOException;

    public abstract Reader getReader() throws IOException;

    public abstract InputStream readInputStream() throws IOException;

    public abstract byte[] readBytes() throws IOException;

    public abstract Object readObject(Class cls) throws IOException;

    public abstract Object readObject() throws IOException;

    public abstract Object readRemote() throws IOException;

    public abstract Object readRef() throws IOException;

    public abstract int addRef(Object obj) throws IOException;

    public abstract void setRef(int i, Object obj) throws IOException;

    public abstract int readListStart() throws IOException;

    public abstract int readLength() throws IOException;

    public abstract int readMapStart() throws IOException;

    public abstract String readType() throws IOException;

    public abstract boolean isEnd() throws IOException;

    public abstract void readEnd() throws IOException;

    public abstract void readMapEnd() throws IOException;

    public abstract void readListEnd() throws IOException;

    public void init(InputStream is) {
    }

    public HessianRemoteResolver getRemoteResolver() {
        return this.resolver;
    }

    public void setRemoteResolver(HessianRemoteResolver resolver) {
        this.resolver = resolver;
    }

    public void setSerializerFactory(SerializerFactory ser) {
    }

    public void skipOptionalCall() throws IOException {
    }

    public int readMethodArgLength() throws IOException {
        return -1;
    }

    public Node readNode() throws IOException {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public Object readObject(Class expectedClass, Class<?>... expectedTypes) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public Object readObject(List<Class<?>> expectedTypes) throws IOException {
        throw new UnsupportedOperationException(String.valueOf(this));
    }

    public void resetReferences() {
    }

    public void close() throws IOException {
    }
}
