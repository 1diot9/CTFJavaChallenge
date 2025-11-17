package org.springframework.cglib.transform;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.TypePath;
import org.springframework.cglib.core.Constants;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/FieldVisitorTee.class */
public class FieldVisitorTee extends FieldVisitor {
    private FieldVisitor fv1;
    private FieldVisitor fv2;

    public FieldVisitorTee(FieldVisitor fv1, FieldVisitor fv2) {
        super(Constants.ASM_API);
        this.fv1 = fv1;
        this.fv2 = fv2;
    }

    @Override // org.springframework.asm.FieldVisitor
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.fv1.visitAnnotation(desc, visible), this.fv2.visitAnnotation(desc, visible));
    }

    @Override // org.springframework.asm.FieldVisitor
    public void visitAttribute(Attribute attr) {
        this.fv1.visitAttribute(attr);
        this.fv2.visitAttribute(attr);
    }

    @Override // org.springframework.asm.FieldVisitor
    public void visitEnd() {
        this.fv1.visitEnd();
        this.fv2.visitEnd();
    }

    @Override // org.springframework.asm.FieldVisitor
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.fv1.visitTypeAnnotation(typeRef, typePath, desc, visible), this.fv2.visitTypeAnnotation(typeRef, typePath, desc, visible));
    }
}
