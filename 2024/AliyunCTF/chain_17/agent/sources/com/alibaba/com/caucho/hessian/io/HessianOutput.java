package com.alibaba.com.caucho.hessian.io;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.io.IOException;
import java.io.OutputStream;
import java.util.IdentityHashMap;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianOutput.class */
public class HessianOutput extends AbstractHessianOutput {
    protected OutputStream os;
    private IdentityHashMap _refs;
    private int _version = 1;

    public HessianOutput(OutputStream os) {
        init(os);
    }

    public HessianOutput() {
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void init(OutputStream os) {
        this.os = os;
        this._refs = null;
        if (this._serializerFactory == null) {
            this._serializerFactory = new SerializerFactory();
        }
    }

    public void setVersion(int version) {
        this._version = version;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void call(String method, Object[] args) throws IOException {
        int length = args != null ? args.length : 0;
        startCall(method, length);
        for (int i = 0; i < length; i++) {
            writeObject(args[i]);
        }
        completeCall();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void startCall(String method, int length) throws IOException {
        this.os.write(99);
        this.os.write(this._version);
        this.os.write(0);
        this.os.write(Opcodes.LDIV);
        int len = method.length();
        this.os.write(len >> 8);
        this.os.write(len);
        printString(method, 0, len);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void startCall() throws IOException {
        this.os.write(99);
        this.os.write(0);
        this.os.write(1);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeMethod(String method) throws IOException {
        this.os.write(Opcodes.LDIV);
        int len = method.length();
        this.os.write(len >> 8);
        this.os.write(len);
        printString(method, 0, len);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void completeCall() throws IOException {
        this.os.write(122);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void startReply() throws IOException {
        this.os.write(Opcodes.FREM);
        this.os.write(1);
        this.os.write(0);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void completeReply() throws IOException {
        this.os.write(122);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeHeader(String name) throws IOException {
        int len = name.length();
        this.os.write(72);
        this.os.write(len >> 8);
        this.os.write(len);
        printString(name);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeFault(String code, String message, Object detail) throws IOException {
        this.os.write(102);
        writeString("code");
        writeString(code);
        writeString(JsonEncoder.MESSAGE_ATTR_NAME);
        writeString(message);
        if (detail != null) {
            writeString("detail");
            writeObject(detail);
        }
        this.os.write(122);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeObject(Object object) throws IOException {
        if (object == null) {
            writeNull();
        } else {
            Serializer serializer = this._serializerFactory.getSerializer(object.getClass());
            serializer.writeObject(object, this);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public boolean writeListBegin(int length, String type) throws IOException {
        this.os.write(86);
        if (type != null) {
            this.os.write(116);
            printLenString(type);
        }
        if (length >= 0) {
            this.os.write(108);
            this.os.write(length >> 24);
            this.os.write(length >> 16);
            this.os.write(length >> 8);
            this.os.write(length);
            return true;
        }
        return true;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeListEnd() throws IOException {
        this.os.write(122);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeMapBegin(String type) throws IOException {
        this.os.write(77);
        this.os.write(116);
        printLenString(type);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeMapEnd() throws IOException {
        this.os.write(122);
    }

    public void writeRemote(String type, String url) throws IOException {
        this.os.write(Opcodes.FREM);
        this.os.write(116);
        printLenString(type);
        this.os.write(83);
        printLenString(url);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeBoolean(boolean value) throws IOException {
        if (value) {
            this.os.write(84);
        } else {
            this.os.write(70);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeInt(int value) throws IOException {
        this.os.write(73);
        this.os.write(value >> 24);
        this.os.write(value >> 16);
        this.os.write(value >> 8);
        this.os.write(value);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeLong(long value) throws IOException {
        this.os.write(76);
        this.os.write((byte) (value >> 56));
        this.os.write((byte) (value >> 48));
        this.os.write((byte) (value >> 40));
        this.os.write((byte) (value >> 32));
        this.os.write((byte) (value >> 24));
        this.os.write((byte) (value >> 16));
        this.os.write((byte) (value >> 8));
        this.os.write((byte) value);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeDouble(double value) throws IOException {
        long bits = Double.doubleToLongBits(value);
        this.os.write(68);
        this.os.write((byte) (bits >> 56));
        this.os.write((byte) (bits >> 48));
        this.os.write((byte) (bits >> 40));
        this.os.write((byte) (bits >> 32));
        this.os.write((byte) (bits >> 24));
        this.os.write((byte) (bits >> 16));
        this.os.write((byte) (bits >> 8));
        this.os.write((byte) bits);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeUTCDate(long time) throws IOException {
        this.os.write(100);
        this.os.write((byte) (time >> 56));
        this.os.write((byte) (time >> 48));
        this.os.write((byte) (time >> 40));
        this.os.write((byte) (time >> 32));
        this.os.write((byte) (time >> 24));
        this.os.write((byte) (time >> 16));
        this.os.write((byte) (time >> 8));
        this.os.write((byte) time);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeNull() throws IOException {
        this.os.write(78);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeString(String value) throws IOException {
        if (value == null) {
            this.os.write(78);
            return;
        }
        int length = value.length();
        int i = 0;
        while (true) {
            int offset = i;
            if (length > 32768) {
                int sublen = 32768;
                char tail = value.charAt((offset + 32768) - 1);
                if (55296 <= tail && tail <= 56319) {
                    sublen = 32768 - 1;
                }
                this.os.write(115);
                this.os.write(sublen >> 8);
                this.os.write(sublen);
                printString(value, offset, sublen);
                length -= sublen;
                i = offset + sublen;
            } else {
                this.os.write(83);
                this.os.write(length >> 8);
                this.os.write(length);
                printString(value, offset, length);
                return;
            }
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeString(char[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            this.os.write(78);
            return;
        }
        while (length > 32768) {
            int sublen = 32768;
            char tail = buffer[(offset + 32768) - 1];
            if (55296 <= tail && tail <= 56319) {
                sublen = 32768 - 1;
            }
            this.os.write(115);
            this.os.write(sublen >> 8);
            this.os.write(sublen);
            printString(buffer, offset, sublen);
            length -= sublen;
            offset += sublen;
        }
        this.os.write(83);
        this.os.write(length >> 8);
        this.os.write(length);
        printString(buffer, offset, length);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeBytes(byte[] buffer) throws IOException {
        if (buffer == null) {
            this.os.write(78);
        } else {
            writeBytes(buffer, 0, buffer.length);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            this.os.write(78);
            return;
        }
        while (length > 32768) {
            this.os.write(98);
            this.os.write(32768 >> 8);
            this.os.write(32768);
            this.os.write(buffer, offset, 32768);
            length -= 32768;
            offset += 32768;
        }
        this.os.write(66);
        this.os.write(length >> 8);
        this.os.write(length);
        this.os.write(buffer, offset, length);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeByteBufferStart() throws IOException {
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeByteBufferPart(byte[] buffer, int offset, int length) throws IOException {
        while (length > 0) {
            int sublen = length;
            if (32768 < sublen) {
                sublen = 32768;
            }
            this.os.write(98);
            this.os.write(sublen >> 8);
            this.os.write(sublen);
            this.os.write(buffer, offset, sublen);
            length -= sublen;
            offset += sublen;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException {
        writeBytes(buffer, offset, length);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeRef(int value) throws IOException {
        this.os.write(82);
        this.os.write(value >> 24);
        this.os.write(value >> 16);
        this.os.write(value >> 8);
        this.os.write(value);
    }

    public void writePlaceholder() throws IOException {
        this.os.write(80);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public boolean addRef(Object object) throws IOException {
        if (this._refs == null) {
            this._refs = new IdentityHashMap();
        }
        Integer ref = (Integer) this._refs.get(object);
        if (ref != null) {
            int value = ref.intValue();
            writeRef(value);
            return true;
        }
        this._refs.put(object, new Integer(this._refs.size()));
        return false;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void resetReferences() {
        if (this._refs != null) {
            this._refs.clear();
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public boolean removeRef(Object obj) throws IOException {
        if (this._refs != null) {
            this._refs.remove(obj);
            return true;
        }
        return false;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public boolean replaceRef(Object oldRef, Object newRef) throws IOException {
        Integer value = (Integer) this._refs.remove(oldRef);
        if (value != null) {
            this._refs.put(newRef, value);
            return true;
        }
        return false;
    }

    public void printLenString(String v) throws IOException {
        if (v == null) {
            this.os.write(0);
            this.os.write(0);
        } else {
            int len = v.length();
            this.os.write(len >> 8);
            this.os.write(len);
            printString(v, 0, len);
        }
    }

    public void printString(String v) throws IOException {
        printString(v, 0, v.length());
    }

    public void printString(String v, int offset, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            char ch2 = v.charAt(i + offset);
            if (ch2 < 128) {
                this.os.write(ch2);
            } else if (ch2 < 2048) {
                this.os.write(Opcodes.CHECKCAST + ((ch2 >> 6) & 31));
                this.os.write(128 + (ch2 & '?'));
            } else {
                this.os.write(224 + ((ch2 >> '\f') & 15));
                this.os.write(128 + ((ch2 >> 6) & 63));
                this.os.write(128 + (ch2 & '?'));
            }
        }
    }

    public void printString(char[] v, int offset, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            char ch2 = v[i + offset];
            if (ch2 < 128) {
                this.os.write(ch2);
            } else if (ch2 < 2048) {
                this.os.write(Opcodes.CHECKCAST + ((ch2 >> 6) & 31));
                this.os.write(128 + (ch2 & '?'));
            } else {
                this.os.write(224 + ((ch2 >> '\f') & 15));
                this.os.write(128 + ((ch2 >> 6) & 63));
                this.os.write(128 + (ch2 & '?'));
            }
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void flush() throws IOException {
        if (this.os != null) {
            this.os.flush();
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void close() throws IOException {
        if (this.os != null) {
            this.os.flush();
        }
    }
}
