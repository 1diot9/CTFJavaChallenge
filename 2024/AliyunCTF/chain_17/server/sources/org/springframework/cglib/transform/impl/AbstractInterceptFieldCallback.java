package org.springframework.cglib.transform.impl;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/AbstractInterceptFieldCallback.class */
public class AbstractInterceptFieldCallback implements InterceptFieldCallback {
    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public int writeInt(Object obj, String name, int oldValue, int newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public char writeChar(Object obj, String name, char oldValue, char newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public byte writeByte(Object obj, String name, byte oldValue, byte newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public boolean writeBoolean(Object obj, String name, boolean oldValue, boolean newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public short writeShort(Object obj, String name, short oldValue, short newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public float writeFloat(Object obj, String name, float oldValue, float newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public double writeDouble(Object obj, String name, double oldValue, double newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public long writeLong(Object obj, String name, long oldValue, long newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public Object writeObject(Object obj, String name, Object oldValue, Object newValue) {
        return newValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public int readInt(Object obj, String name, int oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public char readChar(Object obj, String name, char oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public byte readByte(Object obj, String name, byte oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public boolean readBoolean(Object obj, String name, boolean oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public short readShort(Object obj, String name, short oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public float readFloat(Object obj, String name, float oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public double readDouble(Object obj, String name, double oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public long readLong(Object obj, String name, long oldValue) {
        return oldValue;
    }

    @Override // org.springframework.cglib.transform.impl.InterceptFieldCallback
    public Object readObject(Object obj, String name, Object oldValue) {
        return oldValue;
    }
}
