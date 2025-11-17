package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/ElementValue.class */
public abstract class ElementValue {
    public static final byte STRING = 115;
    public static final byte ENUM_CONSTANT = 101;
    public static final byte CLASS = 99;
    public static final byte ANNOTATION = 64;
    public static final byte ARRAY = 91;
    public static final byte PRIMITIVE_INT = 73;
    public static final byte PRIMITIVE_BYTE = 66;
    public static final byte PRIMITIVE_CHAR = 67;
    public static final byte PRIMITIVE_DOUBLE = 68;
    public static final byte PRIMITIVE_FLOAT = 70;
    public static final byte PRIMITIVE_LONG = 74;
    public static final byte PRIMITIVE_SHORT = 83;
    public static final byte PRIMITIVE_BOOLEAN = 90;
    private final int type;
    private final ConstantPool cpool;

    public abstract String stringifyValue();

    public static ElementValue readElementValue(DataInput input, ConstantPool cpool) throws IOException {
        return readElementValue(input, cpool, 0);
    }

    public static ElementValue readElementValue(DataInput input, ConstantPool cpool, int arrayNesting) throws IOException {
        byte tag = input.readByte();
        switch (tag) {
            case 64:
                return new AnnotationElementValue(64, new AnnotationEntry(input, cpool), cpool);
            case 65:
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
            case TypeReference.CAST /* 71 */:
            case 72:
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 77:
            case 78:
            case Opcodes.IASTORE /* 79 */:
            case 80:
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case Opcodes.BASTORE /* 84 */:
            case Opcodes.CASTORE /* 85 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 88:
            case Opcodes.DUP /* 89 */:
            case 92:
            case 93:
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case 96:
            case 97:
            case Opcodes.FADD /* 98 */:
            case 100:
            case Opcodes.FSUB /* 102 */:
            case Opcodes.DSUB /* 103 */:
            case 104:
            case Opcodes.LMUL /* 105 */:
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case 108:
            case Opcodes.LDIV /* 109 */:
            case Opcodes.FDIV /* 110 */:
            case Opcodes.DDIV /* 111 */:
            case 112:
            case Opcodes.LREM /* 113 */:
            case Opcodes.FREM /* 114 */:
            default:
                throw new ClassFormatException("Unexpected element value kind in annotation: " + tag);
            case 66:
            case 67:
            case 68:
            case 70:
            case 73:
            case 74:
            case 83:
            case 90:
            case 115:
                return new SimpleElementValue(tag, input.readUnsignedShort(), cpool);
            case 91:
                int arrayNesting2 = arrayNesting + 1;
                if (arrayNesting2 > 255) {
                    throw new ClassFormatException(String.format("Arrays are only valid if they represent %,d or fewer dimensions.", 255));
                }
                int numArrayVals = input.readUnsignedShort();
                ElementValue[] evalues = new ElementValue[numArrayVals];
                for (int j = 0; j < numArrayVals; j++) {
                    evalues[j] = readElementValue(input, cpool, arrayNesting2);
                }
                return new ArrayElementValue(91, evalues, cpool);
            case 99:
                return new ClassElementValue(99, input.readUnsignedShort(), cpool);
            case 101:
                input.readUnsignedShort();
                return new EnumElementValue(101, input.readUnsignedShort(), cpool);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ElementValue(int type, ConstantPool cpool) {
        this.type = type;
        this.cpool = cpool;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final ConstantPool getConstantPool() {
        return this.cpool;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getType() {
        return this.type;
    }
}
