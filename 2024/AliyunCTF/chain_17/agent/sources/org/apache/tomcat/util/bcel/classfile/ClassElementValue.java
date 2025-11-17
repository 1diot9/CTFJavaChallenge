package org.apache.tomcat.util.bcel.classfile;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/ClassElementValue.class */
public class ClassElementValue extends ElementValue {
    private final int idx;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ClassElementValue(int type, int idx, ConstantPool cpool) {
        super(type, cpool);
        this.idx = idx;
    }

    @Override // org.apache.tomcat.util.bcel.classfile.ElementValue
    public String stringifyValue() {
        return super.getConstantPool().getConstantUtf8(this.idx).getBytes();
    }
}
