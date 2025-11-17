package org.springframework.cglib.transform;

import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Attribute;
import org.springframework.asm.Handle;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.TypePath;
import org.springframework.cglib.core.Constants;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/MethodVisitorTee.class */
public class MethodVisitorTee extends MethodVisitor {
    private final MethodVisitor mv1;
    private final MethodVisitor mv2;

    public MethodVisitorTee(MethodVisitor mv1, MethodVisitor mv2) {
        super(Constants.ASM_API);
        this.mv1 = mv1;
        this.mv2 = mv2;
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        this.mv1.visitFrame(type, nLocal, local, nStack, stack);
        this.mv2.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitAnnotationDefault() {
        return AnnotationVisitorTee.getInstance(this.mv1.visitAnnotationDefault(), this.mv2.visitAnnotationDefault());
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitAnnotation(desc, visible), this.mv2.visitAnnotation(desc, visible));
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitParameterAnnotation(parameter, desc, visible), this.mv2.visitParameterAnnotation(parameter, desc, visible));
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitAttribute(Attribute attr) {
        this.mv1.visitAttribute(attr);
        this.mv2.visitAttribute(attr);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitCode() {
        this.mv1.visitCode();
        this.mv2.visitCode();
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitInsn(int opcode) {
        this.mv1.visitInsn(opcode);
        this.mv2.visitInsn(opcode);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitIntInsn(int opcode, int operand) {
        this.mv1.visitIntInsn(opcode, operand);
        this.mv2.visitIntInsn(opcode, operand);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitVarInsn(int opcode, int var) {
        this.mv1.visitVarInsn(opcode, var);
        this.mv2.visitVarInsn(opcode, var);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitTypeInsn(int opcode, String desc) {
        this.mv1.visitTypeInsn(opcode, desc);
        this.mv2.visitTypeInsn(opcode, desc);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        this.mv1.visitFieldInsn(opcode, owner, name, desc);
        this.mv2.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        this.mv1.visitMethodInsn(opcode, owner, name, desc);
        this.mv2.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        this.mv1.visitMethodInsn(opcode, owner, name, desc, itf);
        this.mv2.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitJumpInsn(int opcode, Label label) {
        this.mv1.visitJumpInsn(opcode, label);
        this.mv2.visitJumpInsn(opcode, label);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitLabel(Label label) {
        this.mv1.visitLabel(label);
        this.mv2.visitLabel(label);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitLdcInsn(Object cst) {
        this.mv1.visitLdcInsn(cst);
        this.mv2.visitLdcInsn(cst);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitIincInsn(int var, int increment) {
        this.mv1.visitIincInsn(var, increment);
        this.mv2.visitIincInsn(var, increment);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        this.mv1.visitTableSwitchInsn(min, max, dflt, labels);
        this.mv2.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        this.mv1.visitLookupSwitchInsn(dflt, keys, labels);
        this.mv2.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitMultiANewArrayInsn(String desc, int dims) {
        this.mv1.visitMultiANewArrayInsn(desc, dims);
        this.mv2.visitMultiANewArrayInsn(desc, dims);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        this.mv1.visitTryCatchBlock(start, end, handler, type);
        this.mv2.visitTryCatchBlock(start, end, handler, type);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        this.mv1.visitLocalVariable(name, desc, signature, start, end, index);
        this.mv2.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitLineNumber(int line, Label start) {
        this.mv1.visitLineNumber(line, start);
        this.mv2.visitLineNumber(line, start);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitMaxs(int maxStack, int maxLocals) {
        this.mv1.visitMaxs(maxStack, maxLocals);
        this.mv2.visitMaxs(maxStack, maxLocals);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitEnd() {
        this.mv1.visitEnd();
        this.mv2.visitEnd();
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitParameter(String name, int access) {
        this.mv1.visitParameter(name, access);
        this.mv2.visitParameter(name, access);
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitTypeAnnotation(typeRef, typePath, desc, visible), this.mv2.visitTypeAnnotation(typeRef, typePath, desc, visible));
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        this.mv1.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
        this.mv2.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitInsnAnnotation(typeRef, typePath, desc, visible), this.mv2.visitInsnAnnotation(typeRef, typePath, desc, visible));
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitTryCatchAnnotation(typeRef, typePath, desc, visible), this.mv2.visitTryCatchAnnotation(typeRef, typePath, desc, visible));
    }

    @Override // org.springframework.asm.MethodVisitor
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        return AnnotationVisitorTee.getInstance(this.mv1.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible), this.mv2.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible));
    }
}
