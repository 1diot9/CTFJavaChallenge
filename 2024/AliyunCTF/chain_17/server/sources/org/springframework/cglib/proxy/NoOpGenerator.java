package org.springframework.cglib.proxy;

import java.util.Iterator;
import java.util.List;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.proxy.CallbackGenerator;

/* loaded from: server.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/NoOpGenerator.class */
class NoOpGenerator implements CallbackGenerator {
    public static final NoOpGenerator INSTANCE = new NoOpGenerator();

    NoOpGenerator() {
    }

    @Override // org.springframework.cglib.proxy.CallbackGenerator
    public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            MethodInfo method = (MethodInfo) it.next();
            if (TypeUtils.isBridge(method.getModifiers()) || (TypeUtils.isProtected(context.getOriginalModifiers(method)) && TypeUtils.isPublic(method.getModifiers()))) {
                CodeEmitter e = EmitUtils.begin_method(ce, method);
                e.load_this();
                context.emitLoadArgsAndInvoke(e, method);
                e.return_value();
                e.end_method();
            }
        }
    }

    @Override // org.springframework.cglib.proxy.CallbackGenerator
    public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods) {
    }
}
