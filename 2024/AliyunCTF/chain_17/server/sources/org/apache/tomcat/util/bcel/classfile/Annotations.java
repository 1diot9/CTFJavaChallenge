package org.apache.tomcat.util.bcel.classfile;

import java.io.DataInput;
import java.io.IOException;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/Annotations.class */
public class Annotations {
    static final Annotations[] EMPTY_ARRAY = new Annotations[0];
    private final AnnotationEntry[] annotationTable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Annotations(DataInput input, ConstantPool constantPool) throws IOException {
        int annotationTableLength = input.readUnsignedShort();
        this.annotationTable = new AnnotationEntry[annotationTableLength];
        for (int i = 0; i < annotationTableLength; i++) {
            this.annotationTable[i] = new AnnotationEntry(input, constantPool);
        }
    }

    public AnnotationEntry[] getAnnotationEntries() {
        return this.annotationTable;
    }
}
