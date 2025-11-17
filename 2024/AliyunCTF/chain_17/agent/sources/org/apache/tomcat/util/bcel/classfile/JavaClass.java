package org.apache.tomcat.util.bcel.classfile;

import java.util.HashMap;
import java.util.List;

/* loaded from: agent.jar:BOOT-INF/lib/tomcat-embed-core-10.1.18.jar:org/apache/tomcat/util/bcel/classfile/JavaClass.class */
public class JavaClass {
    private final int accessFlags;
    private final String className;
    private final String superclassName;
    private final String[] interfaceNames;
    private final Annotations runtimeVisibleAnnotations;
    private final List<Annotations> runtimeVisibleFieldOrMethodAnnotations;

    /* JADX INFO: Access modifiers changed from: package-private */
    public JavaClass(String className, String superclassName, int accessFlags, ConstantPool constantPool, String[] interfaceNames, Annotations runtimeVisibleAnnotations, List<Annotations> runtimeVisibleFieldOrMethodAnnotations) {
        this.accessFlags = accessFlags;
        this.runtimeVisibleAnnotations = runtimeVisibleAnnotations;
        this.runtimeVisibleFieldOrMethodAnnotations = runtimeVisibleFieldOrMethodAnnotations;
        this.className = className;
        this.superclassName = superclassName;
        this.interfaceNames = interfaceNames;
    }

    public final int getAccessFlags() {
        return this.accessFlags;
    }

    public AnnotationEntry[] getAllAnnotationEntries() {
        HashMap<String, AnnotationEntry> annotationEntries = new HashMap<>();
        if (this.runtimeVisibleAnnotations != null) {
            for (AnnotationEntry annotationEntry : this.runtimeVisibleAnnotations.getAnnotationEntries()) {
                annotationEntries.put(annotationEntry.getAnnotationType(), annotationEntry);
            }
        }
        if (this.runtimeVisibleFieldOrMethodAnnotations != null) {
            for (Annotations annotations : (Annotations[]) this.runtimeVisibleFieldOrMethodAnnotations.toArray(Annotations.EMPTY_ARRAY)) {
                for (AnnotationEntry annotationEntry2 : annotations.getAnnotationEntries()) {
                    annotationEntries.putIfAbsent(annotationEntry2.getAnnotationType(), annotationEntry2);
                }
            }
        }
        if (annotationEntries.isEmpty()) {
            return null;
        }
        return (AnnotationEntry[]) annotationEntries.values().toArray(AnnotationEntry.EMPTY_ARRAY);
    }

    public AnnotationEntry[] getAnnotationEntries() {
        if (this.runtimeVisibleAnnotations != null) {
            return this.runtimeVisibleAnnotations.getAnnotationEntries();
        }
        return null;
    }

    public String getClassName() {
        return this.className;
    }

    public String[] getInterfaceNames() {
        return this.interfaceNames;
    }

    public String getSuperclassName() {
        return this.superclassName;
    }
}
