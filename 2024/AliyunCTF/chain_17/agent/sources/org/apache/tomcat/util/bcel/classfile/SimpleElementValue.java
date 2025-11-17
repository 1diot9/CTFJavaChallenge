package org.apache.tomcat.util.bcel.classfile;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/SimpleElementValue.class */
public class SimpleElementValue extends ElementValue {
    private final int index;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SimpleElementValue(int type, int index, ConstantPool cpool) {
        super(type, cpool);
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override // org.apache.tomcat.util.bcel.classfile.ElementValue
    public String stringifyValue() {
        ConstantPool cpool = super.getConstantPool();
        int type = super.getType();
        switch (type) {
            case 66:
                ConstantInteger b = cpool.getConstantInteger(getIndex());
                return Integer.toString(b.getBytes());
            case 67:
                ConstantInteger ch2 = cpool.getConstantInteger(getIndex());
                return String.valueOf((char) ch2.getBytes());
            case 68:
                ConstantDouble d = (ConstantDouble) cpool.getConstant(getIndex(), (byte) 6);
                return Double.toString(d.getBytes());
            case 70:
                ConstantFloat f = (ConstantFloat) cpool.getConstant(getIndex(), (byte) 4);
                return Float.toString(f.getBytes());
            case 73:
                return Integer.toString(cpool.getConstantInteger(getIndex()).getBytes());
            case 74:
                ConstantLong j = (ConstantLong) cpool.getConstant(getIndex(), (byte) 5);
                return Long.toString(j.getBytes());
            case 83:
                ConstantInteger s = cpool.getConstantInteger(getIndex());
                return Integer.toString(s.getBytes());
            case 90:
                ConstantInteger bo = cpool.getConstantInteger(getIndex());
                if (bo.getBytes() == 0) {
                    return "false";
                }
                return "true";
            case 115:
                return cpool.getConstantUtf8(getIndex()).getBytes();
            default:
                throw new IllegalStateException("SimpleElementValue class does not know how to stringify type " + type);
        }
    }
}
