package org.springframework.cglib.transform;

import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.cglib.core.ClassTransformer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/ClassTransformerChain.class */
public class ClassTransformerChain extends AbstractClassTransformer {
    private ClassTransformer[] chain;

    public ClassTransformerChain(ClassTransformer[] chain) {
        this.chain = (ClassTransformer[]) chain.clone();
    }

    @Override // org.springframework.cglib.transform.AbstractClassTransformer, org.springframework.cglib.core.ClassTransformer
    public void setTarget(ClassVisitor v) {
        super.setTarget(this.chain[0]);
        ClassVisitor next = v;
        for (int i = this.chain.length - 1; i >= 0; i--) {
            this.chain[i].setTarget(next);
            next = this.chain[i];
        }
    }

    @Override // org.springframework.asm.ClassVisitor
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return this.cv.visitMethod(access, name, desc, signature, exceptions);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassTransformerChain{");
        for (int i = 0; i < this.chain.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.chain[i].toString());
        }
        sb.append("}");
        return sb.toString();
    }
}
