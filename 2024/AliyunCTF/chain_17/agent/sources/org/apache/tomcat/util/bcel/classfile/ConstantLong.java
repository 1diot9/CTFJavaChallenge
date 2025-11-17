package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/ConstantLong.class */
public final class ConstantLong extends Constant {
    private final long bytes;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ConstantLong(DataInput file) throws IOException {
        super((byte) 5);
        this.bytes = file.readLong();
    }

    public long getBytes() {
        return this.bytes;
    }
}
