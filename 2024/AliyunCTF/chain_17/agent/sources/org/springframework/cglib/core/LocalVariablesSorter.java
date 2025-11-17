package org.springframework.cglib.core;

import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Type;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/LocalVariablesSorter.class */
public class LocalVariablesSorter extends MethodVisitor {
    protected final int firstLocal;
    private final State state;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/core/LocalVariablesSorter$State.class */
    public static class State {
        int[] mapping = new int[40];
        int nextLocal;

        private State() {
        }
    }

    public LocalVariablesSorter(final int access, final String desc, final MethodVisitor mv) {
        super(Constants.ASM_API, mv);
        this.state = new State();
        Type[] args = Type.getArgumentTypes(desc);
        this.state.nextLocal = (8 & access) != 0 ? 0 : 1;
        for (Type arg : args) {
            this.state.nextLocal += arg.getSize();
        }
        this.firstLocal = this.state.nextLocal;
    }

    public LocalVariablesSorter(LocalVariablesSorter lvs) {
        super(Constants.ASM_API, lvs.mv);
        this.state = lvs.state;
        this.firstLocal = lvs.firstLocal;
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitVarInsn(final int opcode, final int var) {
        int i;
        switch (opcode) {
            case 22:
            case 24:
            case 55:
            case 57:
                i = 2;
                break;
            default:
                i = 1;
                break;
        }
        int size = i;
        this.mv.visitVarInsn(opcode, remap(var, size));
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitIincInsn(final int var, final int increment) {
        this.mv.visitIincInsn(remap(var, 1), increment);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitMaxs(final int maxStack, final int maxLocals) {
        this.mv.visitMaxs(maxStack, this.state.nextLocal);
    }

    @Override // org.springframework.asm.MethodVisitor
    public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
        this.mv.visitLocalVariable(name, desc, signature, start, end, remap(index));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int newLocal(final int size) {
        int var = this.state.nextLocal;
        this.state.nextLocal += size;
        return var;
    }

    private int remap(final int var, final int size) {
        if (var < this.firstLocal) {
            return var;
        }
        int key = ((2 * var) + size) - 1;
        int length = this.state.mapping.length;
        if (key >= length) {
            int[] newMapping = new int[Math.max(2 * length, key + 1)];
            System.arraycopy(this.state.mapping, 0, newMapping, 0, length);
            this.state.mapping = newMapping;
        }
        int value = this.state.mapping[key];
        if (value == 0) {
            value = this.state.nextLocal + 1;
            this.state.mapping[key] = value;
            this.state.nextLocal += size;
        }
        return value - 1;
    }

    private int remap(final int var) {
        if (var < this.firstLocal) {
            return var;
        }
        int key = 2 * var;
        int value = key < this.state.mapping.length ? this.state.mapping[key] : 0;
        if (value == 0) {
            value = key + 1 < this.state.mapping.length ? this.state.mapping[key + 1] : 0;
        }
        if (value == 0) {
            throw new IllegalStateException("Unknown local variable " + var);
        }
        return value - 1;
    }
}
