package com.alibaba.com.caucho.hessian.io;

import ch.qos.logback.classic.encoder.JsonEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;
import org.w3c.dom.Node;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianInput.class */
public class HessianInput extends AbstractHessianInput {
    private static int END_OF_DATA = -2;
    private static Field _detailMessageField;
    protected SerializerFactory _serializerFactory;
    protected ArrayList _refs;
    private InputStream _is;
    private String _method;
    private Reader _chunkReader;
    private InputStream _chunkInputStream;
    private Throwable _replyFault;
    private boolean _isLastChunk;
    private int _chunkLength;
    protected int _peek = -1;
    private StringBuffer _sbuf = new StringBuffer();

    static {
        try {
            _detailMessageField = Throwable.class.getDeclaredField("detailMessage");
            _detailMessageField.setAccessible(true);
        } catch (Throwable th) {
        }
    }

    public HessianInput() {
    }

    public HessianInput(InputStream is) {
        init(is);
    }

    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void setSerializerFactory(SerializerFactory factory) {
        this._serializerFactory = factory;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void init(InputStream is) {
        this._is = is;
        this._method = null;
        this._isLastChunk = true;
        this._chunkLength = 0;
        this._peek = -1;
        this._refs = null;
        this._replyFault = null;
        if (this._serializerFactory == null) {
            this._serializerFactory = new SerializerFactory();
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String getMethod() {
        return this._method;
    }

    public Throwable getReplyFault() {
        return this._replyFault;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public boolean checkAndReadNull() {
        return this._peek == 78;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readCall() throws IOException {
        int tag = read();
        if (tag != 99) {
            throw error("expected hessian call ('c') at " + codeName(tag));
        }
        int major = read();
        int minor = read();
        return (major << 16) + minor;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void skipOptionalCall() throws IOException {
        int tag = read();
        if (tag == 99) {
            read();
            read();
        } else {
            this._peek = tag;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String readMethod() throws IOException {
        int tag = read();
        if (tag != 109) {
            throw error("expected hessian method ('m') at " + codeName(tag));
        }
        int d1 = read();
        int d2 = read();
        this._isLastChunk = true;
        this._chunkLength = (d1 * 256) + d2;
        this._sbuf.setLength(0);
        while (true) {
            int ch2 = parseChar();
            if (ch2 >= 0) {
                this._sbuf.append((char) ch2);
            } else {
                this._method = this._sbuf.toString();
                return this._method;
            }
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void startCall() throws IOException {
        readCall();
        while (readHeader() != null) {
            readObject();
        }
        readMethod();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void completeCall() throws IOException {
        int tag = read();
        if (tag != 122) {
            throw error("expected end of call ('z') at " + codeName(tag) + ".  Check method arguments and ensure method overloading is enabled if necessary");
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readReply(Class expectedClass) throws Throwable {
        int tag = read();
        if (tag != 114) {
            error("expected hessian reply at " + codeName(tag));
        }
        read();
        read();
        int tag2 = read();
        if (tag2 == 102) {
            throw prepareFault();
        }
        this._peek = tag2;
        Object value = readObject(expectedClass);
        completeValueReply();
        return value;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void startReply() throws Throwable {
        int tag = read();
        if (tag != 114) {
            error("expected hessian reply at " + codeName(tag));
        }
        read();
        read();
        int tag2 = read();
        if (tag2 == 102) {
            throw prepareFault();
        }
        this._peek = tag2;
    }

    private Throwable prepareFault() throws IOException {
        HashMap fault = readFault();
        Object detail = fault.get("detail");
        String message = (String) fault.get(JsonEncoder.MESSAGE_ATTR_NAME);
        if (detail instanceof Throwable) {
            this._replyFault = (Throwable) detail;
            if (message != null && _detailMessageField != null) {
                try {
                    _detailMessageField.set(this._replyFault, message);
                } catch (Throwable th) {
                }
            }
            return this._replyFault;
        }
        String code = (String) fault.get("code");
        this._replyFault = new HessianServiceException(message, code, detail);
        return this._replyFault;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void completeReply() throws IOException {
        int tag = read();
        if (tag != 122) {
            error("expected end of reply at " + codeName(tag));
        }
    }

    public void completeValueReply() throws IOException {
        int tag = read();
        if (tag != 122) {
            error("expected end of reply at " + codeName(tag));
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String readHeader() throws IOException {
        int tag = read();
        if (tag == 72) {
            this._isLastChunk = true;
            this._chunkLength = (read() << 8) + read();
            this._sbuf.setLength(0);
            while (true) {
                int ch2 = parseChar();
                if (ch2 >= 0) {
                    this._sbuf.append((char) ch2);
                } else {
                    return this._sbuf.toString();
                }
            }
        } else {
            this._peek = tag;
            return null;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void readNull() throws IOException {
        int tag = read();
        switch (tag) {
            case 78:
                return;
            default:
                throw expect("null", tag);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public boolean readBoolean() throws IOException {
        int tag = read();
        switch (tag) {
            case 68:
                return parseDouble() == 0.0d;
            case 69:
            case 71:
            case 72:
            case 74:
            case 75:
            case 77:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            default:
                throw expect("boolean", tag);
            case 70:
                return false;
            case 73:
                return parseInt() == 0;
            case 76:
                return parseLong() == 0;
            case 78:
                return false;
            case 84:
                return true;
        }
    }

    public short readShort() throws IOException {
        return (short) readInt();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readInt() throws IOException {
        int tag = read();
        switch (tag) {
            case 68:
                return (int) parseDouble();
            case 70:
                return 0;
            case 73:
                return parseInt();
            case 76:
                return (int) parseLong();
            case 84:
                return 1;
            default:
                throw expect("int", tag);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public long readLong() throws IOException {
        int tag = read();
        switch (tag) {
            case 68:
                return (long) parseDouble();
            case 70:
                return 0L;
            case 73:
                return parseInt();
            case 76:
                return parseLong();
            case 84:
                return 1L;
            default:
                throw expect("long", tag);
        }
    }

    public float readFloat() throws IOException {
        return (float) readDouble();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public double readDouble() throws IOException {
        int tag = read();
        switch (tag) {
            case 68:
                return parseDouble();
            case 70:
                return 0.0d;
            case 73:
                return parseInt();
            case 76:
                return parseLong();
            case 84:
                return 1.0d;
            default:
                throw expect("long", tag);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public long readUTCDate() throws IOException {
        int tag = read();
        if (tag != 100) {
            throw error("expected date at " + codeName(tag));
        }
        long b64 = read();
        long b56 = read();
        long b48 = read();
        long b40 = read();
        long b32 = read();
        long b24 = read();
        long b16 = read();
        long b8 = read();
        return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    public int readChar() throws IOException {
        if (this._chunkLength > 0) {
            this._chunkLength--;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = END_OF_DATA;
            }
            int ch2 = parseUTF8Char();
            return ch2;
        }
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = read();
        switch (tag) {
            case 78:
                return -1;
            case 83:
            case 88:
            case 115:
            case 120:
                this._isLastChunk = tag == 83 || tag == 88;
                this._chunkLength = (read() << 8) + read();
                this._chunkLength--;
                int value = parseUTF8Char();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = END_OF_DATA;
                }
                return value;
            default:
                throw new IOException("expected 'S' at " + ((char) tag));
        }
    }

    public int readString(char[] buffer, int offset, int length) throws IOException {
        int readLength = 0;
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            int tag = read();
            switch (tag) {
                case 78:
                    return -1;
                case 83:
                case 88:
                case 115:
                case 120:
                    this._isLastChunk = tag == 83 || tag == 88;
                    this._chunkLength = (read() << 8) + read();
                    break;
                default:
                    throw new IOException("expected 'S' at " + ((char) tag));
            }
        }
        while (length > 0) {
            if (this._chunkLength > 0) {
                int i = offset;
                offset++;
                buffer[i] = (char) parseUTF8Char();
                this._chunkLength--;
                length--;
                readLength++;
            } else {
                if (this._isLastChunk) {
                    if (readLength == 0) {
                        return -1;
                    }
                    this._chunkLength = END_OF_DATA;
                    return readLength;
                }
                int tag2 = read();
                switch (tag2) {
                    case 83:
                    case 88:
                    case 115:
                    case 120:
                        this._isLastChunk = tag2 == 83 || tag2 == 88;
                        this._chunkLength = (read() << 8) + read();
                        break;
                    default:
                        throw new IOException("expected 'S' at " + ((char) tag2));
                }
            }
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = END_OF_DATA;
        return readLength;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String readString() throws IOException {
        int tag = read();
        switch (tag) {
            case 68:
                return String.valueOf(parseDouble());
            case 73:
                return String.valueOf(parseInt());
            case 76:
                return String.valueOf(parseLong());
            case 78:
                return null;
            case 83:
            case 88:
            case 115:
            case 120:
                this._isLastChunk = tag == 83 || tag == 88;
                this._chunkLength = (read() << 8) + read();
                this._sbuf.setLength(0);
                while (true) {
                    int ch2 = parseChar();
                    if (ch2 >= 0) {
                        this._sbuf.append((char) ch2);
                    } else {
                        return this._sbuf.toString();
                    }
                }
                break;
            default:
                throw expect("string", tag);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Node readNode() throws IOException {
        int tag = read();
        switch (tag) {
            case 78:
                return null;
            case 83:
            case 88:
            case 115:
            case 120:
                this._isLastChunk = tag == 83 || tag == 88;
                this._chunkLength = (read() << 8) + read();
                throw error("Can't handle string in this context");
            default:
                throw expect("string", tag);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public byte[] readBytes() throws IOException {
        int tag = read();
        switch (tag) {
            case 66:
            case 98:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while (true) {
                    int data = parseByte();
                    if (data >= 0) {
                        bos.write(data);
                    } else {
                        return bos.toByteArray();
                    }
                }
            case 78:
                return null;
            default:
                throw expect("bytes", tag);
        }
    }

    public int readByte() throws IOException {
        if (this._chunkLength > 0) {
            this._chunkLength--;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = END_OF_DATA;
            }
            return read();
        }
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = read();
        switch (tag) {
            case 66:
            case 98:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                int value = parseByte();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = END_OF_DATA;
                }
                return value;
            case 78:
                return -1;
            default:
                throw new IOException("expected 'B' at " + ((char) tag));
        }
    }

    public int readBytes(byte[] buffer, int offset, int length) throws IOException {
        int readLength = 0;
        if (this._chunkLength == END_OF_DATA) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            int tag = read();
            switch (tag) {
                case 66:
                case 98:
                    this._isLastChunk = tag == 66;
                    this._chunkLength = (read() << 8) + read();
                    break;
                case 78:
                    return -1;
                default:
                    throw new IOException("expected 'B' at " + ((char) tag));
            }
        }
        while (length > 0) {
            if (this._chunkLength > 0) {
                int i = offset;
                offset++;
                buffer[i] = (byte) read();
                this._chunkLength--;
                length--;
                readLength++;
            } else {
                if (this._isLastChunk) {
                    if (readLength == 0) {
                        return -1;
                    }
                    this._chunkLength = END_OF_DATA;
                    return readLength;
                }
                int tag2 = read();
                switch (tag2) {
                    case 66:
                    case 98:
                        this._isLastChunk = tag2 == 66;
                        this._chunkLength = (read() << 8) + read();
                        break;
                    default:
                        throw new IOException("expected 'B' at " + ((char) tag2));
                }
            }
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = END_OF_DATA;
        return readLength;
    }

    private HashMap readFault() throws IOException {
        int code;
        HashMap map = new HashMap();
        int read = read();
        while (true) {
            code = read;
            if (code <= 0 || code == 122) {
                break;
            }
            this._peek = code;
            Object key = readObject();
            Object value = readObject();
            if (key != null && value != null) {
                map.put(key, value);
            }
            read = read();
        }
        if (code != 122) {
            throw expect("fault", code);
        }
        return map;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readObject(Class cl) throws IOException {
        return readObject(cl, null, null);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readObject(Class expectedClass, Class<?>... expectedTypes) throws IOException {
        if (expectedClass == null || expectedClass == Object.class) {
            return readObject();
        }
        int tag = read();
        switch (tag) {
            case 77:
                String type = readType();
                boolean keyValuePair = expectedTypes != null && expectedTypes.length == 2;
                if ("".equals(type)) {
                    return this._serializerFactory.getDeserializer(expectedClass).readMap(this, keyValuePair ? expectedTypes[0] : null, keyValuePair ? expectedTypes[1] : null);
                }
                return this._serializerFactory.getObjectDeserializer(type, expectedClass).readMap(this, keyValuePair ? expectedTypes[0] : null, keyValuePair ? expectedTypes[1] : null);
            case 78:
                return null;
            case 82:
                int ref = parseInt();
                return this._refs.get(ref);
            case 86:
                String type2 = readType();
                int length = readLength();
                Deserializer reader = this._serializerFactory.getObjectDeserializer(type2);
                boolean valueType = expectedTypes != null && expectedTypes.length == 1;
                if (expectedClass != reader.getType() && expectedClass.isAssignableFrom(reader.getType())) {
                    return reader.readList(this, length, valueType ? expectedTypes[0] : null);
                }
                Object v = this._serializerFactory.getDeserializer(expectedClass).readList(this, length, valueType ? expectedTypes[0] : null);
                return v;
            case Opcodes.FREM /* 114 */:
                String type3 = readType();
                String url = readString();
                return resolveRemote(type3, url);
            default:
                this._peek = tag;
                Object value = this._serializerFactory.getDeserializer(expectedClass).readObject(this);
                return value;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readObject() throws IOException {
        return readObject((List<Class<?>>) null);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readObject(List<Class<?>> expectedTypes) throws IOException {
        int tag = read();
        switch (tag) {
            case 66:
            case 98:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while (true) {
                    int data = parseByte();
                    if (data >= 0) {
                        bos.write(data);
                    } else {
                        return bos.toByteArray();
                    }
                }
            case 67:
            case 69:
            case 71:
            case 72:
            case 74:
            case 75:
            case 79:
            case 80:
            case 81:
            case 85:
            case 87:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            case 99:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case Opcodes.DMUL /* 107 */:
            case 108:
            case Opcodes.LDIV /* 109 */:
            case Opcodes.FDIV /* 110 */:
            case Opcodes.DDIV /* 111 */:
            case 112:
            case Opcodes.LREM /* 113 */:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            default:
                throw error("unknown code for readObject at " + codeName(tag));
            case 68:
                return Double.valueOf(parseDouble());
            case 70:
                return false;
            case 73:
                return Integer.valueOf(parseInt());
            case 76:
                return Long.valueOf(parseLong());
            case 77:
                String type = readType();
                boolean keyValuePair = expectedTypes != null && expectedTypes.size() == 2;
                return this._serializerFactory.readMap(this, type, keyValuePair ? expectedTypes.get(0) : null, keyValuePair ? expectedTypes.get(1) : null);
            case 78:
                return null;
            case 82:
                int ref = parseInt();
                return this._refs.get(ref);
            case 83:
            case 115:
                this._isLastChunk = tag == 83;
                this._chunkLength = (read() << 8) + read();
                this._sbuf.setLength(0);
                while (true) {
                    int data2 = parseChar();
                    if (data2 >= 0) {
                        this._sbuf.append((char) data2);
                    } else {
                        return this._sbuf.toString();
                    }
                }
            case 84:
                return true;
            case 86:
                String type2 = readType();
                int length = readLength();
                Deserializer reader = this._serializerFactory.getObjectDeserializer(type2);
                boolean valueType = expectedTypes != null && expectedTypes.size() == 1;
                if (List.class != reader.getType() && List.class.isAssignableFrom(reader.getType())) {
                    return reader.readList(this, length, valueType ? expectedTypes.get(0) : null);
                }
                Class clazz = type2.equals(HashSet.class.getName()) ? Set.class : List.class;
                Object v = this._serializerFactory.getDeserializer(clazz).readList(this, length, valueType ? expectedTypes.get(0) : null);
                return v;
            case 88:
            case 120:
                this._isLastChunk = tag == 88;
                this._chunkLength = (read() << 8) + read();
                return parseXML();
            case 100:
                return new Date(parseLong());
            case Opcodes.FREM /* 114 */:
                String type3 = readType();
                String url = readString();
                return resolveRemote(type3, url);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readRemote() throws IOException {
        String type = readType();
        String url = readString();
        return resolveRemote(type, url);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readRef() throws IOException {
        return this._refs.get(parseInt());
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readListStart() throws IOException {
        return read();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readMapStart() throws IOException {
        return read();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public boolean isEnd() throws IOException {
        int code = read();
        this._peek = code;
        return code < 0 || code == 122;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void readEnd() throws IOException {
        int code = read();
        if (code != 122) {
            throw error("unknown code at " + codeName(code));
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void readMapEnd() throws IOException {
        int code = read();
        if (code != 122) {
            throw error("expected end of map ('z') at " + codeName(code));
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void readListEnd() throws IOException {
        int code = read();
        if (code != 122) {
            throw error("expected end of list ('z') at " + codeName(code));
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int addRef(Object ref) {
        if (this._refs == null) {
            this._refs = new ArrayList();
        }
        this._refs.add(ref);
        return this._refs.size() - 1;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void setRef(int i, Object ref) {
        this._refs.set(i, ref);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void resetReferences() {
        if (this._refs != null) {
            this._refs.clear();
        }
    }

    public Object resolveRemote(String type, String url) throws IOException {
        HessianRemoteResolver resolver = getRemoteResolver();
        if (resolver != null) {
            return resolver.lookup(type, url);
        }
        return new HessianRemote(type, url);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String readType() throws IOException {
        int code = read();
        if (code != 116) {
            this._peek = code;
            return "";
        }
        this._isLastChunk = true;
        this._chunkLength = (read() << 8) + read();
        this._sbuf.setLength(0);
        while (true) {
            int ch2 = parseChar();
            if (ch2 >= 0) {
                this._sbuf.append((char) ch2);
            } else {
                return this._sbuf.toString();
            }
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readLength() throws IOException {
        int code = read();
        if (code != 108) {
            this._peek = code;
            return -1;
        }
        return parseInt();
    }

    private int parseInt() throws IOException {
        int b32 = read();
        int b24 = read();
        int b16 = read();
        int b8 = read();
        return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    private long parseLong() throws IOException {
        long b64 = read();
        long b56 = read();
        long b48 = read();
        long b40 = read();
        long b32 = read();
        long b24 = read();
        long b16 = read();
        long b8 = read();
        return (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
    }

    private double parseDouble() throws IOException {
        long b64 = read();
        long b56 = read();
        long b48 = read();
        long b40 = read();
        long b32 = read();
        long b24 = read();
        long b16 = read();
        long b8 = read();
        long bits = (b64 << 56) + (b56 << 48) + (b48 << 40) + (b40 << 32) + (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
        return Double.longBitsToDouble(bits);
    }

    Node parseXML() throws IOException {
        throw new UnsupportedOperationException();
    }

    private int parseChar() throws IOException {
        while (this._chunkLength <= 0) {
            if (this._isLastChunk) {
                return -1;
            }
            int code = read();
            switch (code) {
                case 83:
                case 88:
                    this._isLastChunk = true;
                    this._chunkLength = (read() << 8) + read();
                    break;
                case 115:
                case 120:
                    this._isLastChunk = false;
                    this._chunkLength = (read() << 8) + read();
                    break;
                default:
                    throw expect("string", code);
            }
        }
        this._chunkLength--;
        return parseUTF8Char();
    }

    private int parseUTF8Char() throws IOException {
        int ch2 = read();
        if (ch2 < 128) {
            return ch2;
        }
        if ((ch2 & 224) == 192) {
            int ch1 = read();
            int v = ((ch2 & 31) << 6) + (ch1 & 63);
            return v;
        }
        if ((ch2 & 240) == 224) {
            int ch12 = read();
            int ch22 = read();
            int v2 = ((ch2 & 15) << 12) + ((ch12 & 63) << 6) + (ch22 & 63);
            return v2;
        }
        throw error("bad utf-8 encoding at " + codeName(ch2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int parseByte() throws IOException {
        while (this._chunkLength <= 0) {
            if (this._isLastChunk) {
                return -1;
            }
            int code = read();
            switch (code) {
                case 66:
                    this._isLastChunk = true;
                    this._chunkLength = (read() << 8) + read();
                    break;
                case 98:
                    this._isLastChunk = false;
                    this._chunkLength = (read() << 8) + read();
                    break;
                default:
                    throw expect("byte[]", code);
            }
        }
        this._chunkLength--;
        return read();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public InputStream readInputStream() throws IOException {
        int tag = read();
        switch (tag) {
            case 66:
            case 98:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                return new InputStream() { // from class: com.alibaba.com.caucho.hessian.io.HessianInput.1
                    boolean _isClosed = false;

                    @Override // java.io.InputStream
                    public int read() throws IOException {
                        if (!this._isClosed && HessianInput.this._is != null) {
                            int ch2 = HessianInput.this.parseByte();
                            if (ch2 < 0) {
                                this._isClosed = true;
                            }
                            return ch2;
                        }
                        return -1;
                    }

                    @Override // java.io.InputStream
                    public int read(byte[] buffer, int offset, int length) throws IOException {
                        if (this._isClosed || HessianInput.this._is == null) {
                            return -1;
                        }
                        int len = HessianInput.this.read(buffer, offset, length);
                        if (len < 0) {
                            this._isClosed = true;
                        }
                        return len;
                    }

                    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                    public void close() throws IOException {
                        do {
                        } while (read() >= 0);
                        this._isClosed = true;
                    }
                };
            case 78:
                return null;
            default:
                throw expect("inputStream", tag);
        }
    }

    int read(byte[] buffer, int offset, int length) throws IOException {
        int readLength = 0;
        while (length > 0) {
            while (this._chunkLength <= 0) {
                if (this._isLastChunk) {
                    if (readLength == 0) {
                        return -1;
                    }
                    return readLength;
                }
                int code = read();
                switch (code) {
                    case 66:
                        this._isLastChunk = true;
                        this._chunkLength = (read() << 8) + read();
                        break;
                    case 98:
                        this._isLastChunk = false;
                        this._chunkLength = (read() << 8) + read();
                        break;
                    default:
                        throw expect("byte[]", code);
                }
            }
            int sublen = this._chunkLength;
            if (length < sublen) {
                sublen = length;
            }
            int sublen2 = this._is.read(buffer, offset, sublen);
            offset += sublen2;
            readLength += sublen2;
            length -= sublen2;
            this._chunkLength -= sublen2;
        }
        return readLength;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int read() throws IOException {
        if (this._peek >= 0) {
            int value = this._peek;
            this._peek = -1;
            return value;
        }
        int ch2 = this._is.read();
        return ch2;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void close() {
        this._is = null;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Reader getReader() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public IOException expect(String expect, int ch2) {
        return error("expected " + expect + " at " + codeName(ch2));
    }

    protected String codeName(int ch2) {
        if (ch2 < 0) {
            return "end of file";
        }
        return "0x" + Integer.toHexString(ch2 & Const.MAX_ARRAY_DIMENSIONS) + " (" + ((char) ch2) + ")";
    }

    protected IOException error(String message) {
        if (this._method != null) {
            return new HessianProtocolException(this._method + ": " + message);
        }
        return new HessianProtocolException(message);
    }
}
