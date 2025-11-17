package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/AnnotationEntry.class */
public class AnnotationEntry {
    static final AnnotationEntry[] EMPTY_ARRAY = new AnnotationEntry[0];
    private final int typeIndex;
    private final ConstantPool constantPool;
    private final List<ElementValuePair> elementValuePairs;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationEntry(DataInput input, ConstantPool constantPool) throws IOException {
        this.constantPool = constantPool;
        this.typeIndex = input.readUnsignedShort();
        int numElementValuePairs = input.readUnsignedShort();
        this.elementValuePairs = new ArrayList(numElementValuePairs);
        for (int i = 0; i < numElementValuePairs; i++) {
            this.elementValuePairs.add(new ElementValuePair(input, constantPool));
        }
    }

    public String getAnnotationType() {
        return this.constantPool.getConstantUtf8(this.typeIndex).getBytes();
    }

    public List<ElementValuePair> getElementValuePairs() {
        return this.elementValuePairs;
    }
}
