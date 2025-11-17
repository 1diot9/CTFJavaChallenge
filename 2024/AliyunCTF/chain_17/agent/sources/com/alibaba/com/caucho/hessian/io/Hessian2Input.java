package com.alibaba.com.caucho.hessian.io;

import ch.qos.logback.classic.encoder.JsonEncoder;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.catalina.servlets.WebdavStatus;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;
import org.w3c.dom.Node;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Hessian2Input.class */
public class Hessian2Input extends AbstractHessianInput implements Hessian2Constants {
    private static final Logger log = Logger.getLogger(Hessian2Input.class.getName());
    private static final double D_256 = 0.00390625d;
    private static final int END_OF_DATA = -2;
    private static final int SIZE = 256;
    private static final int GAP = 16;
    private static Field _detailMessageField;
    private static boolean _isCloseStreamOnClose;
    protected SerializerFactory _serializerFactory;
    protected ArrayList _refs;
    protected ArrayList _classDefs;
    protected ArrayList _types;
    private InputStream _is;
    private int _offset;
    private int _length;
    private boolean _isStreaming;
    private String _method;
    private Throwable _replyFault;
    private boolean _isLastChunk;
    private int _chunkLength;
    private final byte[] _buffer = new byte[256];
    private StringBuilder _sbuf = new StringBuilder();

    static {
        try {
            _detailMessageField = Throwable.class.getDeclaredField("detailMessage");
            _detailMessageField.setAccessible(true);
        } catch (Throwable th) {
        }
    }

    public Hessian2Input(InputStream is) {
        this._is = is;
    }

    public SerializerFactory getSerializerFactory() {
        return this._serializerFactory;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
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

    public boolean isCloseStreamOnClose() {
        return _isCloseStreamOnClose;
    }

    public void setCloseStreamOnClose(boolean isClose) {
        _isCloseStreamOnClose = isClose;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String getMethod() {
        return this._method;
    }

    public Throwable getReplyFault() {
        return this._replyFault;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void init(InputStream is) {
        this._is = is;
        reset();
    }

    public void reset() {
        resetReferences();
        if (this._classDefs != null) {
            this._classDefs.clear();
        }
        if (this._types != null) {
            this._types.clear();
        }
        this._offset = 0;
        this._length = 0;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public boolean checkAndReadNull() {
        try {
            int tag = read();
            if (78 == tag) {
                return true;
            }
            if (-1 != tag) {
                this._offset--;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readCall() throws IOException {
        int tag = read();
        if (tag != 67) {
            throw error("expected hessian call ('C') at " + codeName(tag));
        }
        return 0;
    }

    public int readEnvelope() throws IOException {
        int tag = read();
        int version = 0;
        if (tag == 72) {
            int major = read();
            int minor = read();
            version = (major << 16) + minor;
            tag = read();
        }
        if (tag != 69) {
            throw error("expected hessian Envelope ('E') at " + codeName(tag));
        }
        return version;
    }

    public void completeEnvelope() throws IOException {
        int tag = read();
        if (tag != 90) {
            error("expected end of envelope at " + codeName(tag));
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String readMethod() throws IOException {
        this._method = readString();
        return this._method;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readMethodArgLength() throws IOException {
        return readInt();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void startCall() throws IOException {
        readCall();
        readMethod();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void completeCall() throws IOException {
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readReply(Class expectedClass) throws Throwable {
        int tag = read();
        if (tag == 82) {
            return readObject(expectedClass);
        }
        if (tag == 70) {
            HashMap map = (HashMap) readObject(HashMap.class);
            throw prepareFault(map);
        }
        StringBuilder sb = new StringBuilder();
        sb.append((char) tag);
        while (true) {
            try {
                int ch2 = read();
                if (ch2 < 0) {
                    break;
                }
                sb.append((char) ch2);
            } catch (IOException e) {
                log.log(Level.FINE, e.toString(), (Throwable) e);
            }
        }
        throw error("expected hessian reply at " + codeName(tag) + StrPool.LF + ((Object) sb));
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void startReply() throws Throwable {
        readReply(Object.class);
    }

    private Throwable prepareFault(HashMap fault) throws IOException {
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
    }

    public void completeValueReply() throws IOException {
        int tag = read();
        if (tag != 90) {
            error("expected end of reply at " + codeName(tag));
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String readHeader() throws IOException {
        return null;
    }

    public int startMessage() throws IOException {
        int tag = read();
        if (tag != 112 && tag != 80) {
            throw error("expected Hessian message ('p') at " + codeName(tag));
        }
        int major = read();
        int minor = read();
        return (major << 16) + minor;
    }

    public void completeMessage() throws IOException {
        int tag = read();
        if (tag != 90) {
            error("expected end of message at " + codeName(tag));
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
        int read;
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int tag = read;
        switch (tag) {
            case 56:
            case 57:
            case 58:
            case 59:
            case 61:
            case 62:
            case 63:
                read();
                read();
                return true;
            case 60:
                return (256 * read()) + read() != 0;
            case 64:
            case 65:
            case 66:
            case 67:
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
            case 85:
            case 86:
            case 87:
            case 88:
            case 90:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
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
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            default:
                throw expect("boolean", tag);
            case 68:
                return parseDouble() != 0.0d;
            case 70:
                return false;
            case 73:
                return parseInt() != 0;
            case 76:
                return parseLong() != 0;
            case 78:
                return false;
            case 84:
                return true;
            case 89:
                return (((16777216 * ((long) read())) + (65536 * ((long) read()))) + ((long) (256 * read()))) + ((long) read()) != 0;
            case 91:
                return false;
            case 92:
                return true;
            case 93:
                return read() != 0;
            case 94:
                return (256 * read()) + read() != 0;
            case 95:
                int mills = parseInt();
                return mills != 0;
            case 128:
            case Opcodes.LOR /* 129 */:
            case 130:
            case Opcodes.LXOR /* 131 */:
            case Opcodes.IINC /* 132 */:
            case Opcodes.I2L /* 133 */:
            case Opcodes.I2F /* 134 */:
            case Opcodes.I2D /* 135 */:
            case 136:
            case Opcodes.L2F /* 137 */:
            case Opcodes.L2D /* 138 */:
            case Opcodes.F2I /* 139 */:
            case Opcodes.F2L /* 140 */:
            case Opcodes.F2D /* 141 */:
            case Opcodes.D2I /* 142 */:
            case Opcodes.D2L /* 143 */:
            case 144:
            case Opcodes.I2B /* 145 */:
            case Opcodes.I2C /* 146 */:
            case Opcodes.I2S /* 147 */:
            case Opcodes.LCMP /* 148 */:
            case Opcodes.FCMPL /* 149 */:
            case 150:
            case Opcodes.DCMPL /* 151 */:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case Opcodes.IF_ICMPEQ /* 159 */:
            case 160:
            case Opcodes.IF_ICMPLT /* 161 */:
            case Opcodes.IF_ICMPGE /* 162 */:
            case Opcodes.IF_ICMPGT /* 163 */:
            case Opcodes.IF_ICMPLE /* 164 */:
            case Opcodes.IF_ACMPEQ /* 165 */:
            case Opcodes.IF_ACMPNE /* 166 */:
            case Opcodes.GOTO /* 167 */:
            case 168:
            case Opcodes.RET /* 169 */:
            case Opcodes.TABLESWITCH /* 170 */:
            case Opcodes.LOOKUPSWITCH /* 171 */:
            case Opcodes.IRETURN /* 172 */:
            case Opcodes.LRETURN /* 173 */:
            case Opcodes.FRETURN /* 174 */:
            case Opcodes.DRETURN /* 175 */:
            case 176:
            case Opcodes.RETURN /* 177 */:
            case Opcodes.GETSTATIC /* 178 */:
            case Opcodes.PUTSTATIC /* 179 */:
            case Opcodes.GETFIELD /* 180 */:
            case Opcodes.PUTFIELD /* 181 */:
            case Opcodes.INVOKEVIRTUAL /* 182 */:
            case Opcodes.INVOKESPECIAL /* 183 */:
            case 184:
            case Opcodes.INVOKEINTERFACE /* 185 */:
            case Opcodes.INVOKEDYNAMIC /* 186 */:
            case Opcodes.NEW /* 187 */:
            case Opcodes.NEWARRAY /* 188 */:
            case Opcodes.ANEWARRAY /* 189 */:
            case Opcodes.ARRAYLENGTH /* 190 */:
            case Opcodes.ATHROW /* 191 */:
                return tag != 144;
            case Opcodes.CHECKCAST /* 192 */:
            case Opcodes.INSTANCEOF /* 193 */:
            case Opcodes.MONITORENTER /* 194 */:
            case Opcodes.MONITOREXIT /* 195 */:
            case 196:
            case Opcodes.MULTIANEWARRAY /* 197 */:
            case Opcodes.IFNULL /* 198 */:
            case Opcodes.IFNONNULL /* 199 */:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                read();
                return true;
            case 200:
                return read() != 0;
            case 208:
            case 209:
            case 210:
            case 211:
            case 213:
            case 214:
            case 215:
                read();
                read();
                return true;
            case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
                return (256 * read()) + read() != 0;
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
                return tag != 224;
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case Const.MAX_ARRAY_DIMENSIONS /* 255 */:
                read();
                return true;
            case Hessian2Constants.BC_LONG_BYTE_ZERO /* 248 */:
                return read() != 0;
        }
    }

    public short readShort() throws IOException {
        return (short) readInt();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public final int readInt() throws IOException {
        int i;
        int read = read();
        switch (read) {
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return ((read - 60) << 16) + (256 * read()) + read();
            case 64:
            case 65:
            case 66:
            case 67:
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
            case 85:
            case 86:
            case 87:
            case 88:
            case 90:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
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
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            default:
                throw expect("integer", read);
            case 68:
                return (int) parseDouble();
            case 70:
                return 0;
            case 73:
            case 89:
                return (read() << 24) + (read() << 16) + (read() << 8) + read();
            case 76:
                return (int) parseLong();
            case 78:
                return 0;
            case 84:
                return 1;
            case 91:
                return 0;
            case 92:
                return 1;
            case 93:
                if (this._offset < this._length) {
                    byte[] bArr = this._buffer;
                    int i2 = this._offset;
                    this._offset = i2 + 1;
                    i = bArr[i2];
                } else {
                    i = read();
                }
                return (byte) i;
            case 94:
                return (short) ((256 * read()) + read());
            case 95:
                return (int) (0.001d * parseInt());
            case 128:
            case Opcodes.LOR /* 129 */:
            case 130:
            case Opcodes.LXOR /* 131 */:
            case Opcodes.IINC /* 132 */:
            case Opcodes.I2L /* 133 */:
            case Opcodes.I2F /* 134 */:
            case Opcodes.I2D /* 135 */:
            case 136:
            case Opcodes.L2F /* 137 */:
            case Opcodes.L2D /* 138 */:
            case Opcodes.F2I /* 139 */:
            case Opcodes.F2L /* 140 */:
            case Opcodes.F2D /* 141 */:
            case Opcodes.D2I /* 142 */:
            case Opcodes.D2L /* 143 */:
            case 144:
            case Opcodes.I2B /* 145 */:
            case Opcodes.I2C /* 146 */:
            case Opcodes.I2S /* 147 */:
            case Opcodes.LCMP /* 148 */:
            case Opcodes.FCMPL /* 149 */:
            case 150:
            case Opcodes.DCMPL /* 151 */:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case Opcodes.IF_ICMPEQ /* 159 */:
            case 160:
            case Opcodes.IF_ICMPLT /* 161 */:
            case Opcodes.IF_ICMPGE /* 162 */:
            case Opcodes.IF_ICMPGT /* 163 */:
            case Opcodes.IF_ICMPLE /* 164 */:
            case Opcodes.IF_ACMPEQ /* 165 */:
            case Opcodes.IF_ACMPNE /* 166 */:
            case Opcodes.GOTO /* 167 */:
            case 168:
            case Opcodes.RET /* 169 */:
            case Opcodes.TABLESWITCH /* 170 */:
            case Opcodes.LOOKUPSWITCH /* 171 */:
            case Opcodes.IRETURN /* 172 */:
            case Opcodes.LRETURN /* 173 */:
            case Opcodes.FRETURN /* 174 */:
            case Opcodes.DRETURN /* 175 */:
            case 176:
            case Opcodes.RETURN /* 177 */:
            case Opcodes.GETSTATIC /* 178 */:
            case Opcodes.PUTSTATIC /* 179 */:
            case Opcodes.GETFIELD /* 180 */:
            case Opcodes.PUTFIELD /* 181 */:
            case Opcodes.INVOKEVIRTUAL /* 182 */:
            case Opcodes.INVOKESPECIAL /* 183 */:
            case 184:
            case Opcodes.INVOKEINTERFACE /* 185 */:
            case Opcodes.INVOKEDYNAMIC /* 186 */:
            case Opcodes.NEW /* 187 */:
            case Opcodes.NEWARRAY /* 188 */:
            case Opcodes.ANEWARRAY /* 189 */:
            case Opcodes.ARRAYLENGTH /* 190 */:
            case Opcodes.ATHROW /* 191 */:
                return read - 144;
            case Opcodes.CHECKCAST /* 192 */:
            case Opcodes.INSTANCEOF /* 193 */:
            case Opcodes.MONITORENTER /* 194 */:
            case Opcodes.MONITOREXIT /* 195 */:
            case 196:
            case Opcodes.MULTIANEWARRAY /* 197 */:
            case Opcodes.IFNULL /* 198 */:
            case Opcodes.IFNONNULL /* 199 */:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                return ((read - 200) << 8) + read();
            case 208:
            case 209:
            case 210:
            case 211:
            case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
            case 213:
            case 214:
            case 215:
                return ((read - Hessian2Constants.BC_INT_SHORT_ZERO) << 16) + (256 * read()) + read();
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
                return read - 224;
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case Hessian2Constants.BC_LONG_BYTE_ZERO /* 248 */:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case Const.MAX_ARRAY_DIMENSIONS /* 255 */:
                return ((read - Hessian2Constants.BC_LONG_BYTE_ZERO) << 8) + read();
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public long readLong() throws IOException {
        int i;
        int read = read();
        switch (read) {
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return ((read - 60) << 16) + (256 * read()) + read();
            case 64:
            case 65:
            case 66:
            case 67:
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
            case 85:
            case 86:
            case 87:
            case 88:
            case 90:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
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
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            default:
                throw expect("long", read);
            case 68:
                return (long) parseDouble();
            case 70:
                return 0L;
            case 73:
            case 89:
                return parseInt();
            case 76:
                return parseLong();
            case 78:
                return 0L;
            case 84:
                return 1L;
            case 91:
                return 0L;
            case 92:
                return 1L;
            case 93:
                if (this._offset < this._length) {
                    byte[] bArr = this._buffer;
                    int i2 = this._offset;
                    this._offset = i2 + 1;
                    i = bArr[i2];
                } else {
                    i = read();
                }
                return (byte) i;
            case 94:
                return (short) ((256 * read()) + read());
            case 95:
                return (long) (0.001d * parseInt());
            case 128:
            case Opcodes.LOR /* 129 */:
            case 130:
            case Opcodes.LXOR /* 131 */:
            case Opcodes.IINC /* 132 */:
            case Opcodes.I2L /* 133 */:
            case Opcodes.I2F /* 134 */:
            case Opcodes.I2D /* 135 */:
            case 136:
            case Opcodes.L2F /* 137 */:
            case Opcodes.L2D /* 138 */:
            case Opcodes.F2I /* 139 */:
            case Opcodes.F2L /* 140 */:
            case Opcodes.F2D /* 141 */:
            case Opcodes.D2I /* 142 */:
            case Opcodes.D2L /* 143 */:
            case 144:
            case Opcodes.I2B /* 145 */:
            case Opcodes.I2C /* 146 */:
            case Opcodes.I2S /* 147 */:
            case Opcodes.LCMP /* 148 */:
            case Opcodes.FCMPL /* 149 */:
            case 150:
            case Opcodes.DCMPL /* 151 */:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case Opcodes.IF_ICMPEQ /* 159 */:
            case 160:
            case Opcodes.IF_ICMPLT /* 161 */:
            case Opcodes.IF_ICMPGE /* 162 */:
            case Opcodes.IF_ICMPGT /* 163 */:
            case Opcodes.IF_ICMPLE /* 164 */:
            case Opcodes.IF_ACMPEQ /* 165 */:
            case Opcodes.IF_ACMPNE /* 166 */:
            case Opcodes.GOTO /* 167 */:
            case 168:
            case Opcodes.RET /* 169 */:
            case Opcodes.TABLESWITCH /* 170 */:
            case Opcodes.LOOKUPSWITCH /* 171 */:
            case Opcodes.IRETURN /* 172 */:
            case Opcodes.LRETURN /* 173 */:
            case Opcodes.FRETURN /* 174 */:
            case Opcodes.DRETURN /* 175 */:
            case 176:
            case Opcodes.RETURN /* 177 */:
            case Opcodes.GETSTATIC /* 178 */:
            case Opcodes.PUTSTATIC /* 179 */:
            case Opcodes.GETFIELD /* 180 */:
            case Opcodes.PUTFIELD /* 181 */:
            case Opcodes.INVOKEVIRTUAL /* 182 */:
            case Opcodes.INVOKESPECIAL /* 183 */:
            case 184:
            case Opcodes.INVOKEINTERFACE /* 185 */:
            case Opcodes.INVOKEDYNAMIC /* 186 */:
            case Opcodes.NEW /* 187 */:
            case Opcodes.NEWARRAY /* 188 */:
            case Opcodes.ANEWARRAY /* 189 */:
            case Opcodes.ARRAYLENGTH /* 190 */:
            case Opcodes.ATHROW /* 191 */:
                return read - 144;
            case Opcodes.CHECKCAST /* 192 */:
            case Opcodes.INSTANCEOF /* 193 */:
            case Opcodes.MONITORENTER /* 194 */:
            case Opcodes.MONITOREXIT /* 195 */:
            case 196:
            case Opcodes.MULTIANEWARRAY /* 197 */:
            case Opcodes.IFNULL /* 198 */:
            case Opcodes.IFNONNULL /* 199 */:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                return ((read - 200) << 8) + read();
            case 208:
            case 209:
            case 210:
            case 211:
            case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
            case 213:
            case 214:
            case 215:
                return ((read - Hessian2Constants.BC_INT_SHORT_ZERO) << 16) + (256 * read()) + read();
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
                return read - 224;
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case Hessian2Constants.BC_LONG_BYTE_ZERO /* 248 */:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case Const.MAX_ARRAY_DIMENSIONS /* 255 */:
                return ((read - Hessian2Constants.BC_LONG_BYTE_ZERO) << 8) + read();
        }
    }

    public float readFloat() throws IOException {
        return (float) readDouble();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public double readDouble() throws IOException {
        int i;
        int read = read();
        switch (read) {
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return ((read - 60) << 16) + (256 * read()) + read();
            case 64:
            case 65:
            case 66:
            case 67:
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
            case 85:
            case 86:
            case 87:
            case 88:
            case 90:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
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
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            default:
                throw expect("double", read);
            case 68:
                return parseDouble();
            case 70:
                return 0.0d;
            case 73:
            case 89:
                return parseInt();
            case 76:
                return parseLong();
            case 78:
                return 0.0d;
            case 84:
                return 1.0d;
            case 91:
                return 0.0d;
            case 92:
                return 1.0d;
            case 93:
                if (this._offset < this._length) {
                    byte[] bArr = this._buffer;
                    int i2 = this._offset;
                    this._offset = i2 + 1;
                    i = bArr[i2];
                } else {
                    i = read();
                }
                return (byte) i;
            case 94:
                return (short) ((256 * read()) + read());
            case 95:
                return 0.001d * parseInt();
            case 128:
            case Opcodes.LOR /* 129 */:
            case 130:
            case Opcodes.LXOR /* 131 */:
            case Opcodes.IINC /* 132 */:
            case Opcodes.I2L /* 133 */:
            case Opcodes.I2F /* 134 */:
            case Opcodes.I2D /* 135 */:
            case 136:
            case Opcodes.L2F /* 137 */:
            case Opcodes.L2D /* 138 */:
            case Opcodes.F2I /* 139 */:
            case Opcodes.F2L /* 140 */:
            case Opcodes.F2D /* 141 */:
            case Opcodes.D2I /* 142 */:
            case Opcodes.D2L /* 143 */:
            case 144:
            case Opcodes.I2B /* 145 */:
            case Opcodes.I2C /* 146 */:
            case Opcodes.I2S /* 147 */:
            case Opcodes.LCMP /* 148 */:
            case Opcodes.FCMPL /* 149 */:
            case 150:
            case Opcodes.DCMPL /* 151 */:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case Opcodes.IF_ICMPEQ /* 159 */:
            case 160:
            case Opcodes.IF_ICMPLT /* 161 */:
            case Opcodes.IF_ICMPGE /* 162 */:
            case Opcodes.IF_ICMPGT /* 163 */:
            case Opcodes.IF_ICMPLE /* 164 */:
            case Opcodes.IF_ACMPEQ /* 165 */:
            case Opcodes.IF_ACMPNE /* 166 */:
            case Opcodes.GOTO /* 167 */:
            case 168:
            case Opcodes.RET /* 169 */:
            case Opcodes.TABLESWITCH /* 170 */:
            case Opcodes.LOOKUPSWITCH /* 171 */:
            case Opcodes.IRETURN /* 172 */:
            case Opcodes.LRETURN /* 173 */:
            case Opcodes.FRETURN /* 174 */:
            case Opcodes.DRETURN /* 175 */:
            case 176:
            case Opcodes.RETURN /* 177 */:
            case Opcodes.GETSTATIC /* 178 */:
            case Opcodes.PUTSTATIC /* 179 */:
            case Opcodes.GETFIELD /* 180 */:
            case Opcodes.PUTFIELD /* 181 */:
            case Opcodes.INVOKEVIRTUAL /* 182 */:
            case Opcodes.INVOKESPECIAL /* 183 */:
            case 184:
            case Opcodes.INVOKEINTERFACE /* 185 */:
            case Opcodes.INVOKEDYNAMIC /* 186 */:
            case Opcodes.NEW /* 187 */:
            case Opcodes.NEWARRAY /* 188 */:
            case Opcodes.ANEWARRAY /* 189 */:
            case Opcodes.ARRAYLENGTH /* 190 */:
            case Opcodes.ATHROW /* 191 */:
                return read - 144;
            case Opcodes.CHECKCAST /* 192 */:
            case Opcodes.INSTANCEOF /* 193 */:
            case Opcodes.MONITORENTER /* 194 */:
            case Opcodes.MONITOREXIT /* 195 */:
            case 196:
            case Opcodes.MULTIANEWARRAY /* 197 */:
            case Opcodes.IFNULL /* 198 */:
            case Opcodes.IFNONNULL /* 199 */:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                return ((read - 200) << 8) + read();
            case 208:
            case 209:
            case 210:
            case 211:
            case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
            case 213:
            case 214:
            case 215:
                return ((read - Hessian2Constants.BC_INT_SHORT_ZERO) << 16) + (256 * read()) + read();
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
                return read - 224;
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case Hessian2Constants.BC_LONG_BYTE_ZERO /* 248 */:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case Const.MAX_ARRAY_DIMENSIONS /* 255 */:
                return ((read - Hessian2Constants.BC_LONG_BYTE_ZERO) << 8) + read();
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public long readUTCDate() throws IOException {
        int tag = read();
        if (tag == 74) {
            return parseLong();
        }
        if (tag == 75) {
            return parseInt() * 60000;
        }
        throw expect("date", tag);
    }

    public int readChar() throws IOException {
        if (this._chunkLength > 0) {
            this._chunkLength--;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = -2;
            }
            int ch2 = parseUTF8Char();
            return ch2;
        }
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = read();
        switch (tag) {
            case 78:
                return -1;
            case 82:
            case 83:
                this._isLastChunk = tag == 83;
                this._chunkLength = (read() << 8) + read();
                this._chunkLength--;
                int value = parseUTF8Char();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = -2;
                }
                return value;
            default:
                throw expect("char", tag);
        }
    }

    public int readString(char[] buffer, int offset, int length) throws IOException {
        int readLength = 0;
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            int tag = read();
            switch (tag) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                    this._isLastChunk = true;
                    this._chunkLength = tag - 0;
                    break;
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 79:
                case 80:
                case 81:
                default:
                    throw expect("string", tag);
                case 78:
                    return -1;
                case 82:
                case 83:
                    this._isLastChunk = tag == 83;
                    this._chunkLength = (read() << 8) + read();
                    break;
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
                    this._chunkLength = -2;
                    return readLength;
                }
                int tag2 = read();
                switch (tag2) {
                    case 82:
                    case 83:
                        this._isLastChunk = tag2 == 83;
                        this._chunkLength = (read() << 8) + read();
                        break;
                    default:
                        throw expect("string", tag2);
                }
            }
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = -2;
        return readLength;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public String readString() throws IOException {
        int i;
        int read = read();
        switch (read) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                this._isLastChunk = true;
                this._chunkLength = read - 0;
                this._sbuf.setLength(0);
                while (true) {
                    int parseChar = parseChar();
                    if (parseChar >= 0) {
                        this._sbuf.append((char) parseChar);
                    } else {
                        return this._sbuf.toString();
                    }
                }
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 52:
            case 53:
            case 54:
            case 55:
            case 64:
            case 65:
            case 66:
            case 67:
            case 69:
            case 71:
            case 72:
            case 74:
            case 75:
            case 77:
            case 79:
            case 80:
            case 81:
            case 85:
            case 86:
            case 87:
            case 88:
            case 90:
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
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
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            default:
                throw expect("string", read);
            case 48:
            case 49:
            case 50:
            case 51:
                this._isLastChunk = true;
                this._chunkLength = ((read - 48) * 256) + read();
                this._sbuf.setLength(0);
                while (true) {
                    int parseChar2 = parseChar();
                    if (parseChar2 >= 0) {
                        this._sbuf.append((char) parseChar2);
                    } else {
                        return this._sbuf.toString();
                    }
                }
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return String.valueOf(((read - 60) << 16) + (256 * read()) + read());
            case 68:
                return String.valueOf(parseDouble());
            case 70:
                return "false";
            case 73:
            case 89:
                return String.valueOf(parseInt());
            case 76:
                return String.valueOf(parseLong());
            case 78:
                return null;
            case 82:
            case 83:
                this._isLastChunk = read == 83;
                this._chunkLength = (read() << 8) + read();
                this._sbuf.setLength(0);
                while (true) {
                    int parseChar3 = parseChar();
                    if (parseChar3 >= 0) {
                        this._sbuf.append((char) parseChar3);
                    } else {
                        return this._sbuf.toString();
                    }
                }
            case 84:
                return "true";
            case 91:
                return "0.0";
            case 92:
                return "1.0";
            case 93:
                if (this._offset < this._length) {
                    byte[] bArr = this._buffer;
                    int i2 = this._offset;
                    this._offset = i2 + 1;
                    i = bArr[i2];
                } else {
                    i = read();
                }
                return String.valueOf((int) ((byte) i));
            case 94:
                return String.valueOf((int) ((short) ((256 * read()) + read())));
            case 95:
                return String.valueOf(0.001d * parseInt());
            case 128:
            case Opcodes.LOR /* 129 */:
            case 130:
            case Opcodes.LXOR /* 131 */:
            case Opcodes.IINC /* 132 */:
            case Opcodes.I2L /* 133 */:
            case Opcodes.I2F /* 134 */:
            case Opcodes.I2D /* 135 */:
            case 136:
            case Opcodes.L2F /* 137 */:
            case Opcodes.L2D /* 138 */:
            case Opcodes.F2I /* 139 */:
            case Opcodes.F2L /* 140 */:
            case Opcodes.F2D /* 141 */:
            case Opcodes.D2I /* 142 */:
            case Opcodes.D2L /* 143 */:
            case 144:
            case Opcodes.I2B /* 145 */:
            case Opcodes.I2C /* 146 */:
            case Opcodes.I2S /* 147 */:
            case Opcodes.LCMP /* 148 */:
            case Opcodes.FCMPL /* 149 */:
            case 150:
            case Opcodes.DCMPL /* 151 */:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case Opcodes.IF_ICMPEQ /* 159 */:
            case 160:
            case Opcodes.IF_ICMPLT /* 161 */:
            case Opcodes.IF_ICMPGE /* 162 */:
            case Opcodes.IF_ICMPGT /* 163 */:
            case Opcodes.IF_ICMPLE /* 164 */:
            case Opcodes.IF_ACMPEQ /* 165 */:
            case Opcodes.IF_ACMPNE /* 166 */:
            case Opcodes.GOTO /* 167 */:
            case 168:
            case Opcodes.RET /* 169 */:
            case Opcodes.TABLESWITCH /* 170 */:
            case Opcodes.LOOKUPSWITCH /* 171 */:
            case Opcodes.IRETURN /* 172 */:
            case Opcodes.LRETURN /* 173 */:
            case Opcodes.FRETURN /* 174 */:
            case Opcodes.DRETURN /* 175 */:
            case 176:
            case Opcodes.RETURN /* 177 */:
            case Opcodes.GETSTATIC /* 178 */:
            case Opcodes.PUTSTATIC /* 179 */:
            case Opcodes.GETFIELD /* 180 */:
            case Opcodes.PUTFIELD /* 181 */:
            case Opcodes.INVOKEVIRTUAL /* 182 */:
            case Opcodes.INVOKESPECIAL /* 183 */:
            case 184:
            case Opcodes.INVOKEINTERFACE /* 185 */:
            case Opcodes.INVOKEDYNAMIC /* 186 */:
            case Opcodes.NEW /* 187 */:
            case Opcodes.NEWARRAY /* 188 */:
            case Opcodes.ANEWARRAY /* 189 */:
            case Opcodes.ARRAYLENGTH /* 190 */:
            case Opcodes.ATHROW /* 191 */:
                return String.valueOf(read - 144);
            case Opcodes.CHECKCAST /* 192 */:
            case Opcodes.INSTANCEOF /* 193 */:
            case Opcodes.MONITORENTER /* 194 */:
            case Opcodes.MONITOREXIT /* 195 */:
            case 196:
            case Opcodes.MULTIANEWARRAY /* 197 */:
            case Opcodes.IFNULL /* 198 */:
            case Opcodes.IFNONNULL /* 199 */:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                return String.valueOf(((read - 200) << 8) + read());
            case 208:
            case 209:
            case 210:
            case 211:
            case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
            case 213:
            case 214:
            case 215:
                return String.valueOf(((read - Hessian2Constants.BC_INT_SHORT_ZERO) << 16) + (256 * read()) + read());
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
                return String.valueOf(read - 224);
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case Hessian2Constants.BC_LONG_BYTE_ZERO /* 248 */:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case Const.MAX_ARRAY_DIMENSIONS /* 255 */:
                return String.valueOf(((read - Hessian2Constants.BC_LONG_BYTE_ZERO) << 8) + read());
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public byte[] readBytes() throws IOException {
        int tag = read();
        switch (tag) {
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
                this._isLastChunk = true;
                this._chunkLength = tag - 32;
                byte[] buffer = new byte[this._chunkLength];
                int k = 0;
                while (true) {
                    int data = parseByte();
                    if (data >= 0) {
                        int i = k;
                        k++;
                        buffer[i] = (byte) data;
                    } else {
                        return buffer;
                    }
                }
            case 48:
            case 49:
            case 50:
            case 51:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            default:
                throw expect("bytes", tag);
            case 52:
            case 53:
            case 54:
            case 55:
                this._isLastChunk = true;
                this._chunkLength = ((tag - 52) * 256) + read();
                byte[] buffer2 = new byte[this._chunkLength];
                int k2 = 0;
                while (true) {
                    int data2 = parseByte();
                    if (data2 >= 0) {
                        int i2 = k2;
                        k2++;
                        buffer2[i2] = (byte) data2;
                    } else {
                        return buffer2;
                    }
                }
            case 65:
            case 66:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while (true) {
                    int data3 = parseByte();
                    if (data3 >= 0) {
                        bos.write(data3);
                    } else {
                        return bos.toByteArray();
                    }
                }
            case 78:
                return null;
        }
    }

    public int readByte() throws IOException {
        if (this._chunkLength > 0) {
            this._chunkLength--;
            if (this._chunkLength == 0 && this._isLastChunk) {
                this._chunkLength = -2;
            }
            return read();
        }
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        int tag = read();
        switch (tag) {
            case 65:
            case 66:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                int value = parseByte();
                if (this._chunkLength == 0 && this._isLastChunk) {
                    this._chunkLength = -2;
                }
                return value;
            case 78:
                return -1;
            default:
                throw expect("binary", tag);
        }
    }

    public int readBytes(byte[] buffer, int offset, int length) throws IOException {
        int readLength = 0;
        if (this._chunkLength == -2) {
            this._chunkLength = 0;
            return -1;
        }
        if (this._chunkLength == 0) {
            int tag = read();
            switch (tag) {
                case 65:
                case 66:
                    this._isLastChunk = tag == 66;
                    this._chunkLength = (read() << 8) + read();
                    break;
                case 78:
                    return -1;
                default:
                    throw expect("binary", tag);
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
                    this._chunkLength = -2;
                    return readLength;
                }
                int tag2 = read();
                switch (tag2) {
                    case 65:
                    case 66:
                        this._isLastChunk = tag2 == 66;
                        this._chunkLength = (read() << 8) + read();
                        break;
                    default:
                        throw expect("binary", tag2);
                }
            }
        }
        if (readLength == 0) {
            return -1;
        }
        if (this._chunkLength > 0 || !this._isLastChunk) {
            return readLength;
        }
        this._chunkLength = -2;
        return readLength;
    }

    private HashMap readFault() throws IOException {
        int code;
        HashMap map = new HashMap();
        int read = read();
        while (true) {
            code = read;
            if (code <= 0 || code == 90) {
                break;
            }
            this._offset--;
            Object key = readObject();
            Object value = readObject();
            if (key != null && value != null) {
                map.put(key, value);
            }
            read = read();
        }
        if (code != 90) {
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
        int read;
        if (expectedClass == null || expectedClass == Object.class) {
            return readObject();
        }
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int tag = read;
        switch (tag) {
            case 67:
                readObjectDefinition(expectedClass);
                return readObject(expectedClass);
            case 68:
            case 69:
            case 70:
            case 71:
            case 73:
            case 74:
            case 75:
            case 76:
            case 80:
            case 82:
            case 83:
            case 84:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            default:
                if (tag >= 0) {
                    this._offset--;
                }
                Object value = findSerializerFactory().getDeserializer(expectedClass).readObject(this);
                return value;
            case 72:
                Deserializer reader = findSerializerFactory().getDeserializer(expectedClass);
                boolean keyValuePair = expectedTypes != null && expectedTypes.length == 2;
                return reader.readMap(this, keyValuePair ? expectedTypes[0] : null, keyValuePair ? expectedTypes[1] : null);
            case 77:
                String type = readType();
                if ("".equals(type)) {
                    Deserializer reader2 = findSerializerFactory().getDeserializer(expectedClass);
                    return reader2.readMap(this);
                }
                Deserializer reader3 = findSerializerFactory().getObjectDeserializer(type, expectedClass);
                return reader3.readMap(this);
            case 78:
                return null;
            case 79:
                int ref = readInt();
                int size = this._classDefs.size();
                if (ref < 0 || size <= ref) {
                    throw new HessianProtocolException("'" + ref + "' is an unknown class definition");
                }
                ObjectDefinition def = (ObjectDefinition) this._classDefs.get(ref);
                return readObjectInstance(expectedClass, def);
            case 81:
                return this._refs.get(readInt());
            case 85:
                String type2 = readType();
                Deserializer reader4 = findSerializerFactory().getListDeserializer(type2, expectedClass);
                Object v = reader4.readList(this, -1);
                return v;
            case 86:
                String type3 = readType();
                int length = readInt();
                Deserializer reader5 = findSerializerFactory().getListDeserializer(type3, expectedClass);
                boolean valueType = expectedTypes != null && expectedTypes.length == 1;
                Object v2 = reader5.readLengthList(this, length, valueType ? expectedTypes[0] : null);
                return v2;
            case 87:
                Deserializer reader6 = findSerializerFactory().getListDeserializer(null, expectedClass);
                boolean valueType2 = expectedTypes != null && expectedTypes.length == 1;
                Object v3 = reader6.readList(this, -1, valueType2 ? expectedTypes[0] : null);
                return v3;
            case 88:
                int length2 = readInt();
                Deserializer reader7 = findSerializerFactory().getListDeserializer(null, expectedClass);
                boolean valueType3 = expectedTypes != null && expectedTypes.length == 1;
                Object v4 = reader7.readLengthList(this, length2, valueType3 ? expectedTypes[0] : null);
                return v4;
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
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
                int ref2 = tag - 96;
                int size2 = this._classDefs.size();
                if (ref2 < 0 || size2 <= ref2) {
                    throw new HessianProtocolException("'" + ref2 + "' is an unknown class definition");
                }
                ObjectDefinition def2 = (ObjectDefinition) this._classDefs.get(ref2);
                return readObjectInstance(expectedClass, def2);
            case 112:
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
                int length3 = tag - 112;
                readType();
                Deserializer reader8 = findSerializerFactory().getListDeserializer(null, expectedClass);
                boolean valueType4 = expectedTypes != null && expectedTypes.length == 1;
                Object v5 = reader8.readLengthList(this, length3, valueType4 ? expectedTypes[0] : null);
                return v5;
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
                int length4 = tag - 120;
                Deserializer reader9 = findSerializerFactory().getListDeserializer(null, expectedClass);
                boolean valueType5 = expectedTypes != null && expectedTypes.length == 1;
                Object v6 = reader9.readLengthList(this, length4, valueType5 ? expectedTypes[0] : null);
                return v6;
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readObject() throws IOException {
        return readObject((List<Class<?>>) null);
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Object readObject(List<Class<?>> expectedTypes) throws IOException {
        int read;
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int tag = read;
        switch (tag) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                this._isLastChunk = true;
                this._chunkLength = tag - 0;
                this._sbuf.setLength(0);
                parseString(this._sbuf);
                return this._sbuf.toString();
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
                this._isLastChunk = true;
                int len = tag - 32;
                this._chunkLength = 0;
                byte[] data = new byte[len];
                for (int i2 = 0; i2 < len; i2++) {
                    data[i2] = (byte) read();
                }
                return data;
            case 48:
            case 49:
            case 50:
            case 51:
                this._isLastChunk = true;
                this._chunkLength = ((tag - 48) * 256) + read();
                this._sbuf.setLength(0);
                parseString(this._sbuf);
                return this._sbuf.toString();
            case 52:
            case 53:
            case 54:
            case 55:
                this._isLastChunk = true;
                int len2 = ((tag - 52) * 256) + read();
                this._chunkLength = 0;
                byte[] buffer = new byte[len2];
                for (int i3 = 0; i3 < len2; i3++) {
                    buffer[i3] = (byte) read();
                }
                return buffer;
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
                return Long.valueOf(((tag - 60) << 16) + (256 * read()) + read());
            case 64:
            case 69:
            case 71:
            case 80:
            case 90:
            default:
                if (tag < 0) {
                    throw new EOFException("readObject: unexpected end of file");
                }
                throw error("readObject: unknown code " + codeName(tag));
            case 65:
            case 66:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while (true) {
                    int data2 = parseByte();
                    if (data2 >= 0) {
                        bos.write(data2);
                    } else {
                        return bos.toByteArray();
                    }
                }
            case 67:
                readObjectDefinition(null);
                return readObject();
            case 68:
                return Double.valueOf(parseDouble());
            case 70:
                return false;
            case 72:
                boolean keyValuePair = expectedTypes != null && expectedTypes.size() == 2;
                Deserializer reader = findSerializerFactory().getDeserializer(Map.class);
                return reader.readMap(this, keyValuePair ? expectedTypes.get(0) : null, keyValuePair ? expectedTypes.get(1) : null);
            case 73:
                return Integer.valueOf(parseInt());
            case 74:
                return new Date(parseLong());
            case 75:
                return new Date(parseInt() * 60000);
            case 76:
                return Long.valueOf(parseLong());
            case 77:
                String type = readType();
                return findSerializerFactory().readMap(this, type);
            case 78:
                return null;
            case 79:
                int ref = readInt();
                ObjectDefinition def = (ObjectDefinition) this._classDefs.get(ref);
                return readObjectInstance(null, def);
            case 81:
                int ref2 = readInt();
                return this._refs.get(ref2);
            case 82:
            case 83:
                this._isLastChunk = tag == 83;
                this._chunkLength = (read() << 8) + read();
                this._sbuf.setLength(0);
                parseString(this._sbuf);
                return this._sbuf.toString();
            case 84:
                return true;
            case 85:
                String type2 = readType();
                return findSerializerFactory().readList(this, -1, type2);
            case 86:
                String type3 = readType();
                int length = readInt();
                Deserializer reader2 = findSerializerFactory().getListDeserializer(type3, null);
                boolean valueType = expectedTypes != null && expectedTypes.size() == 1;
                return reader2.readLengthList(this, length, valueType ? expectedTypes.get(0) : null);
            case 87:
                return findSerializerFactory().readList(this, -1, null);
            case 88:
                int length2 = readInt();
                Deserializer reader3 = findSerializerFactory().getListDeserializer(null, null);
                boolean valueType2 = expectedTypes != null && expectedTypes.size() == 1;
                return reader3.readLengthList(this, length2, valueType2 ? expectedTypes.get(0) : null);
            case 89:
                return Long.valueOf(parseInt());
            case 91:
                return Double.valueOf(0.0d);
            case 92:
                return Double.valueOf(1.0d);
            case 93:
                return Double.valueOf((byte) read());
            case 94:
                return Double.valueOf((short) ((256 * read()) + read()));
            case 95:
                int mills = parseInt();
                return Double.valueOf(0.001d * mills);
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
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
                int ref3 = tag - 96;
                if (this._classDefs == null) {
                    throw error("No classes defined at reference '{0}'" + tag);
                }
                ObjectDefinition def2 = (ObjectDefinition) this._classDefs.get(ref3);
                return readObjectInstance(null, def2);
            case 112:
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            case 115:
            case 116:
            case Opcodes.LNEG /* 117 */:
            case Opcodes.FNEG /* 118 */:
            case Opcodes.DNEG /* 119 */:
                String type4 = readType();
                int length3 = tag - 112;
                Deserializer reader4 = findSerializerFactory().getListDeserializer(type4, null);
                boolean valueType3 = expectedTypes != null && expectedTypes.size() == 1;
                return reader4.readLengthList(this, length3, valueType3 ? expectedTypes.get(0) : null);
            case 120:
            case Opcodes.LSHL /* 121 */:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
                int length4 = tag - 120;
                Deserializer reader5 = findSerializerFactory().getListDeserializer(null, null);
                boolean valueType4 = expectedTypes != null && expectedTypes.size() == 1;
                return reader5.readLengthList(this, length4, valueType4 ? expectedTypes.get(0) : null);
            case 128:
            case Opcodes.LOR /* 129 */:
            case 130:
            case Opcodes.LXOR /* 131 */:
            case Opcodes.IINC /* 132 */:
            case Opcodes.I2L /* 133 */:
            case Opcodes.I2F /* 134 */:
            case Opcodes.I2D /* 135 */:
            case 136:
            case Opcodes.L2F /* 137 */:
            case Opcodes.L2D /* 138 */:
            case Opcodes.F2I /* 139 */:
            case Opcodes.F2L /* 140 */:
            case Opcodes.F2D /* 141 */:
            case Opcodes.D2I /* 142 */:
            case Opcodes.D2L /* 143 */:
            case 144:
            case Opcodes.I2B /* 145 */:
            case Opcodes.I2C /* 146 */:
            case Opcodes.I2S /* 147 */:
            case Opcodes.LCMP /* 148 */:
            case Opcodes.FCMPL /* 149 */:
            case 150:
            case Opcodes.DCMPL /* 151 */:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case Opcodes.IF_ICMPEQ /* 159 */:
            case 160:
            case Opcodes.IF_ICMPLT /* 161 */:
            case Opcodes.IF_ICMPGE /* 162 */:
            case Opcodes.IF_ICMPGT /* 163 */:
            case Opcodes.IF_ICMPLE /* 164 */:
            case Opcodes.IF_ACMPEQ /* 165 */:
            case Opcodes.IF_ACMPNE /* 166 */:
            case Opcodes.GOTO /* 167 */:
            case 168:
            case Opcodes.RET /* 169 */:
            case Opcodes.TABLESWITCH /* 170 */:
            case Opcodes.LOOKUPSWITCH /* 171 */:
            case Opcodes.IRETURN /* 172 */:
            case Opcodes.LRETURN /* 173 */:
            case Opcodes.FRETURN /* 174 */:
            case Opcodes.DRETURN /* 175 */:
            case 176:
            case Opcodes.RETURN /* 177 */:
            case Opcodes.GETSTATIC /* 178 */:
            case Opcodes.PUTSTATIC /* 179 */:
            case Opcodes.GETFIELD /* 180 */:
            case Opcodes.PUTFIELD /* 181 */:
            case Opcodes.INVOKEVIRTUAL /* 182 */:
            case Opcodes.INVOKESPECIAL /* 183 */:
            case 184:
            case Opcodes.INVOKEINTERFACE /* 185 */:
            case Opcodes.INVOKEDYNAMIC /* 186 */:
            case Opcodes.NEW /* 187 */:
            case Opcodes.NEWARRAY /* 188 */:
            case Opcodes.ANEWARRAY /* 189 */:
            case Opcodes.ARRAYLENGTH /* 190 */:
            case Opcodes.ATHROW /* 191 */:
                return Integer.valueOf(tag - 144);
            case Opcodes.CHECKCAST /* 192 */:
            case Opcodes.INSTANCEOF /* 193 */:
            case Opcodes.MONITORENTER /* 194 */:
            case Opcodes.MONITOREXIT /* 195 */:
            case 196:
            case Opcodes.MULTIANEWARRAY /* 197 */:
            case Opcodes.IFNULL /* 198 */:
            case Opcodes.IFNONNULL /* 199 */:
            case 200:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case WebdavStatus.SC_MULTI_STATUS /* 207 */:
                return Integer.valueOf(((tag - 200) << 8) + read());
            case 208:
            case 209:
            case 210:
            case 211:
            case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
            case 213:
            case 214:
            case 215:
                return Integer.valueOf(((tag - Hessian2Constants.BC_INT_SHORT_ZERO) << 16) + (256 * read()) + read());
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 221:
            case 222:
            case 223:
            case 224:
            case 225:
            case 226:
            case 227:
            case 228:
            case 229:
            case 230:
            case 231:
            case 232:
            case 233:
            case 234:
            case 235:
            case 236:
            case 237:
            case 238:
            case 239:
                return Long.valueOf(tag - 224);
            case 240:
            case 241:
            case 242:
            case 243:
            case 244:
            case 245:
            case 246:
            case 247:
            case Hessian2Constants.BC_LONG_BYTE_ZERO /* 248 */:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case Const.MAX_ARRAY_DIMENSIONS /* 255 */:
                return Long.valueOf(((tag - Hessian2Constants.BC_LONG_BYTE_ZERO) << 8) + read());
        }
    }

    private void parseString(StringBuilder sbuf) throws IOException {
        while (true) {
            if (this._chunkLength <= 0 && !parseChunkLength()) {
                return;
            }
            int length = this._chunkLength;
            this._chunkLength = 0;
            while (true) {
                int i = length;
                length--;
                if (i > 0) {
                    sbuf.append((char) parseUTF8Char());
                }
            }
        }
    }

    private void readObjectDefinition(Class cl) throws IOException {
        String type = readString();
        int len = readInt();
        String[] fieldNames = new String[len];
        for (int i = 0; i < len; i++) {
            fieldNames[i] = readString();
        }
        ObjectDefinition def = new ObjectDefinition(type, fieldNames);
        if (this._classDefs == null) {
            this._classDefs = new ArrayList();
        }
        this._classDefs.add(def);
    }

    private Object readObjectInstance(Class cl, ObjectDefinition def) throws IOException {
        String type = def.getType();
        String[] fieldNames = def.getFieldNames();
        if (cl != null) {
            Deserializer reader = findSerializerFactory().getObjectDeserializer(type, cl);
            return reader.readObject(this, fieldNames);
        }
        return findSerializerFactory().readObject(this, type, fieldNames);
    }

    private String readLenString() throws IOException {
        int len = readInt();
        this._isLastChunk = true;
        this._chunkLength = len;
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

    private String readLenString(int len) throws IOException {
        this._isLastChunk = true;
        this._chunkLength = len;
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
        int code;
        if (this._offset < this._length) {
            code = this._buffer[this._offset] & 255;
        } else {
            code = read();
            if (code >= 0) {
                this._offset--;
            }
        }
        return code < 0 || code == 90;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void readEnd() throws IOException {
        int read;
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int code = read;
        if (code == 90) {
            return;
        }
        if (code < 0) {
            throw error("unexpected end of file");
        }
        throw error("unknown code:" + codeName(code));
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void readMapEnd() throws IOException {
        int read;
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int code = read;
        if (code != 90) {
            throw error("expected end of map ('Z') at '" + codeName(code) + "'");
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void readListEnd() throws IOException {
        int read;
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int code = read;
        if (code != 90) {
            throw error("expected end of list ('Z') at '" + codeName(code) + "'");
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

    public Object readStreamingObject() throws IOException {
        if (this._refs != null) {
            this._refs.clear();
        }
        return readObject();
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
        int read;
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int code = read;
        this._offset--;
        switch (code) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 48:
            case 49:
            case 50:
            case 51:
            case 82:
            case 83:
                String type = readString();
                if (this._types == null) {
                    this._types = new ArrayList();
                }
                this._types.add(type);
                return type;
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            default:
                int ref = readInt();
                if (this._types.size() <= ref) {
                    throw new IndexOutOfBoundsException("type ref #" + ref + " is greater than the number of valid types (" + this._types.size() + ")");
                }
                return (String) this._types.get(ref);
        }
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public int readLength() throws IOException {
        throw new UnsupportedOperationException();
    }

    private int parseInt() throws IOException {
        int offset = this._offset;
        if (offset + 3 < this._length) {
            byte[] buffer = this._buffer;
            int b32 = buffer[offset + 0] & 255;
            int b24 = buffer[offset + 1] & 255;
            int b16 = buffer[offset + 2] & 255;
            int b8 = buffer[offset + 3] & 255;
            this._offset = offset + 4;
            return (b32 << 24) + (b24 << 16) + (b16 << 8) + b8;
        }
        int b322 = read();
        int b242 = read();
        int b162 = read();
        int b82 = read();
        return (b322 << 24) + (b242 << 16) + (b162 << 8) + b82;
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
        long bits = parseLong();
        return Double.longBitsToDouble(bits);
    }

    Node parseXML() throws IOException {
        throw new UnsupportedOperationException();
    }

    private boolean parseChunkLength() throws IOException {
        int read;
        if (this._isLastChunk) {
            return false;
        }
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int code = read;
        switch (code) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
                this._isLastChunk = true;
                this._chunkLength = code - 0;
                return true;
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            default:
                throw expect("string", code);
            case 48:
            case 49:
            case 50:
            case 51:
                this._isLastChunk = true;
                this._chunkLength = ((code - 48) * 256) + read();
                return true;
            case 82:
                this._isLastChunk = false;
                this._chunkLength = (read() << 8) + read();
                return true;
            case 83:
                this._isLastChunk = true;
                this._chunkLength = (read() << 8) + read();
                return true;
        }
    }

    private int parseChar() throws IOException {
        while (this._chunkLength <= 0) {
            if (!parseChunkLength()) {
                return -1;
            }
        }
        this._chunkLength--;
        return parseUTF8Char();
    }

    private int parseUTF8Char() throws IOException {
        int read;
        if (this._offset < this._length) {
            byte[] bArr = this._buffer;
            int i = this._offset;
            this._offset = i + 1;
            read = bArr[i] & 255;
        } else {
            read = read();
        }
        int ch2 = read;
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
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                    this._isLastChunk = true;
                    this._chunkLength = code - 32;
                    break;
                case 48:
                case 49:
                case 50:
                case 51:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                default:
                    throw expect("byte[]", code);
                case 52:
                case 53:
                case 54:
                case 55:
                    this._isLastChunk = true;
                    this._chunkLength = ((code - 52) * 256) + read();
                    break;
                case 65:
                    this._isLastChunk = false;
                    this._chunkLength = (read() << 8) + read();
                    break;
                case 66:
                    this._isLastChunk = true;
                    this._chunkLength = (read() << 8) + read();
                    break;
            }
        }
        this._chunkLength--;
        return read();
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public InputStream readInputStream() throws IOException {
        int tag = read();
        switch (tag) {
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
                this._isLastChunk = true;
                this._chunkLength = tag - 32;
                break;
            case 48:
            case 49:
            case 50:
            case 51:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            case 97:
            default:
                throw expect("binary", tag);
            case 52:
            case 53:
            case 54:
            case 55:
                this._isLastChunk = true;
                this._chunkLength = ((tag - 52) * 256) + read();
                break;
            case 65:
            case 66:
            case 98:
                this._isLastChunk = tag == 66;
                this._chunkLength = (read() << 8) + read();
                break;
            case 78:
                return null;
        }
        return new ReadInputStream();
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
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 43:
                    case 44:
                    case 45:
                    case 46:
                    case 47:
                        this._isLastChunk = true;
                        this._chunkLength = code - 32;
                        break;
                    case 48:
                    case 49:
                    case 50:
                    case 51:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    case 64:
                    case 67:
                    case 68:
                    case 69:
                    case 70:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    case 78:
                    case 79:
                    case 80:
                    case 81:
                    case 82:
                    case 83:
                    case 84:
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    case 97:
                    default:
                        throw expect("byte[]", code);
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                        this._isLastChunk = true;
                        this._chunkLength = ((code - 52) * 256) + read();
                        break;
                    case 65:
                    case 98:
                        this._isLastChunk = false;
                        this._chunkLength = (read() << 8) + read();
                        break;
                    case 66:
                        this._isLastChunk = true;
                        this._chunkLength = (read() << 8) + read();
                        break;
                }
            }
            int sublen = this._chunkLength;
            if (length < sublen) {
                sublen = length;
            }
            if (this._length <= this._offset && !readBuffer()) {
                return -1;
            }
            if (this._length - this._offset < sublen) {
                sublen = this._length - this._offset;
            }
            System.arraycopy(this._buffer, this._offset, buffer, offset, sublen);
            this._offset += sublen;
            offset += sublen;
            readLength += sublen;
            length -= sublen;
            this._chunkLength -= sublen;
        }
        return readLength;
    }

    public final int read() throws IOException {
        if (this._length <= this._offset && !readBuffer()) {
            return -1;
        }
        byte[] bArr = this._buffer;
        int i = this._offset;
        this._offset = i + 1;
        return bArr[i] & 255;
    }

    private final boolean readBuffer() throws IOException {
        int offset;
        byte[] buffer = this._buffer;
        int offset2 = this._offset;
        int length = this._length;
        if (offset2 < length) {
            System.arraycopy(buffer, offset2, buffer, 0, length - offset2);
            offset = length - offset2;
        } else {
            offset = 0;
        }
        int len = this._is.read(buffer, offset, 256 - offset);
        if (len <= 0) {
            this._length = offset;
            this._offset = 0;
            return offset > 0;
        }
        this._length = offset + len;
        this._offset = 0;
        return true;
    }

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public Reader getReader() {
        return null;
    }

    protected IOException expect(String expect, int ch2) throws IOException {
        if (ch2 < 0) {
            return error("expected " + expect + " at end of file");
        }
        this._offset--;
        try {
            Object obj = readObject();
            if (obj != null) {
                return error("expected " + expect + " at 0x" + Integer.toHexString(ch2 & Const.MAX_ARRAY_DIMENSIONS) + CharSequenceUtil.SPACE + obj.getClass().getName());
            }
            return error("expected " + expect + " at 0x" + Integer.toHexString(ch2 & Const.MAX_ARRAY_DIMENSIONS) + " null");
        } catch (IOException e) {
            log.log(Level.FINE, e.toString(), (Throwable) e);
            return error("expected " + expect + " at 0x" + Integer.toHexString(ch2 & Const.MAX_ARRAY_DIMENSIONS));
        }
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

    @Override // com.alibaba.com.caucho.hessian.io.AbstractHessianInput
    public void close() throws IOException {
        InputStream is = this._is;
        this._is = null;
        if (_isCloseStreamOnClose && is != null) {
            is.close();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Hessian2Input$ObjectDefinition.class */
    public static final class ObjectDefinition {
        private final String _type;
        private final String[] _fields;

        ObjectDefinition(String type, String[] fields) {
            this._type = type;
            this._fields = fields;
        }

        String getType() {
            return this._type;
        }

        String[] getFieldNames() {
            return this._fields;
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/Hessian2Input$ReadInputStream.class */
    class ReadInputStream extends InputStream {
        boolean _isClosed = false;

        ReadInputStream() {
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (!this._isClosed) {
                int ch2 = Hessian2Input.this.parseByte();
                if (ch2 < 0) {
                    this._isClosed = true;
                }
                return ch2;
            }
            return -1;
        }

        @Override // java.io.InputStream
        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (this._isClosed) {
                return -1;
            }
            int len = Hessian2Input.this.read(buffer, offset, length);
            if (len < 0) {
                this._isClosed = true;
            }
            return len;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            do {
            } while (read() >= 0);
        }
    }
}
