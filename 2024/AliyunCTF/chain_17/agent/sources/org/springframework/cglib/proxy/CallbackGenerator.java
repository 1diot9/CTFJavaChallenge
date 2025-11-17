package org.springframework.cglib.proxy;

import java.util.List;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.Signature;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/CallbackGenerator.class */
interface CallbackGenerator {

    /* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/CallbackGenerator$Context.class */
    public interface Context {
        ClassLoader getClassLoader();

        CodeEmitter beginMethod(ClassEmitter ce, MethodInfo method);

        int getOriginalModifiers(MethodInfo method);

        int getIndex(MethodInfo method);

        void emitCallback(CodeEmitter ce, int index);

        Signature getImplSignature(MethodInfo method);

        void emitLoadArgsAndInvoke(CodeEmitter e, MethodInfo method);
    }

    void generate(ClassEmitter ce, Context context, List methods) throws Exception;

    void generateStatic(CodeEmitter e, Context context, List methods) throws Exception;
}
