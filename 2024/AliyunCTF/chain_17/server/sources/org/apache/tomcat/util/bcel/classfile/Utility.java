package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/Utility.class */
public final class Utility {
    static String compactClassName(String str) {
        return str.replace('/', '.');
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getClassName(ConstantPool constantPool, int index) {
        Constant c = constantPool.getConstant(index, (byte) 7);
        int i = ((ConstantClass) c).getNameIndex();
        Constant c2 = constantPool.getConstant(i, (byte) 1);
        String name = ((ConstantUtf8) c2).getBytes();
        return compactClassName(name);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void skipFully(DataInput file, int length) throws IOException {
        int total = file.skipBytes(length);
        if (total != length) {
            throw new EOFException();
        }
    }

    private Utility() {
    }
}
