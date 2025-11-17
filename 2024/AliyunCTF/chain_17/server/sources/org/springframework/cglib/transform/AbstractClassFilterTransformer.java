package org.springframework.cglib.transform;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.core.ClassTransformer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/AbstractClassFilterTransformer.class */
public abstract class AbstractClassFilterTransformer extends AbstractClassTransformer {
    private ClassTransformer pass;
    private ClassVisitor target;

    protected abstract boolean accept(int version, int access, String name, String signature, String superName, String[] interfaces);

    @Override // org.springframework.cglib.transform.AbstractClassTransformer, org.springframework.cglib.core.ClassTransformer
    public void setTarget(ClassVisitor target) {
        super.setTarget(target);
        this.pass.setTarget(target);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractClassFilterTransformer(ClassTransformer pass) {
        this.pass = pass;
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.target = accept(version, access, name, signature, superName, interfaces) ? this.pass : this.cv;
        this.target.visit(version, access, name, signature, superName, interfaces);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitSource(String source, String debug) {
        this.target.visitSource(source, debug);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitOuterClass(String owner, String name, String desc) {
        this.target.visitOuterClass(owner, name, desc);
    }

    @Override // org.springframework.asm.ClassVisitor
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return this.target.visitAnnotation(desc, visible);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitAttribute(Attribute attr) {
        this.target.visitAttribute(attr);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        this.target.visitInnerClass(name, outerName, innerName, access);
    }

    @Override // org.springframework.asm.ClassVisitor
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return this.target.visitField(access, name, desc, signature, value);
    }

    @Override // org.springframework.asm.ClassVisitor
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return this.target.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override // org.springframework.asm.ClassVisitor
    public void visitEnd() {
        this.target.visitEnd();
        this.target = null;
    }
}
