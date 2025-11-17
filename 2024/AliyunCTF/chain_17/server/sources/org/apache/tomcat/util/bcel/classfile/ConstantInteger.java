package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/ConstantInteger.class */
public final class ConstantInteger extends Constant {
    private final int bytes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstantInteger(DataInput file) throws IOException {
        super((byte) 3);
        this.bytes = file.readInt();
    }

    public int getBytes() {
        return this.bytes;
    }
}
