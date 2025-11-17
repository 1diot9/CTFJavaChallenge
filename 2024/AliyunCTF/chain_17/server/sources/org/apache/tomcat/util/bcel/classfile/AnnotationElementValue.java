package org.apache.tomcat.util.bcel.classfile;

/* loaded from: server.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/AnnotationElementValue.class */
public class AnnotationElementValue extends ElementValue {
    private final AnnotationEntry annotationEntry;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnnotationElementValue(int type, AnnotationEntry annotationEntry, ConstantPool cpool) {
        super(type, cpool);
        if (type != 64) {
            throw new ClassFormatException("Only element values of type annotation can be built with this ctor - type specified: " + type);
        }
        this.annotationEntry = annotationEntry;
    }

    public AnnotationEntry getAnnotationEntry() {
        return this.annotationEntry;
    }

    @Override // org.apache.tomcat.util.bcel.classfile.ElementValue
    public String stringifyValue() {
        return this.annotationEntry.toString();
    }
}
