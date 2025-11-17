package org.springframework.cglib.transform.impl;

import java.lang.reflect.Constructor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.Block;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/transform/impl/UndeclaredThrowableTransformer.class */
public class UndeclaredThrowableTransformer extends ClassEmitterTransformer {
    private final Type wrapper;

    public UndeclaredThrowableTransformer(Class wrapper) {
        this.wrapper = Type.getType((Class<?>) wrapper);
        boolean found = false;
        Constructor[] cstructs = wrapper.getConstructors();
        int length = cstructs.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            Constructor cstruct = cstructs[i];
            Class[] types = cstruct.getParameterTypes();
            if (types.length != 1 || !types[0].equals(Throwable.class)) {
                i++;
            } else {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException(wrapper + " does not have a single-arg constructor that takes a Throwable");
        }
    }

    @Override // org.springframework.cglib.core.ClassEmitter
    public CodeEmitter begin_method(int access, final Signature sig, final Type[] exceptions) {
        CodeEmitter e = super.begin_method(access, sig, exceptions);
        if (TypeUtils.isAbstract(access) || sig.equals(Constants.SIG_STATIC)) {
            return e;
        }
        return new CodeEmitter(e) { // from class: org.springframework.cglib.transform.impl.UndeclaredThrowableTransformer.1
            private Block handler = begin_block();

            @Override // org.springframework.cglib.core.CodeEmitter, org.springframework.cglib.core.LocalVariablesSorter, org.springframework.asm.MethodVisitor
            public void visitMaxs(int maxStack, int maxLocals) {
                this.handler.end();
                EmitUtils.wrap_undeclared_throwable(this, this.handler, exceptions, UndeclaredThrowableTransformer.this.wrapper);
                super.visitMaxs(maxStack, maxLocals);
            }
        };
    }
}
