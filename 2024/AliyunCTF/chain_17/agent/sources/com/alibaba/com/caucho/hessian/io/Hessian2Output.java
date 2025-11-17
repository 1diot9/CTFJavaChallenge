package com.alibaba.com.caucho.hessian.io;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.alibaba.com.caucho.hessian.util.IdentityIntMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Hessian2Output.class */
public class Hessian2Output extends AbstractHessianOutput implements Hessian2Constants {
    public static final int SIZE = 4096;
    protected OutputStream _os;
    private boolean _isCloseStreamOnClose;
    private HashMap _classRefs;
    private HashMap _typeRefs;
    private int _offset;
    private boolean _isStreaming;
    private final byte[] _buffer = new byte[4096];
    private IdentityIntMap _refs = new IdentityIntMap();

    static /* synthetic */ int access$008(Hessian2Output x0) {
        int i = x0._offset;
        x0._offset = i + 1;
        return i;
    }

    public Hessian2Output(OutputStream os) {
        this._os = os;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void init(OutputStream os) {
        reset();
        this._os = os;
    }

    public void reset() {
        resetReferences();
        if (this._classRefs != null) {
            this._classRefs.clear();
        }
        if (this._typeRefs != null) {
            this._typeRefs.clear();
        }
        this._offset = 0;
    }

    public boolean isCloseStreamOnClose() {
        return this._isCloseStreamOnClose;
    }

    public void setCloseStreamOnClose(boolean isClose) {
        this._isCloseStreamOnClose = isClose;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void call(String method, Object[] args) throws IOException {
        int length = args != null ? args.length : 0;
        startCall(method, length);
        for (Object obj : args) {
            writeObject(obj);
        }
        completeCall();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void startCall(String method, int length) throws IOException {
        int offset = this._offset;
        if (4096 < offset + 32) {
            flush();
            int i = this._offset;
        }
        byte[] buffer = this._buffer;
        int i2 = this._offset;
        this._offset = i2 + 1;
        buffer[i2] = 67;
        writeString(method);
        writeInt(length);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void startCall() throws IOException {
        flushIfFull();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 67;
    }

    public void startEnvelope(String method) throws IOException {
        int offset = this._offset;
        if (4096 < offset + 32) {
            flush();
            int i = this._offset;
        }
        byte[] bArr = this._buffer;
        int i2 = this._offset;
        this._offset = i2 + 1;
        bArr[i2] = 69;
        writeString(method);
    }

    public void completeEnvelope() throws IOException {
        flushIfFull();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 90;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeMethod(String method) throws IOException {
        writeString(method);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void completeCall() throws IOException {
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void startReply() throws IOException {
        writeVersion();
        flushIfFull();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 82;
    }

    public void writeVersion() throws IOException {
        flushIfFull();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 72;
        byte[] bArr2 = this._buffer;
        int i2 = this._offset;
        this._offset = i2 + 1;
        bArr2[i2] = 2;
        byte[] bArr3 = this._buffer;
        int i3 = this._offset;
        this._offset = i3 + 1;
        bArr3[i3] = 0;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void completeReply() throws IOException {
    }

    public void startMessage() throws IOException {
        flushIfFull();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 112;
        byte[] bArr2 = this._buffer;
        int i2 = this._offset;
        this._offset = i2 + 1;
        bArr2[i2] = 2;
        byte[] bArr3 = this._buffer;
        int i3 = this._offset;
        this._offset = i3 + 1;
        bArr3[i3] = 0;
    }

    public void completeMessage() throws IOException {
        flushIfFull();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 122;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeFault(String code, String message, Object detail) throws IOException {
        flushIfFull();
        writeVersion();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 70;
        byte[] bArr2 = this._buffer;
        int i2 = this._offset;
        this._offset = i2 + 1;
        bArr2[i2] = 72;
        this._refs.put(new HashMap(), this._refs.size());
        writeString("code");
        writeString(code);
        writeString(JsonEncoder.MESSAGE_ATTR_NAME);
        writeString(message);
        if (detail != null) {
            writeString("detail");
            writeObject(detail);
        }
        flushIfFull();
        byte[] bArr3 = this._buffer;
        int i3 = this._offset;
        this._offset = i3 + 1;
        bArr3[i3] = 90;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeObject(Object object) throws IOException {
        if (object == null) {
            writeNull();
        } else {
            Serializer serializer = findSerializerFactory().getSerializer(object.getClass());
            serializer.writeObject(object, this);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public boolean writeListBegin(int length, String type) throws IOException {
        flushIfFull();
        if (length < 0) {
            if (type != null) {
                byte[] bArr = this._buffer;
                int i = this._offset;
                this._offset = i + 1;
                bArr[i] = 85;
                writeType(type);
                return true;
            }
            byte[] bArr2 = this._buffer;
            int i2 = this._offset;
            this._offset = i2 + 1;
            bArr2[i2] = 87;
            return true;
        }
        if (length <= 7) {
            if (type != null) {
                byte[] bArr3 = this._buffer;
                int i3 = this._offset;
                this._offset = i3 + 1;
                bArr3[i3] = (byte) (112 + length);
                writeType(type);
                return false;
            }
            byte[] bArr4 = this._buffer;
            int i4 = this._offset;
            this._offset = i4 + 1;
            bArr4[i4] = (byte) (120 + length);
            return false;
        }
        if (type != null) {
            byte[] bArr5 = this._buffer;
            int i5 = this._offset;
            this._offset = i5 + 1;
            bArr5[i5] = 86;
            writeType(type);
        } else {
            byte[] bArr6 = this._buffer;
            int i6 = this._offset;
            this._offset = i6 + 1;
            bArr6[i6] = 88;
        }
        writeInt(length);
        return false;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeListEnd() throws IOException {
        flushIfFull();
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 90;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeMapBegin(String type) throws IOException {
        if (4096 < this._offset + 32) {
            flush();
        }
        if (type != null) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            bArr[i] = 77;
            writeType(type);
            return;
        }
        byte[] bArr2 = this._buffer;
        int i2 = this._offset;
        this._offset = i2 + 1;
        bArr2[i2] = 72;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeMapEnd() throws IOException {
        if (4096 < this._offset + 32) {
            flush();
        }
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 90;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public int writeObjectBegin(String type) throws IOException {
        if (this._classRefs == null) {
            this._classRefs = new HashMap();
        }
        Integer refV = (Integer) this._classRefs.get(type);
        if (refV != null) {
            int ref = refV.intValue();
            if (4096 < this._offset + 32) {
                flush();
            }
            if (ref <= 15) {
                byte[] bArr = this._buffer;
                int i = this._offset;
                this._offset = i + 1;
                bArr[i] = (byte) (96 + ref);
            } else {
                byte[] bArr2 = this._buffer;
                int i2 = this._offset;
                this._offset = i2 + 1;
                bArr2[i2] = 79;
                writeInt(ref);
            }
            return ref;
        }
        this._classRefs.put(type, Integer.valueOf(this._classRefs.size()));
        if (4096 < this._offset + 32) {
            flush();
        }
        byte[] bArr3 = this._buffer;
        int i3 = this._offset;
        this._offset = i3 + 1;
        bArr3[i3] = 67;
        writeString(type);
        return -1;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeClassFieldLength(int len) throws IOException {
        writeInt(len);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeObjectEnd() throws IOException {
    }

    private void writeType(String type) throws IOException {
        flushIfFull();
        int len = type.length();
        if (len == 0) {
            throw new IllegalArgumentException("empty type is not allowed");
        }
        if (this._typeRefs == null) {
            this._typeRefs = new HashMap();
        }
        Integer typeRefV = (Integer) this._typeRefs.get(type);
        if (typeRefV != null) {
            int typeRef = typeRefV.intValue();
            writeInt(typeRef);
        } else {
            this._typeRefs.put(type, Integer.valueOf(this._typeRefs.size()));
            writeString(type);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeBoolean(boolean value) throws IOException {
        if (4096 < this._offset + 16) {
            flush();
        }
        if (value) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            bArr[i] = 84;
            return;
        }
        byte[] bArr2 = this._buffer;
        int i2 = this._offset;
        this._offset = i2 + 1;
        bArr2[i2] = 70;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeInt(int value) throws IOException {
        int offset;
        int offset2 = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset2 + 16) {
            flush();
            offset2 = this._offset;
        }
        if (-16 <= value && value <= 47) {
            int i = offset2;
            offset = offset2 + 1;
            buffer[i] = (byte) (value + 144);
        } else if (-2048 <= value && value <= 2047) {
            int i2 = offset2;
            int offset3 = offset2 + 1;
            buffer[i2] = (byte) (200 + (value >> 8));
            offset = offset3 + 1;
            buffer[offset3] = (byte) value;
        } else if (-262144 <= value && value <= 262143) {
            int i3 = offset2;
            int offset4 = offset2 + 1;
            buffer[i3] = (byte) (Hessian2Constants.BC_INT_SHORT_ZERO + (value >> 16));
            int offset5 = offset4 + 1;
            buffer[offset4] = (byte) (value >> 8);
            offset = offset5 + 1;
            buffer[offset5] = (byte) value;
        } else {
            int i4 = offset2;
            int offset6 = offset2 + 1;
            buffer[i4] = 73;
            int offset7 = offset6 + 1;
            buffer[offset6] = (byte) (value >> 24);
            int offset8 = offset7 + 1;
            buffer[offset7] = (byte) (value >> 16);
            int offset9 = offset8 + 1;
            buffer[offset8] = (byte) (value >> 8);
            offset = offset9 + 1;
            buffer[offset9] = (byte) value;
        }
        this._offset = offset;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeLong(long value) throws IOException {
        int offset;
        int offset2 = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset2 + 16) {
            flush();
            offset2 = this._offset;
        }
        if (-8 <= value && value <= 15) {
            int i = offset2;
            offset = offset2 + 1;
            buffer[i] = (byte) (value + 224);
        } else if (Hessian2Constants.LONG_BYTE_MIN <= value && value <= Hessian2Constants.LONG_BYTE_MAX) {
            int i2 = offset2;
            int offset3 = offset2 + 1;
            buffer[i2] = (byte) (248 + (value >> 8));
            offset = offset3 + 1;
            buffer[offset3] = (byte) value;
        } else if (-262144 <= value && value <= 262143) {
            int i3 = offset2;
            int offset4 = offset2 + 1;
            buffer[i3] = (byte) (60 + (value >> 16));
            int offset5 = offset4 + 1;
            buffer[offset4] = (byte) (value >> 8);
            offset = offset5 + 1;
            buffer[offset5] = (byte) value;
        } else if (-2147483648L <= value && value <= 2147483647L) {
            buffer[offset2 + 0] = 89;
            buffer[offset2 + 1] = (byte) (value >> 24);
            buffer[offset2 + 2] = (byte) (value >> 16);
            buffer[offset2 + 3] = (byte) (value >> 8);
            buffer[offset2 + 4] = (byte) value;
            offset = offset2 + 5;
        } else {
            buffer[offset2 + 0] = 76;
            buffer[offset2 + 1] = (byte) (value >> 56);
            buffer[offset2 + 2] = (byte) (value >> 48);
            buffer[offset2 + 3] = (byte) (value >> 40);
            buffer[offset2 + 4] = (byte) (value >> 32);
            buffer[offset2 + 5] = (byte) (value >> 24);
            buffer[offset2 + 6] = (byte) (value >> 16);
            buffer[offset2 + 7] = (byte) (value >> 8);
            buffer[offset2 + 8] = (byte) value;
            offset = offset2 + 9;
        }
        this._offset = offset;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeDouble(double value) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset + 16) {
            flush();
            offset = this._offset;
        }
        int intValue = (int) value;
        if (intValue == value) {
            if (intValue == 0) {
                buffer[offset] = 91;
                this._offset = offset + 1;
                return;
            }
            if (intValue == 1) {
                buffer[offset] = 92;
                this._offset = offset + 1;
                return;
            } else {
                if (-128 <= intValue && intValue < 128) {
                    int i = offset;
                    int offset2 = offset + 1;
                    buffer[i] = 93;
                    buffer[offset2] = (byte) intValue;
                    this._offset = offset2 + 1;
                    return;
                }
                if (-32768 <= intValue && intValue < 32768) {
                    buffer[offset + 0] = 94;
                    buffer[offset + 1] = (byte) (intValue >> 8);
                    buffer[offset + 2] = (byte) intValue;
                    this._offset = offset + 3;
                    return;
                }
            }
        }
        int mills = (int) (value * 1000.0d);
        if (0.001d * mills == value) {
            buffer[offset + 0] = 95;
            buffer[offset + 1] = (byte) (mills >> 24);
            buffer[offset + 2] = (byte) (mills >> 16);
            buffer[offset + 3] = (byte) (mills >> 8);
            buffer[offset + 4] = (byte) mills;
            this._offset = offset + 5;
            return;
        }
        long bits = Double.doubleToLongBits(value);
        buffer[offset + 0] = 68;
        buffer[offset + 1] = (byte) (bits >> 56);
        buffer[offset + 2] = (byte) (bits >> 48);
        buffer[offset + 3] = (byte) (bits >> 40);
        buffer[offset + 4] = (byte) (bits >> 32);
        buffer[offset + 5] = (byte) (bits >> 24);
        buffer[offset + 6] = (byte) (bits >> 16);
        buffer[offset + 7] = (byte) (bits >> 8);
        buffer[offset + 8] = (byte) bits;
        this._offset = offset + 9;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeUTCDate(long time) throws IOException {
        if (4096 < this._offset + 32) {
            flush();
        }
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (time % 60000 == 0) {
            long minutes = time / 60000;
            if ((minutes >> 31) == 0 || (minutes >> 31) == -1) {
                int offset2 = offset + 1;
                buffer[offset] = 75;
                int offset3 = offset2 + 1;
                buffer[offset2] = (byte) (minutes >> 24);
                int offset4 = offset3 + 1;
                buffer[offset3] = (byte) (minutes >> 16);
                int offset5 = offset4 + 1;
                buffer[offset4] = (byte) (minutes >> 8);
                buffer[offset5] = (byte) (minutes >> 0);
                this._offset = offset5 + 1;
                return;
            }
        }
        int offset6 = offset + 1;
        buffer[offset] = 74;
        int offset7 = offset6 + 1;
        buffer[offset6] = (byte) (time >> 56);
        int offset8 = offset7 + 1;
        buffer[offset7] = (byte) (time >> 48);
        int offset9 = offset8 + 1;
        buffer[offset8] = (byte) (time >> 40);
        int offset10 = offset9 + 1;
        buffer[offset9] = (byte) (time >> 32);
        int offset11 = offset10 + 1;
        buffer[offset10] = (byte) (time >> 24);
        int offset12 = offset11 + 1;
        buffer[offset11] = (byte) (time >> 16);
        int offset13 = offset12 + 1;
        buffer[offset12] = (byte) (time >> 8);
        buffer[offset13] = (byte) time;
        this._offset = offset13 + 1;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeNull() throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset + 16) {
            flush();
            offset = this._offset;
        }
        buffer[offset] = 78;
        this._offset = offset + 1;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeString(String value) throws IOException {
        int strOffset;
        int offset;
        int offset2 = this._offset;
        byte[] buffer = this._buffer;
        if (4096 <= offset2 + 16) {
            flush();
            offset2 = this._offset;
        }
        if (value == null) {
            buffer[offset2] = 78;
            this._offset = offset2 + 1;
            return;
        }
        int length = value.length();
        int i = 0;
        while (true) {
            strOffset = i;
            if (length <= 32768) {
                break;
            }
            int sublen = 32768;
            int offset3 = this._offset;
            if (4096 <= offset3 + 16) {
                flush();
                offset3 = this._offset;
            }
            char tail = value.charAt((strOffset + 32768) - 1);
            if (55296 <= tail && tail <= 56319) {
                sublen = 32768 - 1;
            }
            buffer[offset3 + 0] = 82;
            buffer[offset3 + 1] = (byte) (sublen >> 8);
            buffer[offset3 + 2] = (byte) sublen;
            this._offset = offset3 + 3;
            printString(value, strOffset, sublen);
            length -= sublen;
            i = strOffset + sublen;
        }
        int offset4 = this._offset;
        if (4096 <= offset4 + 16) {
            flush();
            offset4 = this._offset;
        }
        if (length <= 31) {
            int i2 = offset4;
            offset = offset4 + 1;
            buffer[i2] = (byte) (0 + length);
        } else if (length <= 1023) {
            int i3 = offset4;
            int offset5 = offset4 + 1;
            buffer[i3] = (byte) (48 + (length >> 8));
            offset = offset5 + 1;
            buffer[offset5] = (byte) length;
        } else {
            int i4 = offset4;
            int offset6 = offset4 + 1;
            buffer[i4] = 83;
            int offset7 = offset6 + 1;
            buffer[offset6] = (byte) (length >> 8);
            offset = offset7 + 1;
            buffer[offset7] = (byte) length;
        }
        this._offset = offset;
        printString(value, strOffset, length);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeString(char[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            if (4096 < this._offset + 16) {
                flush();
            }
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            bArr[i] = 78;
            return;
        }
        while (length > 32768) {
            int sublen = 32768;
            if (4096 < this._offset + 16) {
                flush();
            }
            char tail = buffer[(offset + 32768) - 1];
            if (55296 <= tail && tail <= 56319) {
                sublen = 32768 - 1;
            }
            byte[] bArr2 = this._buffer;
            int i2 = this._offset;
            this._offset = i2 + 1;
            bArr2[i2] = 82;
            byte[] bArr3 = this._buffer;
            int i3 = this._offset;
            this._offset = i3 + 1;
            bArr3[i3] = (byte) (sublen >> 8);
            byte[] bArr4 = this._buffer;
            int i4 = this._offset;
            this._offset = i4 + 1;
            bArr4[i4] = (byte) sublen;
            printString(buffer, offset, sublen);
            length -= sublen;
            offset += sublen;
        }
        if (4096 < this._offset + 16) {
            flush();
        }
        if (length <= 31) {
            byte[] bArr5 = this._buffer;
            int i5 = this._offset;
            this._offset = i5 + 1;
            bArr5[i5] = (byte) (0 + length);
        } else if (length <= 1023) {
            byte[] bArr6 = this._buffer;
            int i6 = this._offset;
            this._offset = i6 + 1;
            bArr6[i6] = (byte) (48 + (length >> 8));
            byte[] bArr7 = this._buffer;
            int i7 = this._offset;
            this._offset = i7 + 1;
            bArr7[i7] = (byte) length;
        } else {
            byte[] bArr8 = this._buffer;
            int i8 = this._offset;
            this._offset = i8 + 1;
            bArr8[i8] = 83;
            byte[] bArr9 = this._buffer;
            int i9 = this._offset;
            this._offset = i9 + 1;
            bArr9[i9] = (byte) (length >> 8);
            byte[] bArr10 = this._buffer;
            int i10 = this._offset;
            this._offset = i10 + 1;
            bArr10[i10] = (byte) length;
        }
        printString(buffer, offset, length);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeBytes(byte[] buffer) throws IOException {
        if (buffer == null) {
            if (4096 < this._offset + 16) {
                flush();
            }
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            bArr[i] = 78;
            return;
        }
        writeBytes(buffer, 0, buffer.length);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeBytes(byte[] buffer, int offset, int length) throws IOException {
        if (buffer == null) {
            if (4096 < this._offset + 16) {
                flushBuffer();
            }
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            bArr[i] = 78;
            return;
        }
        flush();
        while ((4096 - this._offset) - 3 < length) {
            int sublen = (4096 - this._offset) - 3;
            if (sublen < 16) {
                flushBuffer();
                sublen = (4096 - this._offset) - 3;
                if (length < sublen) {
                    sublen = length;
                }
            }
            byte[] bArr2 = this._buffer;
            int i2 = this._offset;
            this._offset = i2 + 1;
            bArr2[i2] = 65;
            byte[] bArr3 = this._buffer;
            int i3 = this._offset;
            this._offset = i3 + 1;
            bArr3[i3] = (byte) (sublen >> 8);
            byte[] bArr4 = this._buffer;
            int i4 = this._offset;
            this._offset = i4 + 1;
            bArr4[i4] = (byte) sublen;
            System.arraycopy(buffer, offset, this._buffer, this._offset, sublen);
            this._offset += sublen;
            length -= sublen;
            offset += sublen;
            flushBuffer();
        }
        if (4096 < this._offset + 16) {
            flushBuffer();
        }
        if (length <= 15) {
            byte[] bArr5 = this._buffer;
            int i5 = this._offset;
            this._offset = i5 + 1;
            bArr5[i5] = (byte) (32 + length);
        } else if (length <= 1023) {
            byte[] bArr6 = this._buffer;
            int i6 = this._offset;
            this._offset = i6 + 1;
            bArr6[i6] = (byte) (52 + (length >> 8));
            byte[] bArr7 = this._buffer;
            int i7 = this._offset;
            this._offset = i7 + 1;
            bArr7[i7] = (byte) length;
        } else {
            byte[] bArr8 = this._buffer;
            int i8 = this._offset;
            this._offset = i8 + 1;
            bArr8[i8] = 66;
            byte[] bArr9 = this._buffer;
            int i9 = this._offset;
            this._offset = i9 + 1;
            bArr9[i9] = (byte) (length >> 8);
            byte[] bArr10 = this._buffer;
            int i10 = this._offset;
            this._offset = i10 + 1;
            bArr10[i10] = (byte) length;
        }
        System.arraycopy(buffer, offset, this._buffer, this._offset, length);
        this._offset += length;
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
            flush();
            this._os.write(65);
            this._os.write(sublen >> 8);
            this._os.write(sublen);
            this._os.write(buffer, offset, sublen);
            length -= sublen;
            offset += sublen;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void writeByteBufferEnd(byte[] buffer, int offset, int length) throws IOException {
        writeBytes(buffer, offset, length);
    }

    public OutputStream getBytesOutputStream() throws IOException {
        return new BytesOutputStream();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    protected void writeRef(int value) throws IOException {
        if (4096 < this._offset + 16) {
            flush();
        }
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        bArr[i] = 81;
        writeInt(value);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public boolean addRef(Object object) throws IOException {
        int ref = this._refs.get(object);
        if (ref >= 0) {
            writeRef(ref);
            return true;
        }
        this._refs.put(object, this._refs.size());
        return false;
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
        Integer value = Integer.valueOf(this._refs.remove(oldRef));
        if (value != null) {
            this._refs.put(newRef, value.intValue());
            return true;
        }
        return false;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public void resetReferences() {
        if (this._refs != null) {
            this._refs.clear();
        }
    }

    public void writeStreamingObject(Object obj) throws IOException {
        startStreamingPacket();
        writeObject(obj);
        endStreamingPacket();
    }

    public void startStreamingPacket() throws IOException {
        if (this._refs != null) {
            this._refs.clear();
        }
        flush();
        this._isStreaming = true;
        this._offset = 3;
    }

    public void endStreamingPacket() throws IOException {
        int len = this._offset - 3;
        this._buffer[0] = 80;
        this._buffer[1] = (byte) (len >> 8);
        this._buffer[2] = (byte) len;
        this._isStreaming = false;
        flush();
    }

    public void printLenString(String v) throws IOException {
        if (4096 < this._offset + 16) {
            flush();
        }
        if (v == null) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            bArr[i] = 0;
            byte[] bArr2 = this._buffer;
            int i2 = this._offset;
            this._offset = i2 + 1;
            bArr2[i2] = 0;
            return;
        }
        int len = v.length();
        byte[] bArr3 = this._buffer;
        int i3 = this._offset;
        this._offset = i3 + 1;
        bArr3[i3] = (byte) (len >> 8);
        byte[] bArr4 = this._buffer;
        int i4 = this._offset;
        this._offset = i4 + 1;
        bArr4[i4] = (byte) len;
        printString(v, 0, len);
    }

    public void printString(String v) throws IOException {
        printString(v, 0, v.length());
    }

    public void printString(String v, int strOffset, int length) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        for (int i = 0; i < length; i++) {
            if (4096 <= offset + 16) {
                this._offset = offset;
                flush();
                offset = this._offset;
            }
            char ch2 = v.charAt(i + strOffset);
            if (ch2 < 128) {
                int i2 = offset;
                offset++;
                buffer[i2] = (byte) ch2;
            } else if (ch2 < 2048) {
                int i3 = offset;
                int offset2 = offset + 1;
                buffer[i3] = (byte) (Opcodes.CHECKCAST + ((ch2 >> 6) & 31));
                offset = offset2 + 1;
                buffer[offset2] = (byte) (128 + (ch2 & '?'));
            } else {
                int i4 = offset;
                int offset3 = offset + 1;
                buffer[i4] = (byte) (224 + ((ch2 >> '\f') & 15));
                int offset4 = offset3 + 1;
                buffer[offset3] = (byte) (128 + ((ch2 >> 6) & 63));
                offset = offset4 + 1;
                buffer[offset4] = (byte) (128 + (ch2 & '?'));
            }
        }
        this._offset = offset;
    }

    public void printString(char[] v, int strOffset, int length) throws IOException {
        int offset = this._offset;
        byte[] buffer = this._buffer;
        for (int i = 0; i < length; i++) {
            if (4096 <= offset + 16) {
                this._offset = offset;
                flush();
                offset = this._offset;
            }
            char ch2 = v[i + strOffset];
            if (ch2 < 128) {
                int i2 = offset;
                offset++;
                buffer[i2] = (byte) ch2;
            } else if (ch2 < 2048) {
                int i3 = offset;
                int offset2 = offset + 1;
                buffer[i3] = (byte) (Opcodes.CHECKCAST + ((ch2 >> 6) & 31));
                offset = offset2 + 1;
                buffer[offset2] = (byte) (128 + (ch2 & '?'));
            } else {
                int i4 = offset;
                int offset3 = offset + 1;
                buffer[i4] = (byte) (224 + ((ch2 >> '\f') & 15));
                int offset4 = offset3 + 1;
                buffer[offset3] = (byte) (128 + ((ch2 >> 6) & 63));
                offset = offset4 + 1;
                buffer[offset4] = (byte) (128 + (ch2 & '?'));
            }
        }
        this._offset = offset;
    }

    private final void flushIfFull() throws IOException {
        int offset = this._offset;
        if (4096 < offset + 32) {
            this._offset = 0;
            this._os.write(this._buffer, 0, offset);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public final void flush() throws IOException {
        flushBuffer();
        if (this._os != null) {
            this._os.flush();
        }
    }

    public final void flushBuffer() throws IOException {
        int offset = this._offset;
        if (!this._isStreaming && offset > 0) {
            this._offset = 0;
            this._os.write(this._buffer, 0, offset);
        } else if (this._isStreaming && offset > 3) {
            int len = offset - 3;
            this._buffer[0] = 112;
            this._buffer[1] = (byte) (len >> 8);
            this._buffer[2] = (byte) len;
            this._offset = 3;
            this._os.write(this._buffer, 0, offset);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianOutput
    public final void close() throws IOException {
        flush();
        OutputStream os = this._os;
        this._os = null;
        if (os != null && this._isCloseStreamOnClose) {
            os.close();
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Hessian2Output$BytesOutputStream.class */
    class BytesOutputStream extends OutputStream {
        private int _startOffset;

        BytesOutputStream() throws IOException {
            if (4096 < Hessian2Output.this._offset + 16) {
                Hessian2Output.this.flush();
            }
            this._startOffset = Hessian2Output.this._offset;
            Hessian2Output.this._offset += 3;
        }

        @Override // java.io.OutputStream
        public void write(int ch2) throws IOException {
            if (4096 <= Hessian2Output.this._offset) {
                int length = (Hessian2Output.this._offset - this._startOffset) - 3;
                Hessian2Output.this._buffer[this._startOffset] = 65;
                Hessian2Output.this._buffer[this._startOffset + 1] = (byte) (length >> 8);
                Hessian2Output.this._buffer[this._startOffset + 2] = (byte) length;
                Hessian2Output.this.flush();
                this._startOffset = Hessian2Output.this._offset;
                Hessian2Output.this._offset += 3;
            }
            Hessian2Output.this._buffer[Hessian2Output.access$008(Hessian2Output.this)] = (byte) ch2;
        }

        @Override // java.io.OutputStream
        public void write(byte[] buffer, int offset, int length) throws IOException {
            while (length > 0) {
                int sublen = 4096 - Hessian2Output.this._offset;
                if (length < sublen) {
                    sublen = length;
                }
                if (sublen > 0) {
                    System.arraycopy(buffer, offset, Hessian2Output.this._buffer, Hessian2Output.this._offset, sublen);
                    Hessian2Output.this._offset += sublen;
                }
                length -= sublen;
                offset += sublen;
                if (4096 <= Hessian2Output.this._offset) {
                    int chunkLength = (Hessian2Output.this._offset - this._startOffset) - 3;
                    Hessian2Output.this._buffer[this._startOffset] = 65;
                    Hessian2Output.this._buffer[this._startOffset + 1] = (byte) (chunkLength >> 8);
                    Hessian2Output.this._buffer[this._startOffset + 2] = (byte) chunkLength;
                    Hessian2Output.this.flush();
                    this._startOffset = Hessian2Output.this._offset;
                    Hessian2Output.this._offset += 3;
                }
            }
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            int startOffset = this._startOffset;
            this._startOffset = -1;
            if (startOffset >= 0) {
                int length = (Hessian2Output.this._offset - startOffset) - 3;
                Hessian2Output.this._buffer[startOffset] = 66;
                Hessian2Output.this._buffer[startOffset + 1] = (byte) (length >> 8);
                Hessian2Output.this._buffer[startOffset + 2] = (byte) length;
                Hessian2Output.this.flush();
            }
        }
    }
}
