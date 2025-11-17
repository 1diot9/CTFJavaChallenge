package com.alibaba.com.caucho.hessian.io;

import cn.hutool.core.text.CharSequenceUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import org.apache.catalina.servlets.WebdavStatus;
import org.apache.naming.EjbRef;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;

/* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState.class */
public class HessianDebugState implements Hessian2Constants {
    private PrintWriter _dbg;
    private int _refId;
    private int _column;
    private ArrayList<State> _stateStack = new ArrayList<>();
    private ArrayList<ObjectDef> _objectDefList = new ArrayList<>();
    private ArrayList<String> _typeDefList = new ArrayList<>();
    private boolean _isNewline = true;
    private boolean _isObject = false;
    private State _state = new InitialState();

    static /* synthetic */ int access$108(HessianDebugState x0) {
        int i = x0._column;
        x0._column = i + 1;
        return i;
    }

    static /* synthetic */ int access$408(HessianDebugState x0) {
        int i = x0._refId;
        x0._refId = i + 1;
        return i;
    }

    public HessianDebugState(PrintWriter dbg) {
        this._dbg = dbg;
    }

    static boolean isString(int ch2) {
        switch (ch2) {
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
                return false;
        }
    }

    static boolean isInteger(int ch2) {
        switch (ch2) {
            case 73:
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
            case 208:
            case 209:
            case 210:
            case 211:
            case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
            case 213:
            case 214:
            case 215:
                return true;
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
                return false;
        }
    }

    public void startTop2() {
        this._state = new Top2State();
    }

    public void next(int ch2) throws IOException {
        this._state = this._state.next(ch2);
    }

    void pushStack(State state) {
        this._stateStack.add(state);
    }

    State popStack() {
        return this._stateStack.remove(this._stateStack.size() - 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void println() {
        if (!this._isNewline) {
            this._dbg.println();
            this._dbg.flush();
        }
        this._isNewline = true;
        this._column = 0;
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$ObjectDef.class */
    static class ObjectDef {
        private String _type;
        private ArrayList<String> _fields;

        ObjectDef(String type, ArrayList<String> fields) {
            this._type = type;
            this._fields = fields;
        }

        String getType() {
            return this._type;
        }

        ArrayList<String> getFields() {
            return this._fields;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$State.class */
    public abstract class State {
        State _next;

        abstract State next(int i);

        State() {
        }

        State(State next) {
            this._next = next;
        }

        boolean isShift(Object value) {
            return false;
        }

        State shift(Object value) {
            return this;
        }

        int depth() {
            if (this._next != null) {
                return this._next.depth();
            }
            return 0;
        }

        void printIndent(int depth) {
            if (HessianDebugState.this._isNewline) {
                for (int i = HessianDebugState.this._column; i < depth() + depth; i++) {
                    HessianDebugState.this._dbg.print(CharSequenceUtil.SPACE);
                    HessianDebugState.access$108(HessianDebugState.this);
                }
            }
        }

        void print(String string) {
            print(0, string);
        }

        void print(int depth, String string) {
            printIndent(depth);
            HessianDebugState.this._dbg.print(string);
            HessianDebugState.this._isNewline = false;
            HessianDebugState.this._isObject = false;
            int p = string.lastIndexOf(10);
            if (p > 0) {
                HessianDebugState.this._column = (string.length() - p) - 1;
            } else {
                HessianDebugState.this._column += string.length();
            }
        }

        void println(String string) {
            println(0, string);
        }

        void println(int depth, String string) {
            printIndent(depth);
            HessianDebugState.this._dbg.println(string);
            HessianDebugState.this._dbg.flush();
            HessianDebugState.this._isNewline = true;
            HessianDebugState.this._isObject = false;
            HessianDebugState.this._column = 0;
        }

        void println() {
            if (!HessianDebugState.this._isNewline) {
                HessianDebugState.this._dbg.println();
                HessianDebugState.this._dbg.flush();
            }
            HessianDebugState.this._isNewline = true;
            HessianDebugState.this._isObject = false;
            HessianDebugState.this._column = 0;
        }

        void printObject(String string) {
            if (HessianDebugState.this._isObject) {
                println();
            }
            printIndent(0);
            HessianDebugState.this._dbg.print(string);
            HessianDebugState.this._dbg.flush();
            HessianDebugState.this._column += string.length();
            HessianDebugState.this._isNewline = false;
            HessianDebugState.this._isObject = true;
        }

        protected State nextObject(int ch2) {
            switch (ch2) {
                case -1:
                    println();
                    return this;
                case 0:
                    if (isShift("\"\"")) {
                        return shift("\"\"");
                    }
                    printObject("\"\"".toString());
                    return this;
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
                    return new StringState(this, 'S', ch2);
                case 32:
                    if (isShift("binary(0)")) {
                        return shift("binary(0)");
                    }
                    printObject("binary(0)".toString());
                    return this;
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
                    return new BinaryState(this, 'B', ch2 - 32);
                case 48:
                case 49:
                case 50:
                case 51:
                    return new StringState(this, 'S', ch2 - 48, true);
                case 52:
                case 53:
                case 54:
                case 55:
                    return new BinaryState(this, 'B', ch2 - 52, true);
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                    return new LongState(this, "long", ch2 - 60, 6);
                case 64:
                case 69:
                case 71:
                case 80:
                case 90:
                default:
                    return this;
                case 65:
                    return new BinaryState(this, 'B', false);
                case 66:
                    return new BinaryState(this, 'B', true);
                case 67:
                    return new ObjectDefState(this);
                case 68:
                    return new DoubleState(this);
                case 70:
                    if (isShift(Boolean.FALSE)) {
                        return shift(Boolean.FALSE);
                    }
                    printObject("false");
                    return this;
                case 72:
                    return new MapState(this, HessianDebugState.access$408(HessianDebugState.this), false);
                case 73:
                    return new IntegerState(this, "int");
                case 74:
                    return new DateState(this);
                case 75:
                    return new DateState(this, true);
                case 76:
                    return new LongState(this, "long");
                case 77:
                    return new MapState(this, HessianDebugState.access$408(HessianDebugState.this));
                case 78:
                    if (isShift(null)) {
                        return shift(null);
                    }
                    printObject("null");
                    return this;
                case 79:
                    return new ObjectState(this, HessianDebugState.access$408(HessianDebugState.this));
                case 81:
                    return new RefState(this);
                case 82:
                    return new StringState(this, 'S', false);
                case 83:
                    return new StringState(this, 'S', true);
                case 84:
                    if (isShift(Boolean.TRUE)) {
                        return shift(Boolean.TRUE);
                    }
                    printObject("true");
                    return this;
                case 85:
                    return new ListState(this, HessianDebugState.access$408(HessianDebugState.this), true);
                case 86:
                    return new CompactListState(this, HessianDebugState.access$408(HessianDebugState.this), true);
                case 87:
                    return new ListState(this, HessianDebugState.access$408(HessianDebugState.this), false);
                case 88:
                    return new CompactListState(this, HessianDebugState.access$408(HessianDebugState.this), false);
                case 89:
                    return new LongState(this, "long", 0L, 4);
                case 91:
                case 92:
                    Double value = new Double(ch2 - 91);
                    if (isShift(value)) {
                        return shift(value);
                    }
                    printObject(value.toString());
                    return this;
                case 93:
                    return new DoubleIntegerState(this, 3);
                case 94:
                    return new DoubleIntegerState(this, 2);
                case 95:
                    return new MillsState(this);
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
                    return new ObjectState(this, HessianDebugState.access$408(HessianDebugState.this), ch2 - 96);
                case 112:
                case Opcodes.LREM /* 113 */:
                case Opcodes.FREM /* 114 */:
                case 115:
                case 116:
                case Opcodes.LNEG /* 117 */:
                case Opcodes.FNEG /* 118 */:
                case Opcodes.DNEG /* 119 */:
                    return new CompactListState(this, HessianDebugState.access$408(HessianDebugState.this), true, ch2 - 112);
                case 120:
                case Opcodes.LSHL /* 121 */:
                case 122:
                case 123:
                case 124:
                case 125:
                case 126:
                case 127:
                    return new CompactListState(this, HessianDebugState.access$408(HessianDebugState.this), false, ch2 - 120);
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
                    Integer value2 = new Integer(ch2 - 144);
                    if (isShift(value2)) {
                        return shift(value2);
                    }
                    printObject(value2.toString());
                    return this;
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
                    return new IntegerState(this, "int", ch2 - 200, 3);
                case 208:
                case 209:
                case 210:
                case 211:
                case Hessian2Constants.BC_INT_SHORT_ZERO /* 212 */:
                case 213:
                case 214:
                case 215:
                    return new IntegerState(this, "int", ch2 - Hessian2Constants.BC_INT_SHORT_ZERO, 2);
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
                    Long value3 = new Long(ch2 - 224);
                    if (isShift(value3)) {
                        return shift(value3);
                    }
                    printObject(value3.toString() + "L");
                    return this;
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
                    return new LongState(this, "long", ch2 - Hessian2Constants.BC_LONG_BYTE_ZERO, 7);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$InitialState.class */
    class InitialState extends State {
        InitialState() {
            super();
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            println();
            if (ch2 == 114) {
                return new ReplyState(this);
            }
            if (ch2 == 99) {
                return new CallState(this);
            }
            return nextObject(ch2);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$Top2State.class */
    class Top2State extends State {
        Top2State() {
            super();
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            println();
            if (ch2 == 82) {
                return new Reply2State(this);
            }
            if (ch2 == 70) {
                return new Fault2State(this);
            }
            if (ch2 == 67) {
                return new Call2State(this);
            }
            if (ch2 == 72) {
                return new Hessian2State(this);
            }
            if (ch2 == 114) {
                return new ReplyState(this);
            }
            if (ch2 == 99) {
                return new CallState(this);
            }
            return nextObject(ch2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$IntegerState.class */
    public class IntegerState extends State {
        String _typeCode;
        int _length;
        int _value;

        IntegerState(State next, String typeCode) {
            super(next);
            this._typeCode = typeCode;
        }

        IntegerState(State next, String typeCode, int value, int length) {
            super(next);
            this._typeCode = typeCode;
            this._value = value;
            this._length = length;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            this._value = (256 * this._value) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
            int i = this._length + 1;
            this._length = i;
            if (i == 4) {
                Integer value = new Integer(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$LongState.class */
    public class LongState extends State {
        String _typeCode;
        int _length;
        long _value;

        LongState(State next, String typeCode) {
            super(next);
            this._typeCode = typeCode;
        }

        LongState(State next, String typeCode, long value, int length) {
            super(next);
            this._typeCode = typeCode;
            this._value = value;
            this._length = length;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            this._value = (256 * this._value) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
            int i = this._length + 1;
            this._length = i;
            if (i == 8) {
                Long value = new Long(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                printObject(value.toString() + "L");
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$DoubleIntegerState.class */
    public class DoubleIntegerState extends State {
        int _length;
        int _value;
        boolean _isFirst;

        DoubleIntegerState(State next, int length) {
            super(next);
            this._isFirst = true;
            this._length = length;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            if (this._isFirst) {
                this._value = (byte) ch2;
            } else {
                this._value = (256 * this._value) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
            }
            this._isFirst = false;
            int i = this._length + 1;
            this._length = i;
            if (i == 4) {
                Double value = new Double(this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$RefState.class */
    public class RefState extends State {
        String _typeCode;
        int _length;
        int _value;

        RefState(State next) {
            super(next);
        }

        RefState(State next, String typeCode) {
            super(next);
            this._typeCode = typeCode;
        }

        RefState(State next, String typeCode, int value, int length) {
            super(next);
            this._typeCode = typeCode;
            this._value = value;
            this._length = length;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object o) {
            return true;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object o) {
            println("ref #" + o);
            return this._next;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            return nextObject(ch2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$DateState.class */
    public class DateState extends State {
        int _length;
        long _value;
        boolean _isMinute;

        DateState(State next) {
            super(next);
        }

        DateState(State next, boolean isMinute) {
            super(next);
            this._length = 4;
            this._isMinute = isMinute;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            Date value;
            this._value = (256 * this._value) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
            int i = this._length + 1;
            this._length = i;
            if (i == 8) {
                if (this._isMinute) {
                    value = new Date(this._value * 60000);
                } else {
                    value = new Date(this._value);
                }
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$DoubleState.class */
    public class DoubleState extends State {
        int _length;
        long _value;

        DoubleState(State next) {
            super(next);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            this._value = (256 * this._value) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
            int i = this._length + 1;
            this._length = i;
            if (i == 8) {
                Double value = Double.valueOf(Double.longBitsToDouble(this._value));
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$MillsState.class */
    public class MillsState extends State {
        int _length;
        int _value;

        MillsState(State next) {
            super(next);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            this._value = (256 * this._value) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
            int i = this._length + 1;
            this._length = i;
            if (i == 4) {
                Double value = Double.valueOf(0.001d * this._value);
                if (this._next.isShift(value)) {
                    return this._next.shift(value);
                }
                printObject(value.toString());
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$StringState.class */
    public class StringState extends State {
        private static final int TOP = 0;
        private static final int UTF_2_1 = 1;
        private static final int UTF_3_1 = 2;
        private static final int UTF_3_2 = 3;
        char _typeCode;
        StringBuilder _value;
        int _lengthIndex;
        int _length;
        boolean _isLastChunk;
        int _utfState;
        char _ch;

        StringState(State next, char typeCode, boolean isLastChunk) {
            super(next);
            this._value = new StringBuilder();
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
        }

        StringState(State next, char typeCode, int length) {
            super(next);
            this._value = new StringBuilder();
            this._typeCode = typeCode;
            this._isLastChunk = true;
            this._length = length;
            this._lengthIndex = 2;
        }

        StringState(State next, char typeCode, int length, boolean isLastChunk) {
            super(next);
            this._value = new StringBuilder();
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
            this._length = length;
            this._lengthIndex = 1;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            if (this._lengthIndex < 2) {
                this._length = (256 * this._length) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
                int i = this._lengthIndex + 1;
                this._lengthIndex = i;
                if (i == 2 && this._length == 0 && this._isLastChunk) {
                    if (this._next.isShift(this._value.toString())) {
                        return this._next.shift(this._value.toString());
                    }
                    printObject("\"" + ((Object) this._value) + "\"");
                    return this._next;
                }
                return this;
            }
            if (this._length == 0) {
                if (ch2 == 115 || ch2 == 120) {
                    this._isLastChunk = false;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch2 == 83 || ch2 == 88) {
                    this._isLastChunk = true;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch2 == 0) {
                    if (this._next.isShift(this._value.toString())) {
                        return this._next.shift(this._value.toString());
                    }
                    printObject("\"" + ((Object) this._value) + "\"");
                    return this._next;
                }
                if (0 <= ch2 && ch2 < 32) {
                    this._isLastChunk = true;
                    this._lengthIndex = 2;
                    this._length = ch2 & Const.MAX_ARRAY_DIMENSIONS;
                    return this;
                }
                if (48 <= ch2 && ch2 < 52) {
                    this._isLastChunk = true;
                    this._lengthIndex = 1;
                    this._length = ch2 - 48;
                    return this;
                }
                println(String.valueOf((char) ch2) + ": unexpected character");
                return this._next;
            }
            switch (this._utfState) {
                case 0:
                    if (ch2 < 128) {
                        this._length--;
                        this._value.append((char) ch2);
                        break;
                    } else if (ch2 < 224) {
                        this._ch = (char) ((ch2 & 31) << 6);
                        this._utfState = 1;
                        break;
                    } else {
                        this._ch = (char) ((ch2 & 15) << 12);
                        this._utfState = 2;
                        break;
                    }
                case 1:
                case 3:
                    this._ch = (char) (this._ch + (ch2 & 63));
                    this._value.append(this._ch);
                    this._length--;
                    this._utfState = 0;
                    break;
                case 2:
                    this._ch = (char) (this._ch + ((char) ((ch2 & 63) << 6)));
                    this._utfState = 3;
                    break;
            }
            if (this._length == 0 && this._isLastChunk) {
                if (this._next.isShift(this._value.toString())) {
                    return this._next.shift(this._value.toString());
                }
                printObject("\"" + ((Object) this._value) + "\"");
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$BinaryState.class */
    public class BinaryState extends State {
        char _typeCode;
        int _totalLength;
        int _lengthIndex;
        int _length;
        boolean _isLastChunk;

        BinaryState(State next, char typeCode, boolean isLastChunk) {
            super(next);
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
        }

        BinaryState(State next, char typeCode, int length) {
            super(next);
            this._typeCode = typeCode;
            this._isLastChunk = true;
            this._length = length;
            this._lengthIndex = 2;
        }

        BinaryState(State next, char typeCode, int length, boolean isLastChunk) {
            super(next);
            this._typeCode = typeCode;
            this._isLastChunk = isLastChunk;
            this._length = length;
            this._lengthIndex = 1;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            if (this._lengthIndex < 2) {
                this._length = (256 * this._length) + (ch2 & Const.MAX_ARRAY_DIMENSIONS);
                int i = this._lengthIndex + 1;
                this._lengthIndex = i;
                if (i == 2 && this._length == 0 && this._isLastChunk) {
                    String value = "binary(" + this._totalLength + ")";
                    if (this._next.isShift(value)) {
                        return this._next.shift(value);
                    }
                    printObject(value);
                    return this._next;
                }
                return this;
            }
            if (this._length == 0) {
                if (ch2 == 98) {
                    this._isLastChunk = false;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch2 == 66) {
                    this._isLastChunk = true;
                    this._lengthIndex = 0;
                    return this;
                }
                if (ch2 == 32) {
                    String value2 = "binary(" + this._totalLength + ")";
                    if (this._next.isShift(value2)) {
                        return this._next.shift(value2);
                    }
                    printObject(value2);
                    return this._next;
                }
                if (32 <= ch2 && ch2 < 48) {
                    this._isLastChunk = true;
                    this._lengthIndex = 2;
                    this._length = (ch2 & Const.MAX_ARRAY_DIMENSIONS) - 32;
                    return this;
                }
                println(String.valueOf((char) ch2) + ": unexpected character");
                return this._next;
            }
            this._length--;
            this._totalLength++;
            if (this._length == 0 && this._isLastChunk) {
                String value3 = "binary(" + this._totalLength + ")";
                if (this._next.isShift(value3)) {
                    return this._next.shift(value3);
                }
                printObject(value3);
                return this._next;
            }
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$MapState.class */
    public class MapState extends State {
        private static final int TYPE = 0;
        private static final int KEY = 1;
        private static final int VALUE = 2;
        private int _refId;
        private int _state;
        private int _valueDepth;
        private boolean _hasData;

        MapState(State next, int refId) {
            super(next);
            this._refId = refId;
            this._state = 0;
        }

        MapState(State next, int refId, boolean isType) {
            super(next);
            this._refId = refId;
            if (isType) {
                this._state = 0;
            } else {
                printObject("map (#" + this._refId + ")");
                this._state = 2;
            }
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object value) {
            return this._state == 0;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object type) {
            int iValue;
            if (this._state == 0) {
                if (type instanceof String) {
                    HessianDebugState.this._typeDefList.add((String) type);
                } else if ((type instanceof Integer) && (iValue = ((Integer) type).intValue()) >= 0 && iValue < HessianDebugState.this._typeDefList.size()) {
                    type = HessianDebugState.this._typeDefList.get(iValue);
                }
                printObject("map " + type + " (#" + this._refId + ")");
                this._state = 2;
                return this;
            }
            throw new IllegalStateException();
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            if (this._state == 0) {
                return this._next.depth();
            }
            if (this._state == 1) {
                return this._next.depth() + 2;
            }
            return this._valueDepth;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    return nextObject(ch2);
                case 1:
                    print(" => ");
                    HessianDebugState.this._isObject = false;
                    this._valueDepth = HessianDebugState.this._column;
                    this._state = 2;
                    return nextObject(ch2);
                case 2:
                    if (ch2 == 90) {
                        if (this._hasData) {
                            println();
                        }
                        return this._next;
                    }
                    if (this._hasData) {
                        println();
                    }
                    this._hasData = true;
                    this._state = 1;
                    return nextObject(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$ObjectDefState.class */
    public class ObjectDefState extends State {
        private static final int TYPE = 1;
        private static final int COUNT = 2;
        private static final int FIELD = 3;
        private static final int COMPLETE = 4;
        private int _refId;
        private int _state;
        private boolean _hasData;
        private int _count;
        private String _type;
        private ArrayList<String> _fields;

        ObjectDefState(State next) {
            super(next);
            this._fields = new ArrayList<>();
            this._state = 1;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object value) {
            return true;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object object) {
            if (this._state == 1) {
                this._type = (String) object;
                print("/* defun " + this._type + " [");
                HessianDebugState.this._objectDefList.add(new ObjectDef(this._type, this._fields));
                this._state = 2;
            } else if (this._state == 2) {
                this._count = ((Integer) object).intValue();
                this._state = 3;
            } else if (this._state == 3) {
                String field = (String) object;
                this._count--;
                this._fields.add(field);
                if (this._fields.size() == 1) {
                    print(field);
                } else {
                    print(", " + field);
                }
            } else {
                throw new UnsupportedOperationException();
            }
            return this;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            if (this._state <= 1) {
                return this._next.depth();
            }
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 1:
                    return nextObject(ch2);
                case 2:
                    return nextObject(ch2);
                case 3:
                    if (this._count == 0) {
                        println("] */");
                        this._next.printIndent(0);
                        return this._next.nextObject(ch2);
                    }
                    return nextObject(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$ObjectState.class */
    public class ObjectState extends State {
        private static final int TYPE = 0;
        private static final int FIELD = 1;
        private int _refId;
        private int _state;
        private ObjectDef _def;
        private int _count;
        private int _fieldDepth;

        ObjectState(State next, int refId) {
            super(next);
            this._refId = refId;
            this._state = 0;
        }

        ObjectState(State next, int refId, int def) {
            super(next);
            this._refId = refId;
            this._state = 1;
            if (def < 0 || HessianDebugState.this._objectDefList.size() <= def) {
                throw new IllegalStateException(def + " is an unknown object type");
            }
            this._def = (ObjectDef) HessianDebugState.this._objectDefList.get(def);
            println("object " + this._def.getType() + " (#" + this._refId + ")");
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object value) {
            if (this._state == 0) {
                return true;
            }
            return false;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object object) {
            if (this._state == 0) {
                int def = ((Integer) object).intValue();
                this._def = (ObjectDef) HessianDebugState.this._objectDefList.get(def);
                println("object " + this._def.getType() + " (#" + this._refId + ")");
                this._state = 1;
                if (this._def.getFields().size() == 0) {
                    return this._next;
                }
            }
            return this;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            if (this._state <= 0) {
                return this._next.depth();
            }
            return this._fieldDepth;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    return nextObject(ch2);
                case 1:
                    if (this._def.getFields().size() <= this._count) {
                        return this._next.next(ch2);
                    }
                    this._fieldDepth = this._next.depth() + 2;
                    println();
                    StringBuilder sb = new StringBuilder();
                    ArrayList<String> fields = this._def.getFields();
                    int i = this._count;
                    this._count = i + 1;
                    print(sb.append(fields.get(i)).append(": ").toString());
                    this._fieldDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    return nextObject(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$ListState.class */
    public class ListState extends State {
        private static final int TYPE = 0;
        private static final int LENGTH = 1;
        private static final int VALUE = 2;
        private int _refId;
        private int _state;
        private boolean _hasData;
        private int _count;
        private int _valueDepth;

        ListState(State next, int refId, boolean isType) {
            super(next);
            this._refId = refId;
            if (isType) {
                this._state = 0;
            } else {
                printObject("list (#" + this._refId + ")");
                this._state = 2;
            }
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object value) {
            return this._state == 0 || this._state == 1;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object object) {
            if (this._state == 0) {
                Object type = object;
                if (type instanceof String) {
                    HessianDebugState.this._typeDefList.add((String) type);
                } else if (object instanceof Integer) {
                    int index = ((Integer) object).intValue();
                    type = (index < 0 || index >= HessianDebugState.this._typeDefList.size()) ? "type-unknown(" + index + ")" : HessianDebugState.this._typeDefList.get(index);
                }
                printObject("list " + type + "(#" + this._refId + ")");
                this._state = 2;
                return this;
            }
            if (this._state == 1) {
                this._state = 2;
                return this;
            }
            return this;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            if (this._state <= 1) {
                return this._next.depth();
            }
            if (this._state == 2) {
                return this._valueDepth;
            }
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    return nextObject(ch2);
                case 2:
                    if (ch2 == 90) {
                        if (this._count > 0) {
                            println();
                        }
                        return this._next;
                    }
                    this._valueDepth = this._next.depth() + 2;
                    println();
                    StringBuilder sb = new StringBuilder();
                    int i = this._count;
                    this._count = i + 1;
                    printObject(sb.append(i).append(": ").toString());
                    this._valueDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    return nextObject(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$CompactListState.class */
    public class CompactListState extends State {
        private static final int TYPE = 0;
        private static final int LENGTH = 1;
        private static final int VALUE = 2;
        private int _refId;
        private boolean _isTyped;
        private boolean _isLength;
        private int _state;
        private boolean _hasData;
        private int _length;
        private int _count;
        private int _valueDepth;

        CompactListState(State next, int refId, boolean isTyped) {
            super(next);
            this._isTyped = isTyped;
            this._refId = refId;
            if (isTyped) {
                this._state = 0;
            } else {
                this._state = 1;
            }
        }

        CompactListState(State next, int refId, boolean isTyped, int length) {
            super(next);
            this._isTyped = isTyped;
            this._refId = refId;
            this._length = length;
            this._isLength = true;
            if (isTyped) {
                this._state = 0;
            } else {
                printObject("list (#" + this._refId + ")");
                this._state = 2;
            }
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object value) {
            return this._state == 0 || this._state == 1;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object object) {
            if (this._state == 0) {
                Object type = object;
                if (object instanceof Integer) {
                    int index = ((Integer) object).intValue();
                    type = (index < 0 || index >= HessianDebugState.this._typeDefList.size()) ? "type-unknown(" + index + ")" : HessianDebugState.this._typeDefList.get(index);
                } else if (object instanceof String) {
                    HessianDebugState.this._typeDefList.add((String) object);
                }
                printObject("list " + type + " (#" + this._refId + ")");
                if (this._isLength) {
                    this._state = 2;
                    if (this._length == 0) {
                        return this._next;
                    }
                } else {
                    this._state = 1;
                }
                return this;
            }
            if (this._state == 1) {
                this._length = ((Integer) object).intValue();
                if (!this._isTyped) {
                    printObject("list (#" + this._refId + ")");
                }
                this._state = 2;
                if (this._length == 0) {
                    return this._next;
                }
                return this;
            }
            return this;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            if (this._state <= 1) {
                return this._next.depth();
            }
            if (this._state == 2) {
                return this._valueDepth;
            }
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    return nextObject(ch2);
                case 1:
                    return nextObject(ch2);
                case 2:
                    if (this._length <= this._count) {
                        return this._next.next(ch2);
                    }
                    this._valueDepth = this._next.depth() + 2;
                    println();
                    StringBuilder sb = new StringBuilder();
                    int i = this._count;
                    this._count = i + 1;
                    printObject(sb.append(i).append(": ").toString());
                    this._valueDepth = HessianDebugState.this._column;
                    HessianDebugState.this._isObject = false;
                    return nextObject(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$Hessian2State.class */
    class Hessian2State extends State {
        private static final int MAJOR = 0;
        private static final int MINOR = 1;
        private int _state;
        private int _major;
        private int _minor;

        Hessian2State(State next) {
            super(next);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    this._major = ch2;
                    this._state = 1;
                    return this;
                case 1:
                    this._minor = ch2;
                    println(-2, "hessian " + this._major + "." + this._minor);
                    return this._next;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$CallState.class */
    class CallState extends State {
        private static final int MAJOR = 0;
        private static final int MINOR = 1;
        private static final int HEADER = 2;
        private static final int METHOD = 3;
        private static final int VALUE = 4;
        private static final int ARG = 5;
        private int _state;
        private int _major;
        private int _minor;

        CallState(State next) {
            super(next);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    this._major = ch2;
                    this._state = 1;
                    return this;
                case 1:
                    this._minor = ch2;
                    this._state = 2;
                    println(-2, "call " + this._major + "." + this._minor);
                    return this;
                case 2:
                    if (ch2 == 72) {
                        println();
                        print("header ");
                        HessianDebugState.this._isObject = false;
                        this._state = 4;
                        return new StringState((State) this, 'H', true);
                    }
                    if (ch2 == 109) {
                        println();
                        print("method ");
                        HessianDebugState.this._isObject = false;
                        this._state = 5;
                        return new StringState((State) this, 'm', true);
                    }
                    println(((char) ch2) + ": unexpected char");
                    return HessianDebugState.this.popStack();
                case 3:
                default:
                    throw new IllegalStateException();
                case 4:
                    print(" => ");
                    HessianDebugState.this._isObject = false;
                    this._state = 2;
                    return nextObject(ch2);
                case 5:
                    if (ch2 == 90) {
                        return this._next;
                    }
                    return nextObject(ch2);
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$Call2State.class */
    class Call2State extends State {
        private static final int METHOD = 0;
        private static final int COUNT = 1;
        private static final int ARG = 2;
        private int _state;
        private int _i;
        private int _count;

        Call2State(State next) {
            super(next);
            this._state = 0;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            return this._next.depth() + 5;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object value) {
            return this._state != 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object object) {
            if (this._state == 0) {
                println(-5, "Call " + object);
                this._state = 1;
                return this;
            }
            if (this._state == 1) {
                Integer count = (Integer) object;
                this._count = count.intValue();
                this._state = 2;
                if (this._count == 0) {
                    return this._next;
                }
                return this;
            }
            return this;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    return nextObject(ch2);
                case 1:
                    return nextObject(ch2);
                case 2:
                    if (this._count <= this._i) {
                        return this._next.next(ch2);
                    }
                    println();
                    StringBuilder sb = new StringBuilder();
                    int i = this._i;
                    this._i = i + 1;
                    print(-3, sb.append(i).append(": ").toString());
                    return nextObject(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$ReplyState.class */
    class ReplyState extends State {
        private static final int MAJOR = 0;
        private static final int MINOR = 1;
        private static final int HEADER = 2;
        private static final int VALUE = 3;
        private static final int END = 4;
        private int _state;
        private int _major;
        private int _minor;

        ReplyState(State next) {
            super();
            this._next = next;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    if (ch2 == 116 || ch2 == 83) {
                        return new RemoteState(this).next(ch2);
                    }
                    this._major = ch2;
                    this._state = 1;
                    return this;
                case 1:
                    this._minor = ch2;
                    this._state = 2;
                    println(-2, "reply " + this._major + "." + this._minor);
                    return this;
                case 2:
                    if (ch2 == 72) {
                        this._state = 3;
                        return new StringState((State) this, 'H', true);
                    }
                    if (ch2 == 102) {
                        print("fault ");
                        HessianDebugState.this._isObject = false;
                        this._state = 4;
                        return new MapState(this, 0);
                    }
                    this._state = 4;
                    return nextObject(ch2);
                case 3:
                    this._state = 2;
                    return nextObject(ch2);
                case 4:
                    println();
                    if (ch2 == 90) {
                        return this._next;
                    }
                    return this._next.next(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$Reply2State.class */
    class Reply2State extends State {
        Reply2State(State next) {
            super(next);
            println(-2, "Reply");
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            return nextObject(ch2);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$Fault2State.class */
    class Fault2State extends State {
        Fault2State(State next) {
            super(next);
            println(-2, "Fault");
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        int depth() {
            return this._next.depth() + 2;
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            return nextObject(ch2);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$IndirectState.class */
    class IndirectState extends State {
        IndirectState(State next) {
            super(next);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        boolean isShift(Object object) {
            return this._next.isShift(object);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State shift(Object object) {
            return this._next.shift(object);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            return nextObject(ch2);
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$RemoteState.class */
    class RemoteState extends State {
        private static final int TYPE = 0;
        private static final int VALUE = 1;
        private static final int END = 2;
        private int _state;
        private int _major;
        private int _minor;

        RemoteState(State next) {
            super(next);
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            switch (this._state) {
                case 0:
                    println(-1, EjbRef.REMOTE);
                    if (ch2 == 116) {
                        this._state = 1;
                        return new StringState((State) this, 't', false);
                    }
                    this._state = 2;
                    return nextObject(ch2);
                case 1:
                    this._state = 2;
                    return this._next.nextObject(ch2);
                case 2:
                    return this._next.next(ch2);
                default:
                    throw new IllegalStateException();
            }
        }
    }

    /* loaded from: agent.jar:BOOT-INF/lib/hessian-lite-3.2.13.jar:com/alibaba/com/caucho/hessian/io/HessianDebugState$StreamingState.class */
    class StreamingState extends State {
        private int _digit;
        private int _length;
        private boolean _isLast;
        private boolean _isFirst;
        private State _childState;

        StreamingState(State next, boolean isLast) {
            super(next);
            this._isFirst = true;
            this._isLast = isLast;
            this._childState = new InitialState();
        }

        @Override // com.alibaba.com.caucho.hessian.io.HessianDebugState.State
        State next(int ch2) {
            if (this._digit < 2) {
                this._length = (256 * this._length) + ch2;
                this._digit++;
                if (this._digit == 2 && this._length == 0 && this._isLast) {
                    HessianDebugState.this._refId = 0;
                    return this._next;
                }
                if (this._digit == 2) {
                    println(-1, "packet-start(" + this._length + ")");
                }
                return this;
            }
            if (this._length == 0) {
                this._isLast = ch2 == 80;
                this._digit = 0;
                return this;
            }
            this._childState = this._childState.next(ch2);
            this._length--;
            if (this._length == 0 && this._isLast) {
                println(-1, "");
                println(-1, "packet-end");
                HessianDebugState.this._refId = 0;
                return this._next;
            }
            return this;
        }
    }
}
