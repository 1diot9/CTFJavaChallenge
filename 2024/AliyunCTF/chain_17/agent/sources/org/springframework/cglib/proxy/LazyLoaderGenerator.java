package org.springframework.cglib.proxy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.springframework.asm.Label;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Constants;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.proxy.CallbackGenerator;

/* loaded from: agent.jar:BOOT-INF/lib/spring-core-6.1.3.jar:org/springframework/cglib/proxy/LazyLoaderGenerator.class */
class LazyLoaderGenerator implements CallbackGenerator {
    public static final LazyLoaderGenerator INSTANCE = new LazyLoaderGenerator();
    private static final Signature LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
    private static final Type LAZY_LOADER = TypeUtils.parseType("org.springframework.cglib.proxy.LazyLoader");

    LazyLoaderGenerator() {
    }

    @Override // org.springframework.cglib.proxy.CallbackGenerator
    public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
        Set indexes = new HashSet();
        Iterator it = methods.iterator();
        while (it.hasNext()) {
            MethodInfo method = (MethodInfo) it.next();
            if (!TypeUtils.isProtected(method.getModifiers())) {
                int index = context.getIndex(method);
                indexes.add(Integer.valueOf(index));
                CodeEmitter e = context.beginMethod(ce, method);
                e.load_this();
                e.dup();
                e.invoke_virtual_this(loadMethod(index));
                e.checkcast(method.getClassInfo().getType());
                e.load_args();
                e.invoke(method);
                e.return_value();
                e.end_method();
            }
        }
        Iterator it2 = indexes.iterator();
        while (it2.hasNext()) {
            int index2 = ((Integer) it2.next()).intValue();
            String delegate = "CGLIB$LAZY_LOADER_" + index2;
            ce.declare_field(2, delegate, Constants.TYPE_OBJECT, null);
            CodeEmitter e2 = ce.begin_method(50, loadMethod(index2), null);
            e2.load_this();
            e2.getfield(delegate);
            e2.dup();
            Label end = e2.make_label();
            e2.ifnonnull(end);
            e2.pop();
            e2.load_this();
            context.emitCallback(e2, index2);
            e2.invoke_interface(LAZY_LOADER, LOAD_OBJECT);
            e2.dup_x1();
            e2.putfield(delegate);
            e2.mark(end);
            e2.return_value();
            e2.end_method();
        }
    }

    private Signature loadMethod(int index) {
        return new Signature("CGLIB$LOAD_PRIVATE_" + index, Constants.TYPE_OBJECT, Constants.TYPES_EMPTY);
    }

    @Override // org.springframework.cglib.proxy.CallbackGenerator
    public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods) {
    }
}
