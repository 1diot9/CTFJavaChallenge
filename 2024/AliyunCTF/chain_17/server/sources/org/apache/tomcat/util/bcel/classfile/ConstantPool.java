package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import org.apache.tomcat.util.bcel.Const;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/ConstantPool.class */
public class ConstantPool {
    private final Constant[] constantPool;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstantPool(DataInput input) throws IOException, ClassFormatException {
        byte tag;
        int constantPoolCount = input.readUnsignedShort();
        this.constantPool = new Constant[constantPoolCount];
        int i = 1;
        while (i < constantPoolCount) {
            this.constantPool[i] = Constant.readConstant(input);
            if (this.constantPool[i] != null && ((tag = this.constantPool[i].getTag()) == 6 || tag == 5)) {
                i++;
            }
            i++;
        }
    }

    public <T extends Constant> T getConstant(int i) throws ClassFormatException {
        return (T) getConstant(i, Constant.class);
    }

    public <T extends Constant> T getConstant(int i, byte b) throws ClassFormatException {
        T t = (T) getConstant(i);
        if (t.getTag() != b) {
            throw new ClassFormatException("Expected class '" + Const.getConstantName(b) + "' at index " + i + " and got " + t);
        }
        return t;
    }

    public <T extends Constant> T getConstant(int index, Class<T> castTo) throws ClassFormatException {
        Constant prev;
        if (index >= this.constantPool.length || index < 1) {
            throw new ClassFormatException("Invalid constant pool reference using index: " + index + ". Constant pool size is: " + this.constantPool.length);
        }
        if (this.constantPool[index] != null && !castTo.isAssignableFrom(this.constantPool[index].getClass())) {
            throw new ClassFormatException("Invalid constant pool reference at index: " + index + ". Expected " + castTo + " but was " + this.constantPool[index].getClass());
        }
        if (index > 1 && (prev = this.constantPool[index - 1]) != null && (prev.getTag() == 6 || prev.getTag() == 5)) {
            throw new ClassFormatException("Constant pool at index " + index + " is invalid. The index is unused due to the preceeding " + Const.getConstantName(prev.getTag()) + ".");
        }
        T c = castTo.cast(this.constantPool[index]);
        if (c == null) {
            throw new ClassFormatException("Constant pool at index " + index + " is null.");
        }
        return c;
    }

    public ConstantInteger getConstantInteger(int index) {
        return (ConstantInteger) getConstant(index, (byte) 3);
    }

    public ConstantUtf8 getConstantUtf8(int index) throws ClassFormatException {
        return (ConstantUtf8) getConstant(index, (byte) 1);
    }
}
