package org.springframework.asm;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/asm/MethodVisitor.class */
public abstract class MethodVisitor {
    private static final String REQUIRES_ASM5 = "This feature requires ASM5";
    protected final int api;
    protected MethodVisitor mv;

    /* JADX INFO: Access modifiers changed from: protected */
    public MethodVisitor(final int api) {
        this(api, null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MethodVisitor(final int api, final MethodVisitor methodVisitor) {
        if (api != 589824 && api != 524288 && api != 458752 && api != 393216 && api != 327680 && api != 262144 && api != 17432576) {
            throw new IllegalArgumentException("Unsupported api " + api);
        }
        this.api = api;
        this.mv = methodVisitor;
    }

    public MethodVisitor getDelegate() {
        return this.mv;
    }

    public void visitParameter(final String name, final int access) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException(REQUIRES_ASM5);
        }
        if (this.mv != null) {
            this.mv.visitParameter(name, access);
        }
    }

    public AnnotationVisitor visitAnnotationDefault() {
        if (this.mv != null) {
            return this.mv.visitAnnotationDefault();
        }
        return null;
    }

    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        if (this.mv != null) {
            return this.mv.visitAnnotation(descriptor, visible);
        }
        return null;
    }

    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException(REQUIRES_ASM5);
        }
        if (this.mv != null) {
            return this.mv.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
        }
        return null;
    }

    public void visitAnnotableParameterCount(final int parameterCount, final boolean visible) {
        if (this.mv != null) {
            this.mv.visitAnnotableParameterCount(parameterCount, visible);
        }
    }

    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String descriptor, final boolean visible) {
        if (this.mv != null) {
            return this.mv.visitParameterAnnotation(parameter, descriptor, visible);
        }
        return null;
    }

    public void visitAttribute(final Attribute attribute) {
        if (this.mv != null) {
            this.mv.visitAttribute(attribute);
        }
    }

    public void visitCode() {
        if (this.mv != null) {
            this.mv.visitCode();
        }
    }

    public void visitFrame(final int type, final int numLocal, final Object[] local, final int numStack, final Object[] stack) {
        if (this.mv != null) {
            this.mv.visitFrame(type, numLocal, local, numStack, stack);
        }
    }

    public void visitInsn(final int opcode) {
        if (this.mv != null) {
            this.mv.visitInsn(opcode);
        }
    }

    public void visitIntInsn(final int opcode, final int operand) {
        if (this.mv != null) {
            this.mv.visitIntInsn(opcode, operand);
        }
    }

    public void visitVarInsn(final int opcode, final int varIndex) {
        if (this.mv != null) {
            this.mv.visitVarInsn(opcode, varIndex);
        }
    }

    public void visitTypeInsn(final int opcode, final String type) {
        if (this.mv != null) {
            this.mv.visitTypeInsn(opcode, type);
        }
    }

    public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
        if (this.mv != null) {
            this.mv.visitFieldInsn(opcode, owner, name, descriptor);
        }
    }

    @Deprecated
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor) {
        int opcodeAndSource = opcode | (this.api < 327680 ? 256 : 0);
        visitMethodInsn(opcodeAndSource, owner, name, descriptor, opcode == 185);
    }

    public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface) {
        if (this.api < 327680 && (opcode & 256) == 0) {
            if (isInterface != (opcode == 185)) {
                throw new UnsupportedOperationException("INVOKESPECIAL/STATIC on interfaces requires ASM5");
            }
            visitMethodInsn(opcode, owner, name, descriptor);
        } else if (this.mv != null) {
            this.mv.visitMethodInsn(opcode & (-257), owner, name, descriptor, isInterface);
        }
    }

    public void visitInvokeDynamicInsn(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException(REQUIRES_ASM5);
        }
        if (this.mv != null) {
            this.mv.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }
    }

    public void visitJumpInsn(final int opcode, final Label label) {
        if (this.mv != null) {
            this.mv.visitJumpInsn(opcode, label);
        }
    }

    public void visitLabel(final Label label) {
        if (this.mv != null) {
            this.mv.visitLabel(label);
        }
    }

    public void visitLdcInsn(final Object value) {
        if (this.api < 327680 && ((value instanceof Handle) || ((value instanceof Type) && ((Type) value).getSort() == 11))) {
            throw new UnsupportedOperationException(REQUIRES_ASM5);
        }
        if (this.api < 458752 && (value instanceof ConstantDynamic)) {
            throw new UnsupportedOperationException("This feature requires ASM7");
        }
        if (this.mv != null) {
            this.mv.visitLdcInsn(value);
        }
    }

    public void visitIincInsn(final int varIndex, final int increment) {
        if (this.mv != null) {
            this.mv.visitIincInsn(varIndex, increment);
        }
    }

    public void visitTableSwitchInsn(final int min, final int max, final Label dflt, final Label... labels) {
        if (this.mv != null) {
            this.mv.visitTableSwitchInsn(min, max, dflt, labels);
        }
    }

    public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
        if (this.mv != null) {
            this.mv.visitLookupSwitchInsn(dflt, keys, labels);
        }
    }

    public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
        if (this.mv != null) {
            this.mv.visitMultiANewArrayInsn(descriptor, numDimensions);
        }
    }

    public AnnotationVisitor visitInsnAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException(REQUIRES_ASM5);
        }
        if (this.mv != null) {
            return this.mv.visitInsnAnnotation(typeRef, typePath, descriptor, visible);
        }
        return null;
    }

    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        if (this.mv != null) {
            this.mv.visitTryCatchBlock(start, end, handler, type);
        }
    }

    public AnnotationVisitor visitTryCatchAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException(REQUIRES_ASM5);
        }
        if (this.mv != null) {
            return this.mv.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible);
        }
        return null;
    }

    public void visitLocalVariable(final String name, final String descriptor, final String signature, final Label start, final Label end, final int index) {
        if (this.mv != null) {
            this.mv.visitLocalVariable(name, descriptor, signature, start, end, index);
        }
    }

    public AnnotationVisitor visitLocalVariableAnnotation(final int typeRef, final TypePath typePath, final Label[] start, final Label[] end, final int[] index, final String descriptor, final boolean visible) {
        if (this.api < 327680) {
            throw new UnsupportedOperationException(REQUIRES_ASM5);
        }
        if (this.mv != null) {
            return this.mv.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible);
        }
        return null;
    }

    public void visitLineNumber(final int line, final Label start) {
        if (this.mv != null) {
            this.mv.visitLineNumber(line, start);
        }
    }

    public void visitMaxs(final int maxStack, final int maxLocals) {
        if (this.mv != null) {
            this.mv.visitMaxs(maxStack, maxLocals);
        }
    }

    public void visitEnd() {
        if (this.mv != null) {
            this.mv.visitEnd();
        }
    }
}
