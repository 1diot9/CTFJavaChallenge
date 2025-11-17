package org.springframework.cglib.transform;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.TypePath;
import org.springframework.cglib.core.Constants;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/ClassVisitorTee.class */
public class ClassVisitorTee extends ClassVisitor {
    private ClassVisitor cv1;
    private ClassVisitor cv2;

    public ClassVisitorTee(ClassVisitor cv1, ClassVisitor cv2) {
        super(Constants.ASM_API);
        this.cv1 = cv1;
        this.cv2 = cv2;
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.cv1.visit(version, access, name, signature, superName, interfaces);
        this.cv2.visit(version, access, name, signature, superName, interfaces);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitEnd() {
        this.cv1.visitEnd();
        this.cv2.visitEnd();
        this.cv2 = null;
        this.cv1 = null;
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.cv1.visitInnerClass(name, outerName, innerName, access);
        this.cv2.visitInnerClass(name, outerName, innerName, access);
    }

    @Override // org.springframework.asm.ClassVisitor
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor fv1 = this.cv1.visitField(access, name, desc, signature, value);
        FieldVisitor fv2 = this.cv2.visitField(access, name, desc, signature, value);
        if (fv1 == null) {
            return fv2;
        }
        if (fv2 == null) {
            return fv1;
        }
        return new FieldVisitorTee(fv1, fv2);
    }

    @Override // org.springframework.asm.ClassVisitor
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv1 = this.cv1.visitMethod(access, name, desc, signature, exceptions);
        MethodVisitor mv2 = this.cv2.visitMethod(access, name, desc, signature, exceptions);
        if (mv1 == null) {
            return mv2;
        }
        if (mv2 == null) {
            return mv1;
        }
        return new MethodVisitorTee(mv1, mv2);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitSource(String source, String debug) {
        this.cv1.visitSource(source, debug);
        this.cv2.visitSource(source, debug);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitOuterClass(String owner, String name, String desc) {
        this.cv1.visitOuterClass(owner, name, desc);
        this.cv2.visitOuterClass(owner, name, desc);
    }

    @Override // org.springframework.asm.ClassVisitor
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.cv1.visitAnnotation(desc, visible), this.cv2.visitAnnotation(desc, visible));
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitAttribute(Attribute attrs) {
        this.cv1.visitAttribute(attrs);
        this.cv2.visitAttribute(attrs);
    }

    @Override // org.springframework.asm.ClassVisitor
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.cv1.visitTypeAnnotation(typeRef, typePath, desc, visible), this.cv2.visitTypeAnnotation(typeRef, typePath, desc, visible));
    }
}
