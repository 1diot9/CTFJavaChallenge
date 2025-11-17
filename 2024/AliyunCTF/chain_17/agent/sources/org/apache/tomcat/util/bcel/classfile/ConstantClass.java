package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/ConstantClass.class */
public final class ConstantClass extends Constant {
    private final int nameIndex;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstantClass(DataInput dataInput) throws IOException {
        super((byte) 7);
        this.nameIndex = dataInput.readUnsignedShort();
    }

    public int getNameIndex() {
        return this.nameIndex;
    }
}
